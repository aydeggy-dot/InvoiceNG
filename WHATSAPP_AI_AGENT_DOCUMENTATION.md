# WhatsApp AI Sales Agent - Technical Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Implementation Details](#implementation-details)
4. [Issues Resolved](#issues-resolved)
5. [Current Limitations](#current-limitations)
6. [Scaling for Commercial Production](#scaling-for-commercial-production)
7. [Configuration Reference](#configuration-reference)

---

## Overview

### What We Built
An AI-powered WhatsApp sales agent that:
- Receives customer messages via WhatsApp Business API webhooks
- Processes messages using Claude AI (Anthropic) to generate contextual, sales-focused responses
- Manages shopping cart operations (add items, update quantities, apply discounts)
- Handles the complete sales flow: browsing → cart → address → confirmation → payment
- Stores conversation history and state in PostgreSQL
- Sends AI-generated responses back to customers via WhatsApp

### Technology Stack
| Component | Technology |
|-----------|------------|
| Backend Framework | Spring Boot 3.2.1 (Java 17) |
| Database | PostgreSQL 16 with JSONB support |
| AI Model | Claude Sonnet 4 (`claude-sonnet-4-20250514`) |
| Messaging | WhatsApp Business Cloud API (Meta) |
| ORM | Hibernate 6.4.1 with JPA |
| Build Tool | Maven |
| Async Processing | Spring @Async |

---

## Architecture

### High-Level Flow
```
┌─────────────┐     ┌──────────────┐     ┌─────────────────┐
│  Customer   │────▶│  WhatsApp    │────▶│  Meta Cloud     │
│  (Phone)    │◀────│  App         │◀────│  API            │
└─────────────┘     └──────────────┘     └────────┬────────┘
                                                  │
                                                  ▼
┌─────────────────────────────────────────────────────────────┐
│                    INVOICENG BACKEND                        │
│  ┌──────────────────┐    ┌──────────────────────────────┐  │
│  │ WhatsApp Webhook │───▶│ WebhookProcessingService     │  │
│  │ Controller       │    │ (@Async + @Transactional)    │  │
│  └──────────────────┘    └──────────────┬───────────────┘  │
│                                         │                   │
│                                         ▼                   │
│  ┌──────────────────┐    ┌──────────────────────────────┐  │
│  │ Conversation     │◀──▶│ AISalesAgentService          │  │
│  │ Service          │    │ (Claude API Integration)     │  │
│  └────────┬─────────┘    └──────────────────────────────┘  │
│           │                                                 │
│           ▼                                                 │
│  ┌──────────────────┐    ┌──────────────────────────────┐  │
│  │ PostgreSQL       │    │ ConversationStateMachine     │  │
│  │ (Conversations,  │◀──▶│ (Cart Operations, State)     │  │
│  │  Messages, Cart) │    └──────────────────────────────┘  │
│  └──────────────────┘                                      │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

#### 1. WhatsAppWebhookController
**File:** `src/main/java/com/invoiceng/controller/WhatsAppWebhookController.java`

Handles incoming webhooks from Meta:
- **GET endpoint:** Webhook verification (Meta subscription validation)
- **POST endpoint:** Receives all WhatsApp events (messages, status updates)
- Returns 200 OK immediately, processes asynchronously

#### 2. WebhookProcessingService
**File:** `src/main/java/com/invoiceng/service/WebhookProcessingService.java`

Async processing with proper transaction management:
- Uses `@Async` annotation for non-blocking webhook handling
- Uses `@Transactional` for database operations
- Routes messages to appropriate business based on phone_number_id
- Coordinates AI response generation and WhatsApp message sending

#### 3. ConversationService
**File:** `src/main/java/com/invoiceng/service/ConversationService.java`

Manages conversation lifecycle:
- Creates/retrieves conversations per customer-business pair
- Stores inbound/outbound messages
- Handles message deduplication
- Manages conversation state transitions

#### 4. ConversationStateMachine
**File:** `src/main/java/com/invoiceng/service/ConversationStateMachine.java`

Handles cart and order operations:
- Add/remove/update cart items
- Apply discounts (with business-configured limits)
- Set delivery address and calculate fees
- Order confirmation and completion

#### 5. AISalesAgentService
**File:** `src/main/java/com/invoiceng/service/ai/AISalesAgentService.java`

Orchestrates AI-powered responses:
- Builds context from conversation history, products, and business config
- Calls Claude API via ClaudeService
- Parses AI responses for cart operations and state changes
- Handles handoff triggers for human escalation

#### 6. ClaudeService
**File:** `src/main/java/com/invoiceng/service/ai/ClaudeService.java`

Direct integration with Anthropic Claude API:
- Configurable model selection
- Token limit management
- Error handling and logging

---

## Implementation Details

### Database Schema (Key Tables)

#### conversations
```sql
CREATE TABLE conversations (
    id UUID PRIMARY KEY,
    business_id UUID REFERENCES users(id),
    customer_phone VARCHAR(20) NOT NULL,
    customer_name VARCHAR(255),
    customer_whatsapp_id VARCHAR(100),
    state VARCHAR(50) DEFAULT 'greeting',
    context JSONB,
    cart JSONB,  -- Stores OrderContext as JSON
    is_active BOOLEAN DEFAULT true,
    is_handed_off BOOLEAN DEFAULT false,
    handed_off_at TIMESTAMP,
    handed_off_reason TEXT,
    outcome VARCHAR(20),
    order_id UUID,
    message_count INTEGER DEFAULT 0,
    last_message_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(business_id, customer_phone)
);
```

#### conversation_messages
```sql
CREATE TABLE conversation_messages (
    id UUID PRIMARY KEY,
    conversation_id UUID REFERENCES conversations(id),
    direction VARCHAR(10),  -- 'inbound' or 'outbound'
    content TEXT,
    message_type VARCHAR(20),
    whatsapp_message_id VARCHAR(255) UNIQUE,
    intent_detected VARCHAR(100),
    entities_extracted JSONB,
    ai_confidence DECIMAL,
    media_url TEXT,
    created_at TIMESTAMP
);
```

### Conversation States
```java
public enum ConversationState {
    GREETING("greeting"),
    BROWSING("browsing"),
    ADDING_TO_CART("adding_to_cart"),
    COLLECTING_ADDRESS("collecting_address"),
    CONFIRMING_ORDER("confirming_order"),
    AWAITING_PAYMENT("awaiting_payment"),
    COMPLETED("completed"),
    HANDED_OFF("handed_off");
}
```

### Cart Data Structure (OrderContext)
```java
public class OrderContext {
    private List<CartItem> items;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal total;
    private String deliveryAddress;
    private String deliveryArea;
    private boolean confirmed;
    private String invoiceId;
}

public class CartItem {
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercent;
    private BigDecimal lineTotal;
}
```

---

## Issues Resolved

### Issue 1: LazyInitializationException
**Problem:** Hibernate lazy loading failed outside transaction context when accessing `conversation.getBusiness()` in async thread.

**Solution:**
- Created dedicated `WebhookProcessingService` with `@Async` + `@Transactional` annotations
- Enabled async support with `AsyncConfig` class containing `@EnableAsync`
- Ensures all database operations happen within proper transaction boundaries

**Files Modified:**
- Created `WebhookProcessingService.java`
- Created `AsyncConfig.java`

### Issue 2: Claude API Model Not Found
**Problem:** Error `model_not_found` when calling Claude API with incorrect model ID.

**Solution:** Updated `application.yml` to use valid model:
```yaml
claude:
  api-key: ${ANTHROPIC_API_KEY}
  model: claude-sonnet-4-20250514  # Corrected model ID
  max-tokens: 512
```

### Issue 3: Cart Field Type Mismatch (ClassCastException)
**Problem:** `ClassCastException: LinkedHashMap cannot be cast to String` when Hibernate loaded JSONB `cart` field.

**Root Cause:** Cart field was typed as `Object`, causing Hibernate to deserialize JSONB as LinkedHashMap instead of String.

**Solution:** Changed cart field from `Object` to `String`:
```java
// Before (broken)
@Column(name = "cart", columnDefinition = "jsonb")
private Object cart;

// After (fixed)
@JdbcTypeCode(SqlTypes.JSON)
@Column(name = "cart", columnDefinition = "jsonb")
private String cart;
```

**Files Modified:**
- `Conversation.java` - Changed cart field type
- `ConversationStateMachine.java` - Updated serialization to use `objectMapper.writeValueAsString()` and `objectMapper.readValue()`

### Issue 4: PostgreSQL JSONB Type Mismatch
**Problem:** `ERROR: column "cart" is of type jsonb but expression is of type character varying`

**Root Cause:** After changing cart to `String`, the `@JdbcTypeCode(SqlTypes.JSON)` annotation was accidentally removed.

**Solution:** Restored the annotation:
```java
@JdbcTypeCode(SqlTypes.JSON)  // Required for Hibernate to cast String as JSON
@Column(name = "cart", columnDefinition = "jsonb")
private String cart;
```

### Issue 5: Recipient Phone Number Not Allowed
**Problem:** New phone numbers receive `error 131030: Recipient phone number not in allowed list`

**Explanation:** This is a Meta WhatsApp Business API restriction for development/test mode, not a code issue.

**Solution:** Add recipient phone numbers to the allowed list in Meta Developer Console before the app is approved for production.

---

## Current Limitations

### Development Mode Restrictions
1. **Recipient Whitelist:** Can only send messages to pre-approved phone numbers
2. **Message Limits:** Limited number of messages per day in test mode
3. **Template Requirements:** Business-initiated conversations require approved templates

### Technical Limitations
1. **Single Business per Phone Number:** Current implementation assumes one WhatsApp phone number per business
2. **No Media Handling:** Image/document messages are received but not fully processed
3. **Basic Product Matching:** Uses simple string matching for product lookup (no fuzzy search or NLP)
4. **No Retry Logic:** Failed WhatsApp API calls are not automatically retried

---

## Scaling for Commercial Production

### Phase 1: Meta App Verification & Production Access

#### Requirements for Production
1. **Business Verification:** Verify your business with Meta (legal documents required)
2. **App Review:** Submit app for Meta review
3. **Display Name Approval:** Get your WhatsApp display name approved
4. **Template Approval:** Pre-approve message templates for business-initiated conversations

#### Benefits After Approval
- No recipient whitelist restrictions
- Higher message throughput limits
- Access to more WhatsApp Business features
- Official "verified" business badge

### Phase 2: Infrastructure Scaling

#### Database Scaling
```
Current: Single PostgreSQL instance
    ↓
Production Recommendations:
```

1. **Read Replicas:**
   ```yaml
   # application-prod.yml
   spring:
     datasource:
       primary:
         url: jdbc:postgresql://primary-db:5432/invoiceng
       replica:
         url: jdbc:postgresql://replica-db:5432/invoiceng
   ```

2. **Connection Pooling:**
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 50
         minimum-idle: 10
         connection-timeout: 30000
   ```

3. **Partitioning:** Partition `conversation_messages` by date for better query performance

#### Application Scaling

1. **Horizontal Scaling:**
   ```yaml
   # docker-compose.prod.yml
   services:
     invoiceng-api:
       image: invoiceng-api:latest
       deploy:
         replicas: 3
       environment:
         - SPRING_PROFILES_ACTIVE=prod
   ```

2. **Load Balancer:**
   - Use NGINX or AWS ALB
   - Sticky sessions not required (stateless design)

3. **Message Queue Integration:**
   ```java
   // Replace direct async with message queue
   @Service
   public class WebhookQueueService {
       @Autowired
       private RabbitTemplate rabbitTemplate;

       public void queueWebhook(WhatsAppWebhookPayload payload) {
           rabbitTemplate.convertAndSend("whatsapp.webhooks", payload);
       }
   }
   ```

### Phase 3: Multi-Tenant Architecture

#### Current: Single WhatsApp Number
```
All businesses → Single Meta WhatsApp Number → Your Backend
```

#### Production: Multiple WhatsApp Numbers
```
Business A → WhatsApp Number A ─┐
Business B → WhatsApp Number B ─┼──→ Your Backend (Multi-tenant)
Business C → WhatsApp Number C ─┘
```

#### Implementation Changes

1. **Dynamic WhatsApp Credentials:**
   ```java
   @Entity
   public class BusinessWhatsAppConfig {
       @Id
       private UUID id;

       @OneToOne
       private User business;

       private String phoneNumberId;

       @Column(columnDefinition = "TEXT")
       private String encryptedAccessToken;

       private String webhookVerifyToken;
       private boolean isVerified;
       private LocalDateTime tokenExpiresAt;
   }
   ```

2. **Webhook Routing:**
   ```java
   @PostMapping("/webhooks/whatsapp/{businessId}")
   public ResponseEntity<Void> handleWebhook(
       @PathVariable UUID businessId,
       @RequestBody WhatsAppWebhookPayload payload
   ) {
       // Route to specific business
   }
   ```

3. **Token Refresh Service:**
   ```java
   @Scheduled(cron = "0 0 */6 * * *")  // Every 6 hours
   public void refreshExpiringTokens() {
       List<BusinessWhatsAppConfig> expiring =
           configRepository.findByTokenExpiresAtBefore(
               LocalDateTime.now().plusDays(7)
           );
       // Refresh tokens via Meta API
   }
   ```

### Phase 4: AI Service Scaling

#### Current Limitations
- Direct Claude API calls
- No caching
- Single model

#### Production Recommendations

1. **AI Response Caching:**
   ```java
   @Service
   public class CachedAIService {
       @Autowired
       private RedisTemplate<String, String> redis;

       public String getResponse(String contextHash) {
           String cached = redis.opsForValue().get("ai:" + contextHash);
           if (cached != null) return cached;

           String response = claudeService.call(...);
           redis.opsForValue().set("ai:" + contextHash, response,
               Duration.ofHours(1));
           return response;
       }
   }
   ```

2. **Model Selection by Business Tier:**
   ```java
   public String selectModel(User business) {
       return switch (business.getSubscriptionTier()) {
           case "free" -> "claude-3-haiku-20240307";      // Fast, cheap
           case "pro" -> "claude-sonnet-4-20250514";       // Balanced
           case "enterprise" -> "claude-opus-4-5-20251101"; // Best quality
           default -> "claude-3-haiku-20240307";
       };
   }
   ```

3. **Fallback Chain:**
   ```java
   public AIResponse generateWithFallback(Conversation conv, String message) {
       try {
           return claudeService.generate(conv, message);
       } catch (RateLimitException e) {
           return openAIService.generate(conv, message);  // Fallback
       } catch (Exception e) {
           return new AIResponse(getStaticFallbackMessage());
       }
   }
   ```

### Phase 5: Observability & Monitoring

#### Logging Enhancement
```yaml
# logback-spring.xml
<appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <customFields>{"service":"invoiceng-api"}</customFields>
    </encoder>
</appender>
```

#### Metrics Collection
```java
@Component
public class WhatsAppMetrics {
    private final MeterRegistry registry;

    public void recordMessageReceived(String businessId) {
        registry.counter("whatsapp.messages.received",
            "business", businessId).increment();
    }

    public void recordAILatency(long ms) {
        registry.timer("ai.response.latency").record(ms, TimeUnit.MILLISECONDS);
    }

    public void recordConversion(String businessId) {
        registry.counter("conversions.total",
            "business", businessId).increment();
    }
}
```

#### Health Checks
```java
@Component
public class WhatsAppHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        boolean apiReachable = checkMetaAPI();
        if (apiReachable) {
            return Health.up().withDetail("whatsapp", "connected").build();
        }
        return Health.down().withDetail("whatsapp", "unreachable").build();
    }
}
```

### Phase 6: Security Hardening

#### Webhook Signature Verification
```java
public boolean verifyWebhookSignature(String payload, String signature) {
    String expected = HmacUtils.hmacSha256Hex(
        appSecret,
        payload
    );
    return MessageDigest.isEqual(
        expected.getBytes(),
        signature.getBytes()
    );
}
```

#### Token Encryption
```java
@Service
public class TokenEncryptionService {
    @Value("${encryption.key}")
    private String encryptionKey;

    public String encrypt(String token) {
        // AES-256 encryption
    }

    public String decrypt(String encrypted) {
        // AES-256 decryption
    }
}
```

#### Rate Limiting
```java
@Component
public class WebhookRateLimiter {
    private final RateLimiter limiter = RateLimiter.create(100.0); // 100/sec

    public boolean tryAcquire() {
        return limiter.tryAcquire();
    }
}
```

---

## Configuration Reference

### application.yml (Development)
```yaml
server:
  port: 8085

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/invoiceng
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

whatsapp:
  api-version: v18.0
  verify-token: ${WHATSAPP_VERIFY_TOKEN}
  phone-number-id: ${WHATSAPP_PHONE_NUMBER_ID}
  access-token: ${WHATSAPP_ACCESS_TOKEN}

claude:
  api-key: ${ANTHROPIC_API_KEY}
  model: claude-sonnet-4-20250514
  max-tokens: 512

logging:
  level:
    com.invoiceng: DEBUG
    org.hibernate.SQL: DEBUG
```

### Environment Variables Required
```bash
# Database
DB_USERNAME=postgres
DB_PASSWORD=your_password

# WhatsApp Business API
WHATSAPP_VERIFY_TOKEN=your_verify_token
WHATSAPP_PHONE_NUMBER_ID=889555034246429
WHATSAPP_ACCESS_TOKEN=your_access_token

# Anthropic Claude
ANTHROPIC_API_KEY=sk-ant-...

# Optional: Paystack (for payments)
PAYSTACK_SECRET_KEY=sk_test_...
```

---

## Quick Start Commands

### Start Backend
```bash
cd invoiceng-api
mvn clean compile spring-boot:run -Dspring-boot.run.arguments="--server.port=8085"
```

### Expose Local Server (for webhook testing)
```bash
ngrok http 8085
# Copy the HTTPS URL to Meta Developer Console webhook settings
```

### Test Webhook Locally
```bash
curl -X POST http://localhost:8085/api/v1/webhooks/whatsapp \
  -H "Content-Type: application/json" \
  -d '{"object":"whatsapp_business_account","entry":[...]}'
```

---

## Support & Resources

- **Meta WhatsApp Business API Docs:** https://developers.facebook.com/docs/whatsapp/cloud-api
- **Anthropic Claude API Docs:** https://docs.anthropic.com/
- **Spring Boot Documentation:** https://docs.spring.io/spring-boot/docs/current/reference/html/

---

*Documentation Version: 1.0*
*Last Updated: December 12, 2025*
*Author: Claude Code (Anthropic)*
