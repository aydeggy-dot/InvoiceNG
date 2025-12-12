# InvoiceNG AI Sales Agent Platform - Addendum

> **Strategic Pivot**: From invoice management tool → AI-powered WhatsApp sales automation platform
> **New Value Proposition**: "Your 24/7 AI sales agent that never sleeps, never forgets, and always closes"
> **Target Market**: Lagos social commerce sellers (Instagram/Facebook → WhatsApp sales funnel)

---

## Table of Contents

1. [Strategic Vision](#1-strategic-vision)
2. [Product Architecture](#2-product-architecture)
3. [AI Agent System Design](#3-ai-agent-system-design)
4. [WhatsApp Business API Integration](#4-whatsapp-business-api-integration)
5. [Conversation Flow Engine](#5-conversation-flow-engine)
6. [Product Catalog System](#6-product-catalog-system)
7. [Database Schema Updates](#7-database-schema-updates)
8. [API Specification Updates](#8-api-specification-updates)
9. [Dashboard & Business Portal](#9-dashboard--business-portal)
10. [Implementation Phases](#10-implementation-phases)
11. [Claude Code Task Prompts](#11-claude-code-task-prompts)

---

## 1. Strategic Vision

### 1.1 The New Problem We're Solving

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    THE LAGOS SOCIAL COMMERCE PROBLEM                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   CURRENT WORKFLOW (Manual):                                                │
│   ─────────────────────────                                                  │
│                                                                             │
│   1. Seller posts product on Instagram/Facebook                            │
│      "Beautiful Ankara dress ₦45,000. DM or WhatsApp: 0801..."            │
│                                                                             │
│   2. Customer clicks WhatsApp link                                         │
│      "Hi, I saw your dress on Instagram"                                   │
│                                                                             │
│   3. Seller responds (whenever they see it - could be hours)               │
│      "Hello dear, yes it's available"                                      │
│                                                                             │
│   4. Back-and-forth conversation (10-50 messages)                          │
│      - What sizes available?                                               │
│      - Do you have other colors?                                           │
│      - What's the price?                                                   │
│      - Can you do discount?                                                │
│      - How long to deliver?                                                │
│      - Can I pay on delivery?                                              │
│                                                                             │
│   5. Customer goes cold (seller was slow, distracted, or slept)            │
│      OR                                                                     │
│   5. Order placed, payment negotiated manually                             │
│                                                                             │
│   6. Seller tries to remember who ordered what                             │
│      (Scrolls through 100s of chats)                                       │
│                                                                             │
│   PROBLEMS:                                                                 │
│   ─────────                                                                 │
│   ❌ Response time: 30 mins - 24 hours (customers leave)                   │
│   ❌ Inconsistent responses (tired, busy, forgot details)                  │
│   ❌ Lost sales: 60-70% of inquiries never convert                         │
│   ❌ No tracking: Who asked for what? Who paid?                            │
│   ❌ Can't scale: One person = limited conversations                       │
│   ❌ Night/weekend inquiries go unanswered                                 │
│   ❌ Repetitive questions drain energy                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 The AI Agent Solution

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    THE AI SALES AGENT SOLUTION                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   NEW WORKFLOW (AI-Powered):                                                │
│   ──────────────────────────                                                 │
│                                                                             │
│   1. Seller posts product on Instagram/Facebook                            │
│      "Beautiful Ankara dress ₦45,000. WhatsApp: 0801..." (same)           │
│                                                                             │
│   2. Customer clicks WhatsApp link                                         │
│      "Hi, I saw your dress on Instagram"                                   │
│                                                                             │
│   3. AI AGENT responds INSTANTLY (24/7)                                    │
│      "Hello! 👋 Welcome to Amara's Fashion House!                          │
│       Yes, our beautiful Ankara dress is available.                        │
│       It comes in sizes S, M, L, XL.                                       │
│       What size would you like?"                                           │
│                                                                             │
│   4. AI handles entire conversation naturally                              │
│      ✅ Answers all product questions                                      │
│      ✅ Shows other options if requested                                   │
│      ✅ Handles price negotiations (within limits)                         │
│      ✅ Collects delivery address                                          │
│      ✅ Explains payment options                                           │
│      ✅ Generates invoice with payment link                                │
│                                                                             │
│   5. Customer pays via Paystack link                                       │
│      AI: "Payment received! 🎉 Your order #ORD-001 is confirmed.          │
│           We'll dispatch within 24 hours. Thank you!"                      │
│                                                                             │
│   6. Seller gets notification on dashboard                                 │
│      "New order! ₦45,000 - Ankara Dress (Size M) - Lekki delivery"        │
│                                                                             │
│   RESULTS:                                                                  │
│   ─────────                                                                 │
│   ✅ Response time: < 5 seconds (24/7/365)                                 │
│   ✅ Consistent, professional responses                                    │
│   ✅ 2-3x conversion rate improvement                                      │
│   ✅ Complete order tracking                                               │
│   ✅ Unlimited simultaneous conversations                                  │
│   ✅ Seller focuses on fulfillment, not chatting                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 Target Customer Profile (Updated)

```yaml
Business Type: Social commerce sellers
  - Fashion (clothing, shoes, accessories)
  - Beauty (makeup, skincare, hair)
  - Food (small chops, cakes, meals)
  - Home goods (decor, furniture)
  - Electronics (phones, accessories)
  - General merchandise

Sales Channel:
  - Primary: WhatsApp (90% of sales conversations)
  - Lead Generation: Instagram, Facebook, TikTok, Twitter

Monthly Revenue: ₦200K - ₦10M
Monthly Inquiries: 50 - 1,000+
Conversion Rate (Current): 20-40%
Conversion Rate (With AI): 50-70% (target)

Pain Points:
  1. Can't respond fast enough (lose sales)
  2. Answering same questions repeatedly
  3. Night/weekend inquiries go cold
  4. No time to do anything else
  5. Can't track what's sold vs pending
  6. Manual invoicing and payment follow-up

Willingness to Pay:
  - ₦10,000 - ₦50,000/month for working solution
  - High ROI: One extra sale/day = ₦1-2M/year
```

### 1.4 Competitive Moat

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    WHY THIS IS DEFENSIBLE                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. WHATSAPP BUSINESS API ACCESS                                          │
│      • Complex to set up (Meta approval required)                          │
│      • Most competitors don't have it                                      │
│      • Creates barrier to entry                                            │
│                                                                             │
│   2. AI TRAINING ON NIGERIAN COMMERCE                                      │
│      • Understands Pidgin English                                          │
│      • Knows Nigerian negotiation culture                                  │
│      • Handles "what's your last price" gracefully                        │
│      • Understands local payment methods                                   │
│                                                                             │
│   3. INTEGRATED PAYMENT + FULFILLMENT                                      │
│      • Not just chat - complete transaction                                │
│      • Paystack integration for payments                                   │
│      • Delivery tracking integration                                       │
│                                                                             │
│   4. NETWORK EFFECTS                                                       │
│      • More conversations = better AI                                      │
│      • Best practices learned across sellers                               │
│      • Marketplace potential (connect buyers to sellers)                   │
│                                                                             │
│   5. DATA ADVANTAGE                                                        │
│      • Conversation analytics                                              │
│      • Conversion optimization insights                                    │
│      • Pricing intelligence                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Product Architecture

### 2.1 High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    AI SALES AGENT PLATFORM ARCHITECTURE                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                         CUSTOMER TOUCHPOINTS                                │
│   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                  │
│   │  Instagram  │     │  Facebook   │     │   TikTok    │                  │
│   │   (Post)    │     │   (Post)    │     │   (Bio)     │                  │
│   └──────┬──────┘     └──────┬──────┘     └──────┬──────┘                  │
│          │                   │                   │                          │
│          └───────────────────┼───────────────────┘                          │
│                              │                                              │
│                              ▼                                              │
│                    ┌─────────────────┐                                     │
│                    │    WhatsApp     │                                     │
│                    │  (Click to Chat)│                                     │
│                    └────────┬────────┘                                     │
│                              │                                              │
│ ════════════════════════════════════════════════════════════════════════   │
│                              │                                              │
│                              ▼                                              │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                 WHATSAPP BUSINESS API LAYER                          │  │
│   │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │  │
│   │  │   Webhook    │  │   Message    │  │   Media      │               │  │
│   │  │   Receiver   │  │   Sender     │  │   Handler    │               │  │
│   │  └──────┬───────┘  └──────────────┘  └──────────────┘               │  │
│   └─────────┼───────────────────────────────────────────────────────────┘  │
│             │                                                               │
│             ▼                                                               │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                    AI AGENT ORCHESTRATION LAYER                      │  │
│   │                                                                      │  │
│   │  ┌──────────────────────────────────────────────────────────────┐   │  │
│   │  │                  CONVERSATION MANAGER                         │   │  │
│   │  │  • Session tracking                                          │   │  │
│   │  │  • Context management                                        │   │  │
│   │  │  • State machine                                             │   │  │
│   │  │  • Human handoff detection                                   │   │  │
│   │  └──────────────────────────────────────────────────────────────┘   │  │
│   │                              │                                       │  │
│   │                              ▼                                       │  │
│   │  ┌──────────────────────────────────────────────────────────────┐   │  │
│   │  │                    AI BRAIN (Claude API)                      │   │  │
│   │  │  • Natural language understanding                            │   │  │
│   │  │  • Product recommendation                                    │   │  │
│   │  │  • Negotiation handling                                      │   │  │
│   │  │  • Response generation                                       │   │  │
│   │  └──────────────────────────────────────────────────────────────┘   │  │
│   │                              │                                       │  │
│   │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐  │  │
│   │  │  Product    │  │   Order     │  │  Payment    │  │  Notify   │  │  │
│   │  │  Catalog    │  │  Creator    │  │  Generator  │  │  Service  │  │  │
│   │  └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘  │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                              │                                              │
│                              ▼                                              │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                       DATA LAYER                                     │  │
│   │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │  │
│   │  │Businesses│  │ Products │  │  Orders  │  │Conversa- │            │  │
│   │  │          │  │ Catalog  │  │          │  │  tions   │            │  │
│   │  └──────────┘  └──────────┘  └──────────┘  └──────────┘            │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                              │                                              │
│                              ▼                                              │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                    BUSINESS DASHBOARD                                │  │
│   │  • Real-time conversations                                          │  │
│   │  • Order management                                                 │  │
│   │  • Product catalog                                                  │  │
│   │  • Analytics & insights                                             │  │
│   │  • AI agent configuration                                           │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Component Breakdown

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CORE COMPONENTS                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. WHATSAPP INTEGRATION SERVICE                                          │
│   ────────────────────────────────                                          │
│   • Receives incoming messages via webhook                                 │
│   • Sends outgoing messages via API                                        │
│   • Handles media (images, documents)                                      │
│   • Manages message templates                                              │
│   • Tracks delivery/read status                                            │
│                                                                             │
│   2. CONVERSATION MANAGER                                                  │
│   ───────────────────────                                                   │
│   • Creates/retrieves conversation sessions                                │
│   • Maintains conversation state                                           │
│   • Stores conversation history                                            │
│   • Detects conversation stage                                             │
│   • Triggers appropriate actions                                           │
│                                                                             │
│   3. AI AGENT ENGINE                                                       │
│   ──────────────────                                                        │
│   • Processes customer messages                                            │
│   • Generates contextual responses                                         │
│   • Recommends products                                                    │
│   • Handles objections                                                     │
│   • Negotiates within parameters                                           │
│   • Collects order information                                             │
│                                                                             │
│   4. PRODUCT CATALOG SERVICE                                               │
│   ──────────────────────────                                                │
│   • Stores business products                                               │
│   • Manages inventory                                                      │
│   • Handles variants (size, color)                                         │
│   • Provides product search                                                │
│   • Returns product recommendations                                        │
│                                                                             │
│   5. ORDER SERVICE                                                         │
│   ───────────────                                                           │
│   • Creates orders from conversations                                      │
│   • Generates invoices                                                     │
│   • Tracks order status                                                    │
│   • Manages fulfillment workflow                                           │
│                                                                             │
│   6. PAYMENT SERVICE                                                       │
│   ─────────────────                                                         │
│   • Generates Paystack payment links                                       │
│   • Processes webhooks                                                     │
│   • Updates order status                                                   │
│   • Triggers notifications                                                 │
│                                                                             │
│   7. NOTIFICATION SERVICE                                                  │
│   ───────────────────────                                                   │
│   • Notifies business of new orders                                        │
│   • Alerts on human handoff requests                                       │
│   • Sends dispatch reminders                                               │
│   • Payment confirmation                                                   │
│                                                                             │
│   8. BUSINESS DASHBOARD                                                    │
│   ────────────────────                                                      │
│   • Live conversation monitoring                                           │
│   • Order management                                                       │
│   • Product catalog CRUD                                                   │
│   • AI agent configuration                                                 │
│   • Analytics and reports                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. AI Agent System Design

### 3.1 Agent Personality & Configuration

```yaml
# Each business configures their AI agent

agent_config:
  # Basic Identity
  business_name: "Amara's Fashion House"
  agent_name: "Amara" # Or use business name
  greeting_style: "warm_friendly" # warm_friendly, professional, casual
  language: "english_nigerian" # Supports Pidgin, formal English
  
  # Personality Traits
  personality:
    friendliness: 0.9 # 0-1 scale
    formality: 0.4 # Low = casual, High = formal
    emoji_usage: "moderate" # none, minimal, moderate, heavy
    humor: "light" # none, light, playful
  
  # Sales Behavior
  sales_style:
    pushy_level: 0.3 # 0 = consultative, 1 = aggressive
    upsell_enabled: true
    cross_sell_enabled: true
    discount_authority: 10 # Max % discount AI can offer
    negotiation_enabled: true
    min_price_percentage: 85 # Won't go below 85% of listed price
  
  # Operating Hours (for human handoff)
  business_hours:
    timezone: "Africa/Lagos"
    hours:
      monday: { start: "08:00", end: "20:00" }
      tuesday: { start: "08:00", end: "20:00" }
      # ... etc
    after_hours_behavior: "ai_only" # ai_only, collect_info, emergency_only
  
  # Human Handoff Triggers
  handoff_triggers:
    - "speak to human"
    - "talk to owner"
    - "complaint"
    - "refund"
    - "problem with order"
    - custom_patterns: ["manager", "supervisor", "real person"]
  
  # Response Templates
  templates:
    greeting: "Hello! 👋 Welcome to {business_name}! How can I help you today?"
    after_hours: "Thanks for reaching out! We're currently closed but I can help you browse our products and place an order."
    payment_sent: "Here's your payment link: {link}\n\nOnce payment is confirmed, we'll process your order immediately! 🎉"
    order_confirmed: "Payment received! ✅\n\nOrder #{order_number}\n{order_summary}\n\nWe'll dispatch within {dispatch_time}. Thank you for shopping with us!"
```

### 3.2 Conversation State Machine

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CONVERSATION STATE MACHINE                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                              ┌──────────────┐                               │
│                              │   NEW_CHAT   │                               │
│                              └──────┬───────┘                               │
│                                     │                                       │
│                                     ▼                                       │
│                              ┌──────────────┐                               │
│                              │   GREETING   │                               │
│                              └──────┬───────┘                               │
│                                     │                                       │
│            ┌────────────────────────┼────────────────────────┐              │
│            │                        │                        │              │
│            ▼                        ▼                        ▼              │
│    ┌──────────────┐        ┌──────────────┐        ┌──────────────┐        │
│    │  BROWSING    │◄──────►│  PRODUCT_    │◄──────►│   GENERAL    │        │
│    │              │        │  INQUIRY     │        │   QUESTION   │        │
│    └──────┬───────┘        └──────┬───────┘        └──────────────┘        │
│           │                       │                                         │
│           └───────────┬───────────┘                                         │
│                       │                                                     │
│                       ▼                                                     │
│               ┌──────────────┐                                             │
│               │  INTERESTED  │                                             │
│               │  (Qualified) │                                             │
│               └──────┬───────┘                                             │
│                      │                                                      │
│           ┌──────────┼──────────┐                                          │
│           │          │          │                                          │
│           ▼          ▼          ▼                                          │
│   ┌────────────┐ ┌────────┐ ┌──────────────┐                              │
│   │ NEGOTIATING│ │OBJECTION│ │  READY_TO_   │                              │
│   │            │ │HANDLING │ │  ORDER       │                              │
│   └─────┬──────┘ └────┬───┘ └──────┬───────┘                              │
│         │             │            │                                        │
│         └─────────────┼────────────┘                                        │
│                       │                                                     │
│                       ▼                                                     │
│               ┌──────────────┐                                             │
│               │  COLLECTING  │                                             │
│               │  ORDER_INFO  │                                             │
│               └──────┬───────┘                                             │
│                      │                                                      │
│                      ▼                                                     │
│               ┌──────────────┐                                             │
│               │   ORDER_     │                                             │
│               │   CREATED    │                                             │
│               └──────┬───────┘                                             │
│                      │                                                      │
│                      ▼                                                     │
│               ┌──────────────┐                                             │
│               │  AWAITING_   │                                             │
│               │  PAYMENT     │                                             │
│               └──────┬───────┘                                             │
│                      │                                                      │
│           ┌──────────┴──────────┐                                          │
│           │                     │                                          │
│           ▼                     ▼                                          │
│   ┌──────────────┐      ┌──────────────┐                                  │
│   │    PAID      │      │   PAYMENT_   │                                  │
│   │              │      │   FOLLOW_UP  │───► (Re-send link)               │
│   └──────┬───────┘      └──────────────┘                                  │
│          │                                                                  │
│          ▼                                                                  │
│   ┌──────────────┐                                                         │
│   │  FULFILLED   │                                                         │
│   │  (Complete)  │                                                         │
│   └──────────────┘                                                         │
│                                                                             │
│   SPECIAL STATES (Can occur at any point):                                 │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                    │
│   │   HUMAN_     │  │   INACTIVE   │  │    SPAM/     │                    │
│   │   HANDOFF    │  │   (Timeout)  │  │    BLOCKED   │                    │
│   └──────────────┘  └──────────────┘  └──────────────┘                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.3 AI Agent Prompt Engineering

```python
# System prompt template for Claude API

AGENT_SYSTEM_PROMPT = """
You are {agent_name}, an AI sales assistant for {business_name}, a {business_type} business in Lagos, Nigeria.

## YOUR IDENTITY
- Name: {agent_name}
- Business: {business_name}
- Role: Sales assistant helping customers browse products and complete purchases
- Personality: {personality_description}

## COMMUNICATION STYLE
- Language: Nigerian English (can use light Pidgin if customer does)
- Tone: {tone_description}
- Emoji usage: {emoji_level}
- Always be helpful, patient, and professional
- Never be rude, even if customer is difficult
- Use customer's name when known

## YOUR KNOWLEDGE
### Products Available:
{product_catalog}

### Pricing Rules:
- Listed prices are in Nigerian Naira (₦)
- You can offer up to {max_discount}% discount
- Minimum acceptable price: {min_price_percentage}% of listed price
- For bulk orders (3+), you can offer additional 5% discount

### Delivery Information:
- Delivery areas: {delivery_areas}
- Delivery fee: {delivery_fee_structure}
- Delivery time: {delivery_time}

### Payment Methods:
- Bank transfer (Paystack link)
- Card payment (Paystack link)
- {additional_payment_methods}

## YOUR GOALS
1. Welcome customers warmly
2. Understand what they're looking for
3. Recommend suitable products
4. Answer questions about products
5. Handle objections professionally
6. Negotiate within allowed limits
7. Collect order details (product, size, color, quantity)
8. Collect delivery information (name, phone, address)
9. Generate and send payment link
10. Confirm payment and order

## CONVERSATION RULES
1. Keep responses concise (max 3-4 sentences usually)
2. Ask one question at a time
3. Always move conversation toward a sale
4. If customer asks about unavailable item, suggest alternatives
5. If customer goes silent, follow up politely after context allows
6. Never make up products or prices not in your catalog
7. If unsure about something, say you'll check and get back

## HUMAN HANDOFF
Transfer to human when customer:
- Explicitly asks for human/owner/manager
- Has a complaint about previous order
- Requests refund
- Becomes abusive
- Asks about custom orders outside catalog
- Topic is too complex for you to handle

To hand off, respond with: [HANDOFF_REQUESTED: {reason}]

## ORDER CREATION
When customer is ready to order, collect:
1. Product name and variant (size/color)
2. Quantity
3. Customer name
4. Phone number
5. Delivery address
6. Any special instructions

Then respond with:
[CREATE_ORDER]
Product: {product}
Variant: {variant}
Quantity: {quantity}
Customer: {name}
Phone: {phone}
Address: {address}
Notes: {notes}
[/CREATE_ORDER]

## CURRENT CONVERSATION CONTEXT
Customer phone: {customer_phone}
Conversation stage: {conversation_stage}
Items discussed: {items_discussed}
Cart: {current_cart}
Previous messages: {message_count}

## IMPORTANT
- Today's date: {current_date}
- Business hours: {business_hours}
- Current status: {business_status}

Now, respond to the customer's latest message naturally and helpfully.
"""
```

### 3.4 Response Generation Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    AI RESPONSE GENERATION FLOW                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   INCOMING MESSAGE                                                          │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 1. MESSAGE PREPROCESSING                                             │  │
│   │    • Extract text content                                           │  │
│   │    • Detect language (English/Pidgin)                               │  │
│   │    • Identify message type (text, image, voice)                     │  │
│   │    • Check for spam patterns                                        │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 2. CONTEXT LOADING                                                   │  │
│   │    • Load conversation history (last 20 messages)                   │  │
│   │    • Load customer profile (if returning)                           │  │
│   │    • Load business config & catalog                                 │  │
│   │    • Load current cart/order state                                  │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 3. INTENT CLASSIFICATION                                             │  │
│   │    • Product inquiry ("do you have...")                             │  │
│   │    • Price question ("how much...")                                 │  │
│   │    • Availability check ("is it available...")                      │  │
│   │    • Negotiation ("what's your last price...")                      │  │
│   │    • Order intent ("I want to buy...")                              │  │
│   │    • Delivery question ("do you deliver to...")                     │  │
│   │    • Complaint/Issue ("I have a problem...")                        │  │
│   │    • General chat (greetings, thanks, etc.)                         │  │
│   │    • Human handoff request                                          │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 4. TOOL/ACTION DETERMINATION                                         │  │
│   │    • Search product catalog?                                        │  │
│   │    • Check inventory?                                               │  │
│   │    • Calculate discount?                                            │  │
│   │    • Create order?                                                  │  │
│   │    • Generate payment link?                                         │  │
│   │    • Hand off to human?                                             │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 5. CLAUDE API CALL                                                   │  │
│   │    • Construct full prompt with context                             │  │
│   │    • Include relevant tools                                         │  │
│   │    • Call Claude API                                                │  │
│   │    • Parse response and tool calls                                  │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 6. ACTION EXECUTION                                                  │  │
│   │    • Execute tool calls (product search, order creation, etc.)      │  │
│   │    • Update conversation state                                      │  │
│   │    • Update cart if needed                                          │  │
│   │    • Create order if confirmed                                      │  │
│   │    • Generate payment link if needed                                │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 7. RESPONSE FORMATTING                                               │  │
│   │    • Format response for WhatsApp                                   │  │
│   │    • Add emojis if configured                                       │  │
│   │    • Attach images if product shown                                 │  │
│   │    • Include payment link if generated                              │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 8. SEND VIA WHATSAPP API                                             │  │
│   │    • Send text message                                              │  │
│   │    • Send media if applicable                                       │  │
│   │    • Log message in conversation history                            │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │ 9. POST-PROCESSING                                                   │  │
│   │    • Update conversation state                                      │  │
│   │    • Update analytics                                               │  │
│   │    • Notify business owner if needed                                │  │
│   │    • Schedule follow-up if needed                                   │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.5 Claude Tools Definition

```typescript
// Tools available to the AI agent

const agentTools = [
  {
    name: "search_products",
    description: "Search the product catalog for items matching customer query",
    input_schema: {
      type: "object",
      properties: {
        query: {
          type: "string",
          description: "Search query (product name, category, or description)"
        },
        category: {
          type: "string",
          description: "Optional category filter"
        },
        max_price: {
          type: "number",
          description: "Optional maximum price filter"
        }
      },
      required: ["query"]
    }
  },
  {
    name: "get_product_details",
    description: "Get detailed information about a specific product",
    input_schema: {
      type: "object",
      properties: {
        product_id: {
          type: "string",
          description: "The product ID"
        }
      },
      required: ["product_id"]
    }
  },
  {
    name: "check_availability",
    description: "Check if a product variant is in stock",
    input_schema: {
      type: "object",
      properties: {
        product_id: { type: "string" },
        variant: {
          type: "object",
          properties: {
            size: { type: "string" },
            color: { type: "string" }
          }
        },
        quantity: { type: "number" }
      },
      required: ["product_id", "quantity"]
    }
  },
  {
    name: "calculate_price",
    description: "Calculate total price with any applicable discounts",
    input_schema: {
      type: "object",
      properties: {
        items: {
          type: "array",
          items: {
            type: "object",
            properties: {
              product_id: { type: "string" },
              quantity: { type: "number" },
              variant: { type: "object" }
            }
          }
        },
        discount_code: { type: "string" },
        delivery_area: { type: "string" }
      },
      required: ["items"]
    }
  },
  {
    name: "add_to_cart",
    description: "Add item to customer's cart",
    input_schema: {
      type: "object",
      properties: {
        product_id: { type: "string" },
        variant: { type: "object" },
        quantity: { type: "number" }
      },
      required: ["product_id", "quantity"]
    }
  },
  {
    name: "create_order",
    description: "Create an order from the current cart",
    input_schema: {
      type: "object",
      properties: {
        customer_name: { type: "string" },
        customer_phone: { type: "string" },
        delivery_address: { type: "string" },
        delivery_area: { type: "string" },
        special_instructions: { type: "string" },
        apply_discount: { type: "number", description: "Discount percentage to apply" }
      },
      required: ["customer_name", "customer_phone", "delivery_address"]
    }
  },
  {
    name: "generate_payment_link",
    description: "Generate a Paystack payment link for the order",
    input_schema: {
      type: "object",
      properties: {
        order_id: { type: "string" }
      },
      required: ["order_id"]
    }
  },
  {
    name: "check_delivery_area",
    description: "Check if we deliver to a specific area and get delivery fee",
    input_schema: {
      type: "object",
      properties: {
        area: { type: "string", description: "Area or location name" }
      },
      required: ["area"]
    }
  },
  {
    name: "request_human_handoff",
    description: "Request to transfer conversation to human agent",
    input_schema: {
      type: "object",
      properties: {
        reason: { type: "string" },
        urgency: { type: "string", enum: ["low", "medium", "high"] }
      },
      required: ["reason"]
    }
  },
  {
    name: "send_product_image",
    description: "Send product image to customer",
    input_schema: {
      type: "object",
      properties: {
        product_id: { type: "string" },
        image_type: { type: "string", enum: ["main", "gallery", "all"] }
      },
      required: ["product_id"]
    }
  }
];
```

---

## 4. WhatsApp Business API Integration

### 4.1 WhatsApp Cloud API Setup

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    WHATSAPP BUSINESS API REQUIREMENTS                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   PREREQUISITES:                                                            │
│   ──────────────                                                             │
│   1. Facebook Business Manager account                                     │
│   2. Meta Developer account                                                │
│   3. Verified business (business verification)                             │
│   4. WhatsApp Business Platform access                                     │
│   5. Phone number (not currently on WhatsApp)                              │
│                                                                             │
│   SETUP PROCESS:                                                           │
│   ───────────────                                                           │
│   1. Create Meta Developer App                                             │
│   2. Add WhatsApp product to app                                          │
│   3. Configure webhook URL                                                 │
│   4. Register phone number                                                 │
│   5. Create message templates (for business-initiated)                     │
│   6. Get access token                                                      │
│                                                                             │
│   COST STRUCTURE (Meta pricing):                                           │
│   ─────────────────────────────                                             │
│   • User-initiated conversations: ~$0.005 per 24hr window                 │
│   • Business-initiated: ~$0.03-0.05 per conversation                      │
│   • First 1,000 conversations/month: FREE                                  │
│                                                                             │
│   FOR LAGOS BUSINESSES:                                                    │
│   ─────────────────────                                                     │
│   • User-initiated: ~₦8 per conversation                                  │
│   • Business-initiated: ~₦50 per conversation                             │
│   • Most will be user-initiated (from Instagram clicks) = CHEAP           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 Webhook Implementation

```java
// WhatsAppWebhookController.java

@RestController
@RequestMapping("/api/v1/webhooks/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {
    
    private final WhatsAppService whatsAppService;
    private final ConversationService conversationService;
    private final AgentService agentService;
    
    @Value("${whatsapp.verify-token}")
    private String verifyToken;
    
    /**
     * Webhook verification (GET) - Meta sends this to verify webhook
     */
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge
    ) {
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            log.info("Webhook verified successfully");
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Verification failed");
    }
    
    /**
     * Webhook events (POST) - Receives all WhatsApp events
     */
    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody WhatsAppWebhookPayload payload) {
        log.debug("Received webhook: {}", payload);
        
        // Process asynchronously to respond quickly
        CompletableFuture.runAsync(() -> processWebhook(payload));
        
        // Always return 200 quickly to acknowledge
        return ResponseEntity.ok().build();
    }
    
    private void processWebhook(WhatsAppWebhookPayload payload) {
        try {
            for (var entry : payload.getEntry()) {
                for (var change : entry.getChanges()) {
                    if ("messages".equals(change.getField())) {
                        processMessages(change.getValue());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing webhook", e);
        }
    }
    
    private void processMessages(WhatsAppValue value) {
        // Get business phone number ID (to identify which business)
        String phoneNumberId = value.getMetadata().getPhoneNumberId();
        
        // Find the business associated with this WhatsApp number
        Business business = businessService.findByWhatsAppPhoneId(phoneNumberId)
            .orElseThrow(() -> new BusinessNotFoundException(phoneNumberId));
        
        for (var message : value.getMessages()) {
            String customerPhone = message.getFrom();
            String messageId = message.getId();
            
            // Get or create conversation
            Conversation conversation = conversationService
                .getOrCreateConversation(business.getId(), customerPhone);
            
            // Check for duplicate (WhatsApp may retry)
            if (conversationService.isMessageProcessed(messageId)) {
                log.debug("Skipping duplicate message: {}", messageId);
                continue;
            }
            
            // Extract message content
            MessageContent content = extractContent(message);
            
            // Save incoming message
            conversationService.saveMessage(
                conversation.getId(),
                MessageDirection.INBOUND,
                content,
                messageId
            );
            
            // Process with AI agent
            AgentResponse response = agentService.processMessage(
                business,
                conversation,
                content
            );
            
            // Send response via WhatsApp
            whatsAppService.sendMessage(
                phoneNumberId,
                customerPhone,
                response
            );
            
            // Save outgoing message
            conversationService.saveMessage(
                conversation.getId(),
                MessageDirection.OUTBOUND,
                response.toContent(),
                response.getMessageId()
            );
            
            // Update conversation state
            conversationService.updateState(
                conversation.getId(),
                response.getNewState()
            );
            
            // Handle special actions
            if (response.hasOrderCreated()) {
                notificationService.notifyNewOrder(business, response.getOrder());
            }
            if (response.isHandoffRequested()) {
                notificationService.notifyHandoffRequest(business, conversation);
            }
        }
    }
    
    private MessageContent extractContent(WhatsAppMessage message) {
        return switch (message.getType()) {
            case "text" -> new TextContent(message.getText().getBody());
            case "image" -> new ImageContent(
                message.getImage().getId(),
                message.getImage().getCaption()
            );
            case "audio" -> new AudioContent(message.getAudio().getId());
            case "document" -> new DocumentContent(
                message.getDocument().getId(),
                message.getDocument().getFilename()
            );
            default -> new TextContent("[Unsupported message type: " + message.getType() + "]");
        };
    }
}
```

### 4.3 WhatsApp Message Sending Service

```java
// WhatsAppService.java

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {
    
    private final WebClient webClient;
    
    @Value("${whatsapp.api-version}")
    private String apiVersion;
    
    @Value("${whatsapp.access-token}")
    private String accessToken;
    
    private static final String BASE_URL = "https://graph.facebook.com";
    
    /**
     * Send text message
     */
    public WhatsAppSendResponse sendTextMessage(
            String phoneNumberId,
            String recipientPhone,
            String text
    ) {
        Map<String, Object> body = Map.of(
            "messaging_product", "whatsapp",
            "recipient_type", "individual",
            "to", recipientPhone,
            "type", "text",
            "text", Map.of("body", text)
        );
        
        return sendMessage(phoneNumberId, body);
    }
    
    /**
     * Send message with buttons (interactive)
     */
    public WhatsAppSendResponse sendButtonMessage(
            String phoneNumberId,
            String recipientPhone,
            String bodyText,
            List<Button> buttons
    ) {
        List<Map<String, Object>> buttonMaps = buttons.stream()
            .map(b -> Map.<String, Object>of(
                "type", "reply",
                "reply", Map.of(
                    "id", b.getId(),
                    "title", b.getTitle()
                )
            ))
            .toList();
        
        Map<String, Object> body = Map.of(
            "messaging_product", "whatsapp",
            "recipient_type", "individual",
            "to", recipientPhone,
            "type", "interactive",
            "interactive", Map.of(
                "type", "button",
                "body", Map.of("text", bodyText),
                "action", Map.of("buttons", buttonMaps)
            )
        );
        
        return sendMessage(phoneNumberId, body);
    }
    
    /**
     * Send product catalog message
     */
    public WhatsAppSendResponse sendProductMessage(
            String phoneNumberId,
            String recipientPhone,
            String headerText,
            String bodyText,
            Product product
    ) {
        // If we have product image, send as image with caption
        if (product.getImageUrl() != null) {
            return sendImageMessage(
                phoneNumberId,
                recipientPhone,
                product.getImageUrl(),
                String.format("%s\n\n%s\n\n💰 Price: ₦%,d",
                    product.getName(),
                    product.getDescription(),
                    product.getPrice().intValue()
                )
            );
        }
        
        // Otherwise send text
        return sendTextMessage(phoneNumberId, recipientPhone,
            String.format("📦 *%s*\n\n%s\n\n💰 Price: ₦%,d\n\nReply to order!",
                product.getName(),
                product.getDescription(),
                product.getPrice().intValue()
            )
        );
    }
    
    /**
     * Send image message
     */
    public WhatsAppSendResponse sendImageMessage(
            String phoneNumberId,
            String recipientPhone,
            String imageUrl,
            String caption
    ) {
        Map<String, Object> body = Map.of(
            "messaging_product", "whatsapp",
            "recipient_type", "individual",
            "to", recipientPhone,
            "type", "image",
            "image", Map.of(
                "link", imageUrl,
                "caption", caption
            )
        );
        
        return sendMessage(phoneNumberId, body);
    }
    
    /**
     * Send payment link message
     */
    public WhatsAppSendResponse sendPaymentLinkMessage(
            String phoneNumberId,
            String recipientPhone,
            Order order,
            String paymentLink
    ) {
        String message = String.format("""
            ✅ *Order Summary*
            
            %s
            
            📦 Subtotal: ₦%,d
            🚚 Delivery: ₦%,d
            ━━━━━━━━━━━━━━━
            💰 *Total: ₦%,d*
            
            🔗 *Pay securely here:*
            %s
            
            ⏰ Link expires in 24 hours
            
            Reply "PAID" once payment is complete, or contact us if you have any issues!
            """,
            formatOrderItems(order.getItems()),
            order.getSubtotal().intValue(),
            order.getDeliveryFee().intValue(),
            order.getTotal().intValue(),
            paymentLink
        );
        
        return sendTextMessage(phoneNumberId, recipientPhone, message);
    }
    
    /**
     * Send order confirmation after payment
     */
    public WhatsAppSendResponse sendOrderConfirmation(
            String phoneNumberId,
            String recipientPhone,
            Order order
    ) {
        String message = String.format("""
            🎉 *Payment Received!*
            
            Thank you, %s! Your order has been confirmed.
            
            📋 *Order #%s*
            %s
            
            📍 *Delivery Address:*
            %s
            
            🚚 We'll dispatch your order within %s.
            You'll receive a notification when it's on the way!
            
            Thank you for shopping with us! 💚
            """,
            order.getCustomerName(),
            order.getOrderNumber(),
            formatOrderItems(order.getItems()),
            order.getDeliveryAddress(),
            order.getBusiness().getDispatchTime()
        );
        
        return sendTextMessage(phoneNumberId, recipientPhone, message);
    }
    
    private WhatsAppSendResponse sendMessage(String phoneNumberId, Map<String, Object> body) {
        try {
            return webClient.post()
                .uri(BASE_URL + "/{version}/{phoneNumberId}/messages", apiVersion, phoneNumberId)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(WhatsAppSendResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message", e);
            throw new WhatsAppException("Failed to send message", e);
        }
    }
    
    private String formatOrderItems(List<OrderItem> items) {
        return items.stream()
            .map(item -> String.format("• %s x%d - ₦%,d",
                item.getProductName(),
                item.getQuantity(),
                item.getTotal().intValue()
            ))
            .collect(Collectors.joining("\n"));
    }
}
```

### 4.4 Multi-Business WhatsApp Management

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    MULTI-BUSINESS WHATSAPP ARCHITECTURE                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   OPTION 1: SHARED BUSINESS SOLUTION PROVIDER (BSP) NUMBER                 │
│   ─────────────────────────────────────────────────────────                 │
│   • You get ONE WhatsApp Business number                                   │
│   • All businesses use YOUR number                                         │
│   • You route messages to appropriate business                             │
│   • Simpler but less branded                                               │
│                                                                             │
│   OPTION 2: EACH BUSINESS HAS OWN NUMBER (Recommended)                     │
│   ────────────────────────────────────────────────────                      │
│   • Each business connects their own WhatsApp                              │
│   • Uses embedded signup flow                                              │
│   • More complex but better branding                                       │
│   • Requires Meta partnership (Tech Provider)                              │
│                                                                             │
│   FOR MVP: Start with Option 1, migrate to Option 2                        │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────    │
│                                                                             │
│   IMPLEMENTATION (Option 2 - Full):                                        │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │                YOUR WHATSAPP TECH PROVIDER ACCOUNT                   │  │
│   │                                                                      │  │
│   │    ┌───────────────┐   ┌───────────────┐   ┌───────────────┐       │  │
│   │    │  Business A   │   │  Business B   │   │  Business C   │       │  │
│   │    │  +234801...   │   │  +234802...   │   │  +234803...   │       │  │
│   │    │  Phone ID: X  │   │  Phone ID: Y  │   │  Phone ID: Z  │       │  │
│   │    └───────┬───────┘   └───────┬───────┘   └───────┬───────┘       │  │
│   │            │                   │                   │                │  │
│   │            └───────────────────┼───────────────────┘                │  │
│   │                                │                                    │  │
│   │                                ▼                                    │  │
│   │                    ┌───────────────────────┐                        │  │
│   │                    │   YOUR WEBHOOK URL    │                        │  │
│   │                    │   /webhooks/whatsapp  │                        │  │
│   │                    └───────────────────────┘                        │  │
│   │                                │                                    │  │
│   │                    Route by phone_number_id                        │  │
│   │                                │                                    │  │
│   └────────────────────────────────┼────────────────────────────────────┘  │
│                                    │                                        │
│                                    ▼                                        │
│                         ┌───────────────────┐                              │
│                         │  YOUR PLATFORM    │                              │
│                         │  AI Agent Engine  │                              │
│                         └───────────────────┘                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Conversation Flow Engine

### 5.1 Sample Conversation Flows

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    SAMPLE CONVERSATION: FASHION PURCHASE                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   [Customer clicks WhatsApp link from Instagram post]                      │
│                                                                             │
│   CUSTOMER: Hi, I saw the red dress on your page                           │
│                                                                             │
│   AI AGENT: Hello! 👋 Welcome to Amara's Fashion House!                    │
│             Yes, our beautiful red Ankara dress is still available!        │
│             It's ₦45,000 and comes in sizes S, M, L, XL.                   │
│             [Sends product image]                                          │
│             What size would you like?                                      │
│                                                                             │
│   CUSTOMER: Do you have it in blue?                                        │
│                                                                             │
│   AI AGENT: Great question! This particular design is only available       │
│             in red. But I have a similar style in blue - the "Adaeze"     │
│             dress at ₦42,000. Would you like to see it?                   │
│                                                                             │
│   CUSTOMER: Yes please                                                     │
│                                                                             │
│   AI AGENT: Here's our Adaeze dress in royal blue! 💙                      │
│             [Sends product image]                                          │
│             It's ₦42,000 and available in M, L, XL.                       │
│             Many customers love this one for owambe!                      │
│                                                                             │
│   CUSTOMER: How much is your last price?                                   │
│                                                                             │
│   AI AGENT: I understand! For this beautiful piece, our best price is     │
│             ₦40,000. That's our special price for you today. 😊           │
│             Shall I help you place an order?                              │
│                                                                             │
│   CUSTOMER: Ok I'll take size L                                            │
│                                                                             │
│   AI AGENT: Excellent choice! 🎉                                           │
│             Size L Adaeze dress in blue - ₦40,000                         │
│                                                                             │
│             Where should we deliver to? Please share:                      │
│             1. Your full name                                              │
│             2. Phone number                                                │
│             3. Delivery address                                            │
│                                                                             │
│   CUSTOMER: Chioma Eze, 08123456789, 15 Admiralty Way Lekki               │
│                                                                             │
│   AI AGENT: Thank you, Chioma! 📝                                          │
│                                                                             │
│             *Order Summary*                                                │
│             ━━━━━━━━━━━━━━━                                                │
│             • Adaeze Dress (Blue, L) - ₦40,000                            │
│             • Delivery to Lekki - ₦2,000                                  │
│             ━━━━━━━━━━━━━━━                                                │
│             *Total: ₦42,000*                                              │
│                                                                             │
│             Click here to pay securely:                                    │
│             https://paystack.com/pay/abc123                               │
│                                                                             │
│             Once payment is confirmed, we'll dispatch within 24 hours!    │
│                                                                             │
│   [Customer pays via Paystack]                                             │
│                                                                             │
│   AI AGENT: Payment received! 🎉                                           │
│                                                                             │
│             Thank you Chioma! Your order #ORD-2501-0042 is confirmed.     │
│                                                                             │
│             📦 Adaeze Dress (Blue, L)                                      │
│             📍 15 Admiralty Way, Lekki                                     │
│             📱 08123456789                                                 │
│                                                                             │
│             We'll dispatch within 24 hours and send you tracking info.    │
│                                                                             │
│             Thank you for shopping with Amara's Fashion House! 💚         │
│             Save our number for future orders.                            │
│                                                                             │
│   [Business owner sees notification on dashboard]                          │
│   [Order appears in dashboard with all details]                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 Handling Complex Scenarios

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    SCENARIO: PRICE NEGOTIATION                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   CUSTOMER: 45k too much. Give me 30k                                      │
│                                                                             │
│   AI AGENT: I understand you're looking for a good deal! 😊               │
│             This dress is made with premium Ankara fabric and              │
│             hand-finished details, which is why it's priced at ₦45,000.   │
│                                                                             │
│             The best I can do is ₦40,000 - that's over 10% off!           │
│             It's a really fair price for this quality.                    │
│                                                                             │
│             Would you like to proceed at ₦40,000?                         │
│                                                                             │
│   CUSTOMER: Make it 35k and I'll buy 2                                     │
│                                                                             │
│   AI AGENT: Ooh, buying 2 pieces! I love that! 🛍️                         │
│             For 2 pieces, I can give you a special bulk price:            │
│             ₦38,000 each = ₦76,000 total (instead of ₦90,000)            │
│                                                                             │
│             That's ₦14,000 in savings! Best deal I can offer.            │
│             Should I proceed with this order?                              │
│                                                                             │
│   [AI applied bulk discount rules from business config]                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    SCENARIO: OUT OF STOCK                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   CUSTOMER: I want the blue gown size XS                                   │
│                                                                             │
│   AI AGENT: Oh no! 😔 I'm so sorry, but the blue gown is currently        │
│             out of stock in size XS. We only have M, L, XL available.     │
│                                                                             │
│             But I have good news! We have similar styles that come        │
│             in smaller sizes:                                              │
│                                                                             │
│             1. "Ngozi" dress - ₦38,000 (XS-XL available)                  │
│             2. "Blessing" gown - ₦43,000 (XS-L available)                 │
│                                                                             │
│             Would you like to see any of these?                           │
│                                                                             │
│             Or I can notify you when the blue gown is restocked in XS?    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    SCENARIO: HUMAN HANDOFF                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   CUSTOMER: I ordered last week and still haven't received it!            │
│             This is rubbish! I want to speak to the owner NOW             │
│                                                                             │
│   AI AGENT: I'm really sorry to hear about this delay! 😔                 │
│             I completely understand your frustration.                      │
│                                                                             │
│             Let me connect you with our customer care team right away.    │
│             Someone will respond within the next 30 minutes.              │
│                                                                             │
│             In the meantime, could you please share your order number     │
│             so they can look into this immediately?                       │
│                                                                             │
│   [SYSTEM: Handoff triggered - Business owner notified]                   │
│   [Conversation marked for human follow-up]                               │
│   [AI stops responding until human takes over]                            │
│                                                                             │
│   BUSINESS OWNER (via dashboard): Hi, this is Amara personally.           │
│             I'm so sorry about the delay! Let me check your order...      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. Product Catalog System

### 6.1 Product Data Structure

```typescript
// Product catalog schema

interface Product {
  id: string;
  businessId: string;
  
  // Basic Info
  name: string;
  description: string;
  shortDescription: string; // For quick responses
  category: string;
  subcategory?: string;
  tags: string[]; // For AI search
  
  // Pricing
  price: number;
  compareAtPrice?: number; // Original price (for showing discount)
  costPrice?: number; // For profit calculation
  minPrice?: number; // Minimum negotiable price
  
  // Variants
  hasVariants: boolean;
  variants?: ProductVariant[];
  
  // Inventory
  trackInventory: boolean;
  quantity?: number;
  allowBackorder: boolean;
  
  // Media
  images: ProductImage[];
  
  // Status
  status: 'active' | 'draft' | 'archived';
  
  // AI Training
  aiKeywords: string[]; // Additional keywords for AI matching
  aiNotes: string; // Special instructions for AI about this product
  
  // Timestamps
  createdAt: Date;
  updatedAt: Date;
}

interface ProductVariant {
  id: string;
  name: string; // e.g., "Blue / Size M"
  options: {
    size?: string;
    color?: string;
    material?: string;
    [key: string]: string | undefined;
  };
  sku?: string;
  price?: number; // Override base price
  quantity?: number;
  imageId?: string; // Variant-specific image
}

interface ProductImage {
  id: string;
  url: string;
  altText?: string;
  position: number;
  isMain: boolean;
}

// Example product
const exampleProduct: Product = {
  id: "prod_12345",
  businessId: "bus_67890",
  
  name: "Adaeze Ankara Dress",
  description: "Beautiful A-line Ankara dress with modern African print. " +
               "Perfect for owambe, weddings, and special occasions. " +
               "Made with premium quality Ankara fabric. " +
               "Features a flattering V-neckline and flared skirt.",
  shortDescription: "Premium Ankara dress for special occasions",
  category: "Dresses",
  subcategory: "Ankara",
  tags: ["ankara", "dress", "owambe", "wedding", "african print", "gown"],
  
  price: 45000,
  compareAtPrice: 55000,
  costPrice: 22000,
  minPrice: 38000, // AI can negotiate down to this
  
  hasVariants: true,
  variants: [
    { id: "var_1", name: "Red / S", options: { color: "Red", size: "S" }, quantity: 3 },
    { id: "var_2", name: "Red / M", options: { color: "Red", size: "M" }, quantity: 5 },
    { id: "var_3", name: "Red / L", options: { color: "Red", size: "L" }, quantity: 2 },
    { id: "var_4", name: "Blue / M", options: { color: "Blue", size: "M" }, quantity: 4 },
    { id: "var_5", name: "Blue / L", options: { color: "Blue", size: "L" }, quantity: 0 }, // Out of stock
  ],
  
  trackInventory: true,
  allowBackorder: false,
  
  images: [
    { id: "img_1", url: "https://...", isMain: true, position: 0 },
    { id: "img_2", url: "https://...", isMain: false, position: 1 },
  ],
  
  status: 'active',
  
  aiKeywords: ["aso ebi", "party dress", "native", "traditional"],
  aiNotes: "Best seller. Often bought for December parties. " +
           "Pair with gele recommendation. Can offer 5% extra for buying matching gele.",
  
  createdAt: new Date(),
  updatedAt: new Date(),
};
```

### 6.2 Product Import Methods

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    PRODUCT CATALOG IMPORT OPTIONS                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   METHOD 1: MANUAL ENTRY (Dashboard)                                       │
│   ──────────────────────────────────                                        │
│   • Add products one by one                                                │
│   • Upload images                                                          │
│   • Set variants and pricing                                               │
│   • Best for: Small catalogs (<50 products)                                │
│                                                                             │
│   METHOD 2: SPREADSHEET IMPORT                                             │
│   ────────────────────────────                                              │
│   • Download template (Excel/CSV)                                          │
│   • Fill in product details                                                │
│   • Upload spreadsheet                                                     │
│   • System creates products                                                │
│   • Best for: Medium catalogs (50-500 products)                           │
│                                                                             │
│   METHOD 3: INSTAGRAM IMPORT (AI-Powered) ⭐                               │
│   ──────────────────────────────────────                                    │
│   • Connect Instagram business account                                     │
│   • AI scans posts for products                                            │
│   • Extracts: name, price, description from captions                      │
│   • Downloads images                                                       │
│   • Creates draft products for review                                      │
│   • Best for: Instagram-heavy sellers                                      │
│                                                                             │
│   METHOD 4: WHATSAPP CATALOG SYNC                                          │
│   ────────────────────────────────                                          │
│   • If business has WhatsApp Business catalog                              │
│   • Sync products automatically                                            │
│   • Two-way sync available                                                 │
│   • Best for: Existing WhatsApp Business users                            │
│                                                                             │
│   METHOD 5: VOICE/CHAT ENTRY (AI-Powered)                                  │
│   ────────────────────────────────────────                                  │
│   • Business owner chats with AI                                           │
│   • "Add new product: Red dress, 45k, sizes S to XL"                      │
│   • AI creates product from description                                    │
│   • Owner sends product photo via WhatsApp                                 │
│   • Best for: Non-tech-savvy users                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Database Schema Updates

### 7.1 New Tables for AI Agent Platform

```sql
-- V4__ai_agent_platform.sql

-- ============================================================
-- BUSINESSES (Updated from users)
-- ============================================================
ALTER TABLE users ADD COLUMN IF NOT EXISTS business_type VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS instagram_handle VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS facebook_page VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS whatsapp_phone_number_id VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS whatsapp_connected BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS whatsapp_connected_at TIMESTAMP;

-- ============================================================
-- AI AGENT CONFIGURATION
-- ============================================================
CREATE TABLE agent_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Identity
    agent_name VARCHAR(100),
    greeting_message TEXT,
    
    -- Personality (JSON)
    personality JSONB DEFAULT '{
        "friendliness": 0.8,
        "formality": 0.5,
        "emoji_usage": "moderate",
        "language": "english_nigerian"
    }',
    
    -- Sales Settings
    sales_settings JSONB DEFAULT '{
        "max_discount_percent": 10,
        "min_price_percent": 85,
        "negotiation_enabled": true,
        "upsell_enabled": true,
        "bulk_discount_enabled": true,
        "bulk_discount_threshold": 3,
        "bulk_discount_percent": 5
    }',
    
    -- Business Hours (JSON)
    business_hours JSONB,
    after_hours_behavior VARCHAR(20) DEFAULT 'ai_only',
    
    -- Human Handoff
    handoff_triggers TEXT[], -- Array of phrases
    handoff_notification_method VARCHAR(20) DEFAULT 'push', -- push, sms, email
    
    -- Response Templates (JSON)
    templates JSONB,
    
    -- Delivery Settings
    delivery_areas JSONB, -- Array of {area, fee}
    default_delivery_fee DECIMAL(10, 2) DEFAULT 0,
    dispatch_time VARCHAR(100) DEFAULT '24-48 hours',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(business_id)
);

-- ============================================================
-- PRODUCT CATALOG
-- ============================================================
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Basic Info
    name VARCHAR(255) NOT NULL,
    description TEXT,
    short_description VARCHAR(500),
    category VARCHAR(100),
    subcategory VARCHAR(100),
    tags TEXT[],
    
    -- Pricing
    price DECIMAL(12, 2) NOT NULL,
    compare_at_price DECIMAL(12, 2),
    cost_price DECIMAL(12, 2),
    min_price DECIMAL(12, 2), -- Minimum negotiable
    
    -- Variants
    has_variants BOOLEAN DEFAULT FALSE,
    variant_options JSONB, -- {size: [S,M,L], color: [Red,Blue]}
    
    -- Inventory
    track_inventory BOOLEAN DEFAULT FALSE,
    quantity INTEGER DEFAULT 0,
    allow_backorder BOOLEAN DEFAULT FALSE,
    
    -- AI Training
    ai_keywords TEXT[],
    ai_notes TEXT,
    
    -- Status
    status VARCHAR(20) DEFAULT 'active',
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_business ON products(business_id);
CREATE INDEX idx_products_category ON products(business_id, category);
CREATE INDEX idx_products_status ON products(business_id, status);
CREATE INDEX idx_products_search ON products USING GIN(to_tsvector('english', name || ' ' || COALESCE(description, '')));

-- ============================================================
-- PRODUCT VARIANTS
-- ============================================================
CREATE TABLE product_variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    name VARCHAR(255) NOT NULL, -- "Red / Size M"
    sku VARCHAR(100),
    
    options JSONB NOT NULL, -- {size: "M", color: "Red"}
    
    price DECIMAL(12, 2), -- Override if different
    quantity INTEGER DEFAULT 0,
    
    image_url TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_variants_product ON product_variants(product_id);

-- ============================================================
-- PRODUCT IMAGES
-- ============================================================
CREATE TABLE product_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    url TEXT NOT NULL,
    alt_text VARCHAR(255),
    position INTEGER DEFAULT 0,
    is_main BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_images_product ON product_images(product_id);

-- ============================================================
-- CONVERSATIONS
-- ============================================================
CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Customer Info
    customer_phone VARCHAR(20) NOT NULL,
    customer_name VARCHAR(255),
    customer_whatsapp_id VARCHAR(100),
    
    -- State
    state VARCHAR(50) DEFAULT 'greeting',
    context JSONB DEFAULT '{}', -- Products discussed, cart, etc.
    
    -- Cart
    cart JSONB DEFAULT '[]',
    
    -- Tracking
    is_active BOOLEAN DEFAULT TRUE,
    last_message_at TIMESTAMP,
    message_count INTEGER DEFAULT 0,
    
    -- Human Handoff
    is_handed_off BOOLEAN DEFAULT FALSE,
    handed_off_at TIMESTAMP,
    handed_off_reason TEXT,
    
    -- Outcome
    outcome VARCHAR(20), -- converted, abandoned, handed_off
    order_id UUID REFERENCES orders(id),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(business_id, customer_phone)
);

CREATE INDEX idx_conversations_business ON conversations(business_id);
CREATE INDEX idx_conversations_active ON conversations(business_id, is_active);
CREATE INDEX idx_conversations_customer ON conversations(customer_phone);

-- ============================================================
-- CONVERSATION MESSAGES
-- ============================================================
CREATE TABLE conversation_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    
    -- Message Info
    direction VARCHAR(10) NOT NULL, -- 'inbound' or 'outbound'
    message_type VARCHAR(20) DEFAULT 'text', -- text, image, audio, etc.
    content TEXT,
    media_url TEXT,
    
    -- WhatsApp IDs
    whatsapp_message_id VARCHAR(100),
    
    -- AI Processing
    intent_detected VARCHAR(50),
    entities_extracted JSONB,
    ai_confidence DECIMAL(3, 2),
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Prevent duplicates
    UNIQUE(whatsapp_message_id)
);

CREATE INDEX idx_messages_conversation ON conversation_messages(conversation_id);
CREATE INDEX idx_messages_whatsapp_id ON conversation_messages(whatsapp_message_id);

-- ============================================================
-- ORDERS (Updated)
-- ============================================================
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    conversation_id UUID REFERENCES conversations(id),
    
    -- Order Number
    order_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- Customer Info
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(255),
    
    -- Delivery
    delivery_address TEXT NOT NULL,
    delivery_area VARCHAR(100),
    delivery_fee DECIMAL(10, 2) DEFAULT 0,
    delivery_notes TEXT,
    
    -- Items (JSONB array)
    items JSONB NOT NULL,
    
    -- Pricing
    subtotal DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    discount_reason VARCHAR(255),
    total DECIMAL(12, 2) NOT NULL,
    
    -- Payment
    payment_status VARCHAR(20) DEFAULT 'pending', -- pending, paid, failed, refunded
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),
    payment_link TEXT,
    paid_at TIMESTAMP,
    
    -- Fulfillment
    fulfillment_status VARCHAR(20) DEFAULT 'unfulfilled', -- unfulfilled, processing, shipped, delivered, cancelled
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    tracking_number VARCHAR(100),
    
    -- Notes
    internal_notes TEXT,
    
    -- Source
    source VARCHAR(20) DEFAULT 'whatsapp', -- whatsapp, dashboard, api
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_business ON orders(business_id);
CREATE INDEX idx_orders_conversation ON orders(conversation_id);
CREATE INDEX idx_orders_status ON orders(business_id, payment_status, fulfillment_status);
CREATE INDEX idx_orders_customer ON orders(customer_phone);

-- ============================================================
-- AI USAGE TRACKING
-- ============================================================
CREATE TABLE ai_usage (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    conversation_id UUID REFERENCES conversations(id),
    
    -- Usage Details
    operation VARCHAR(50) NOT NULL, -- response_generation, product_search, etc.
    model VARCHAR(50) NOT NULL, -- claude-3-haiku, claude-3-sonnet
    input_tokens INTEGER NOT NULL,
    output_tokens INTEGER NOT NULL,
    
    -- Cost (in USD cents for precision)
    cost_cents INTEGER NOT NULL,
    
    -- Timing
    latency_ms INTEGER,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_usage_business ON ai_usage(business_id);
CREATE INDEX idx_ai_usage_date ON ai_usage(created_at);

-- ============================================================
-- NOTIFICATION QUEUE
-- ============================================================
CREATE TABLE notification_queue (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    type VARCHAR(50) NOT NULL, -- new_order, payment_received, handoff_request
    channel VARCHAR(20) NOT NULL, -- push, sms, email, whatsapp
    
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    
    -- Related entities
    order_id UUID REFERENCES orders(id),
    conversation_id UUID REFERENCES conversations(id),
    
    -- Status
    status VARCHAR(20) DEFAULT 'pending', -- pending, sent, failed
    sent_at TIMESTAMP,
    error_message TEXT,
    
    -- Scheduling
    scheduled_for TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_pending ON notification_queue(status, scheduled_for) 
    WHERE status = 'pending';

-- ============================================================
-- ANALYTICS: DAILY STATS
-- ============================================================
CREATE TABLE daily_stats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    
    -- Conversations
    conversations_started INTEGER DEFAULT 0,
    conversations_converted INTEGER DEFAULT 0,
    conversations_abandoned INTEGER DEFAULT 0,
    conversations_handed_off INTEGER DEFAULT 0,
    
    -- Messages
    messages_received INTEGER DEFAULT 0,
    messages_sent INTEGER DEFAULT 0,
    
    -- Orders
    orders_created INTEGER DEFAULT 0,
    orders_paid INTEGER DEFAULT 0,
    
    -- Revenue
    revenue DECIMAL(15, 2) DEFAULT 0,
    
    -- AI Usage
    ai_tokens_used INTEGER DEFAULT 0,
    ai_cost_cents INTEGER DEFAULT 0,
    
    -- Response Time
    avg_response_time_ms INTEGER,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(business_id, date)
);

CREATE INDEX idx_daily_stats_business ON daily_stats(business_id, date);
```

---

## 8. API Specification Updates

### 8.1 Product Catalog APIs

```yaml
# Product Catalog API

# List products
GET /api/v1/products:
  description: List all products for business
  headers:
    Authorization: Bearer {token}
  query:
    status?: active | draft | archived
    category?: string
    search?: string
    page?: number
    limit?: number
  response:
    200:
      data:
        - id: uuid
          name: string
          shortDescription: string
          price: number
          category: string
          mainImage: string
          hasVariants: boolean
          variantCount: number
          quantity: number
          status: string
      pagination: {...}

# Get single product
GET /api/v1/products/{id}:
  response:
    200:
      id: uuid
      name: string
      description: string
      price: number
      minPrice: number
      variants: ProductVariant[]
      images: ProductImage[]
      aiKeywords: string[]
      aiNotes: string
      # ...

# Create product
POST /api/v1/products:
  request:
    body:
      name: string # Required
      description?: string
      price: number # Required
      minPrice?: number
      category?: string
      variants?: CreateVariantInput[]
      images?: string[] # URLs or base64
      trackInventory?: boolean
      quantity?: number
      aiKeywords?: string[]
      aiNotes?: string
  response:
    201:
      # Product object

# Update product
PUT /api/v1/products/{id}:
  request:
    body:
      # All fields optional
  response:
    200:
      # Updated product

# Delete product
DELETE /api/v1/products/{id}:
  response:
    204:

# Bulk import products
POST /api/v1/products/import:
  request:
    body:
      source: spreadsheet | instagram
      data: # Spreadsheet rows or Instagram post IDs
  response:
    202:
      jobId: uuid
      status: processing
```

### 8.2 Agent Configuration APIs

```yaml
# Agent Configuration API

# Get agent config
GET /api/v1/agent/config:
  response:
    200:
      agentName: string
      greetingMessage: string
      personality: PersonalityConfig
      salesSettings: SalesSettings
      businessHours: BusinessHours
      deliveryAreas: DeliveryArea[]
      templates: Templates

# Update agent config
PUT /api/v1/agent/config:
  request:
    body:
      agentName?: string
      greetingMessage?: string
      personality?: Partial<PersonalityConfig>
      salesSettings?: Partial<SalesSettings>
      businessHours?: BusinessHours
      deliveryAreas?: DeliveryArea[]
      templates?: Partial<Templates>
  response:
    200:
      # Updated config

# Test agent response
POST /api/v1/agent/test:
  description: Send test message and get AI response (for configuration testing)
  request:
    body:
      message: string
      context?: object
  response:
    200:
      response: string
      intent: string
      suggestedActions: string[]
```

### 8.3 Conversation APIs

```yaml
# Conversation API

# List conversations
GET /api/v1/conversations:
  query:
    status?: active | handed_off | converted | abandoned
    dateFrom?: date
    dateTo?: date
    search?: string # Customer phone or name
    page?: number
    limit?: number
  response:
    200:
      data:
        - id: uuid
          customerPhone: string
          customerName: string
          state: string
          lastMessage: string
          lastMessageAt: timestamp
          messageCount: number
          isHandedOff: boolean
          outcome: string
          orderId: uuid | null
      pagination: {...}

# Get conversation details
GET /api/v1/conversations/{id}:
  response:
    200:
      id: uuid
      customerPhone: string
      customerName: string
      state: string
      cart: CartItem[]
      context: object
      isHandedOff: boolean
      messages:
        - id: uuid
          direction: inbound | outbound
          content: string
          mediaUrl: string | null
          createdAt: timestamp
      order: Order | null

# Take over conversation (human handoff)
POST /api/v1/conversations/{id}/takeover:
  response:
    200:
      message: "Conversation taken over"

# Release conversation back to AI
POST /api/v1/conversations/{id}/release:
  response:
    200:
      message: "Conversation released to AI"

# Send manual message
POST /api/v1/conversations/{id}/messages:
  request:
    body:
      content: string
      mediaUrl?: string
  response:
    201:
      # Message sent confirmation

# Get conversation analytics
GET /api/v1/conversations/analytics:
  query:
    period: today | week | month | custom
    dateFrom?: date
    dateTo?: date
  response:
    200:
      totalConversations: number
      conversionRate: number
      averageResponseTime: number
      handoffRate: number
      topIntents: IntentCount[]
      hourlyDistribution: HourlyStats[]
```

### 8.4 Order APIs (Updated)

```yaml
# Order API (Updated for AI platform)

# List orders
GET /api/v1/orders:
  query:
    status?: pending | paid | shipped | delivered | cancelled
    paymentStatus?: pending | paid | failed
    fulfillmentStatus?: unfulfilled | processing | shipped | delivered
    dateFrom?: date
    dateTo?: date
    search?: string
    page?: number
    limit?: number
  response:
    200:
      data:
        - id: uuid
          orderNumber: string
          customerName: string
          customerPhone: string
          items: OrderItem[]
          total: number
          paymentStatus: string
          fulfillmentStatus: string
          source: string
          createdAt: timestamp
      summary:
        totalOrders: number
        totalRevenue: number
        pendingPayment: number
        awaitingFulfillment: number
      pagination: {...}

# Get order details
GET /api/v1/orders/{id}:
  response:
    200:
      # Full order object with conversation link

# Update order status
PUT /api/v1/orders/{id}/status:
  request:
    body:
      fulfillmentStatus?: string
      trackingNumber?: string
      internalNotes?: string
  response:
    200:
      # Updated order

# Mark as shipped (sends WhatsApp notification)
POST /api/v1/orders/{id}/ship:
  request:
    body:
      trackingNumber?: string
      estimatedDelivery?: string
      notifyCustomer: boolean
  response:
    200:
      # Order with shipping details
      # WhatsApp notification sent if notifyCustomer=true

# Mark as delivered
POST /api/v1/orders/{id}/deliver:
  request:
    body:
      notifyCustomer: boolean
  response:
    200:
      # Order marked delivered
```

### 8.5 WhatsApp Connection APIs

```yaml
# WhatsApp Connection API

# Get connection status
GET /api/v1/whatsapp/status:
  response:
    200:
      connected: boolean
      phoneNumber: string | null
      phoneNumberId: string | null
      connectedAt: timestamp | null
      messagesSent: number
      messagesReceived: number

# Start connection flow (returns Meta embedded signup URL)
POST /api/v1/whatsapp/connect:
  response:
    200:
      authUrl: string # Redirect user here
      state: string # For verification

# Complete connection (callback from Meta)
POST /api/v1/whatsapp/callback:
  request:
    body:
      code: string
      state: string
  response:
    200:
      connected: true
      phoneNumber: string
      phoneNumberId: string

# Disconnect WhatsApp
POST /api/v1/whatsapp/disconnect:
  response:
    200:
      message: "WhatsApp disconnected"

# Send test message
POST /api/v1/whatsapp/test:
  request:
    body:
      phoneNumber: string
  response:
    200:
      sent: boolean
      messageId: string
```

---

## 9. Dashboard & Business Portal

### 9.1 Dashboard Layout

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    BUSINESS DASHBOARD LAYOUT                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  🏠 InvoiceNG          Amara's Fashion     [🔔 3] [Settings] [👤]  │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│   ┌──────────┐  ┌───────────────────────────────────────────────────────┐  │
│   │          │  │                                                        │  │
│   │  📊      │  │   DASHBOARD                                           │  │
│   │ Dashboard│  │                                                        │  │
│   │          │  │   ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐    │  │
│   │  💬      │  │   │Today    │ │ Active  │ │Conversion│ │ Revenue │    │  │
│   │ Chats    │  │   │Orders   │ │ Chats   │ │  Rate   │ │ Today   │    │  │
│   │          │  │   │   12    │ │    5    │ │   68%   │ │₦485,000 │    │  │
│   │  📦      │  │   │ +3 ▲    │ │         │ │ +5% ▲   │ │ +12% ▲  │    │  │
│   │ Orders   │  │   └─────────┘ └─────────┘ └─────────┘ └─────────┘    │  │
│   │          │  │                                                        │  │
│   │  🛍️      │  │   ┌─────────────────────────────────────────────────┐ │  │
│   │ Products │  │   │              LIVE CONVERSATIONS                  │ │  │
│   │          │  │   │                                                   │ │  │
│   │  👥      │  │   │  🟢 +234801... "I want the blue dress"    2m ago│ │  │
│   │ Customers│  │   │  🟢 +234802... "What sizes available?"    5m ago│ │  │
│   │          │  │   │  🟡 +234803... [Payment link sent]       12m ago│ │  │
│   │  📈      │  │   │  🔴 +234804... [Handoff requested]        1m ago│ │  │
│   │ Analytics│  │   │                                                   │ │  │
│   │          │  │   │  [View All Conversations →]                      │ │  │
│   │  ⚙️      │  │   └─────────────────────────────────────────────────┘ │  │
│   │ Settings │  │                                                        │  │
│   │          │  │   ┌────────────────────┐ ┌────────────────────────┐   │  │
│   │  🤖      │  │   │   RECENT ORDERS    │ │    AI PERFORMANCE      │   │  │
│   │ AI Agent │  │   │                    │ │                        │   │  │
│   │          │  │   │  #ORD-042 ₦45,000 │ │  Avg Response: 3.2s   │   │  │
│   │          │  │   │  Paid ✓ Awaiting  │ │  Messages Today: 234  │   │  │
│   │          │  │   │                    │ │  Handoffs: 2 (3%)     │   │  │
│   │          │  │   │  #ORD-041 ₦82,000 │ │  AI Cost Today: ₦450  │   │  │
│   │          │  │   │  Paid ✓ Shipped   │ │                        │   │  │
│   │          │  │   │                    │ │                        │   │  │
│   │          │  │   │  [View All →]      │ │  [View Details →]     │   │  │
│   │          │  │   └────────────────────┘ └────────────────────────┘   │  │
│   │          │  │                                                        │  │
│   └──────────┘  └───────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 9.2 Live Chat Monitor

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    LIVE CHAT MONITOR VIEW                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  💬 Live Conversations                    [Active: 5] [Filter ▼]   │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│   ┌───────────────────────┐  ┌──────────────────────────────────────────┐  │
│   │ CONVERSATION LIST     │  │  CONVERSATION DETAIL                      │  │
│   │                       │  │                                           │  │
│   │ ┌───────────────────┐ │  │  +2348012345678                          │  │
│   │ │🟢 +234801...      │ │  │  State: COLLECTING_ORDER_INFO            │  │
│   │ │ "Yes size M"      │ │  │  Cart: 1 item (₦45,000)                  │  │
│   │ │ 30 sec ago        │ │  │  [Take Over] [View Customer]            │  │
│   │ │ State: ORDERING   │ │  │                                           │  │
│   │ └───────────────────┘ │  │  ─────────────────────────────────────   │  │
│   │ ┌───────────────────┐ │  │                                           │  │
│   │ │🟢 +234802...      │ │  │  CUSTOMER (2:30 PM)                      │  │
│   │ │ "Do you deliver.."│ │  │  Hi, I saw your dress on Instagram       │  │
│   │ │ 2 min ago         │ │  │                                           │  │
│   │ │ State: INQUIRY    │ │  │  AI AGENT (2:30 PM)                      │  │
│   │ └───────────────────┘ │  │  Hello! 👋 Welcome to Amara's Fashion!   │  │
│   │ ┌───────────────────┐ │  │  Yes, our beautiful Ankara dress is      │  │
│   │ │🟡 +234803...      │ │  │  available. What size would you like?    │  │
│   │ │ [Payment pending] │ │  │                                           │  │
│   │ │ 15 min ago        │ │  │  CUSTOMER (2:31 PM)                      │  │
│   │ │ State: AWAITING   │ │  │  Size M please                           │  │
│   │ └───────────────────┘ │  │                                           │  │
│   │ ┌───────────────────┐ │  │  AI AGENT (2:31 PM)                      │  │
│   │ │🔴 +234804...      │ │  │  Great choice! Size M Ankara dress -    │  │
│   │ │ "Talk to owner"   │ │  │  ₦45,000. Where should we deliver?      │  │
│   │ │ HANDOFF NEEDED    │ │  │  Please share your name, phone, and     │  │
│   │ │ 1 min ago         │ │  │  delivery address.                       │  │
│   │ └───────────────────┘ │  │                                           │  │
│   │                       │  │  CUSTOMER (2:32 PM)                      │  │
│   │ Filter:               │  │  Yes size M                               │  │
│   │ [All] [Active]        │  │                                           │  │
│   │ [Needs Attention]     │  │  ─────────────────────────────────────   │  │
│   │ [Awaiting Payment]    │  │                                           │  │
│   │                       │  │  ┌─────────────────────────────────────┐ │  │
│   │                       │  │  │ Type message to send as business... │ │  │
│   │                       │  │  │                              [Send] │ │  │
│   │                       │  │  └─────────────────────────────────────┘ │  │
│   └───────────────────────┘  └──────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 9.3 AI Agent Configuration Page

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    AI AGENT CONFIGURATION                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   🤖 AI Agent Settings                              [Test Agent] [Save]    │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  IDENTITY                                                            │  │
│   │                                                                      │  │
│   │  Agent Name: [Amara                    ]                            │  │
│   │  (This is how your AI introduces itself)                            │  │
│   │                                                                      │  │
│   │  Greeting Message:                                                  │  │
│   │  ┌─────────────────────────────────────────────────────────────┐   │  │
│   │  │ Hello! 👋 Welcome to Amara's Fashion House!                  │   │  │
│   │  │ How can I help you today?                                    │   │  │
│   │  └─────────────────────────────────────────────────────────────┘   │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  PERSONALITY                                                         │  │
│   │                                                                      │  │
│   │  Friendliness     [━━━━━━━━━●━━] 80%                               │  │
│   │  Formality        [━━━━━●━━━━━━] 50%                               │  │
│   │                                                                      │  │
│   │  Emoji Usage:     (●) Moderate  ( ) Minimal  ( ) None              │  │
│   │  Language:        [Nigerian English ▼]                              │  │
│   │  Can use Pidgin:  [✓]                                              │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  SALES & NEGOTIATION                                                 │  │
│   │                                                                      │  │
│   │  [✓] Enable price negotiation                                       │  │
│   │      Maximum discount AI can offer: [10]%                           │  │
│   │      Minimum acceptable price: [85]% of listed price               │  │
│   │                                                                      │  │
│   │  [✓] Enable bulk discounts                                          │  │
│   │      Items needed for bulk: [3]                                     │  │
│   │      Additional discount: [5]%                                      │  │
│   │                                                                      │  │
│   │  [✓] Enable upselling (suggest related products)                   │  │
│   │  [✓] Enable cross-selling (suggest complementary items)            │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  DELIVERY AREAS                                                      │  │
│   │                                                                      │  │
│   │  ┌─────────────────────────────────────────────────────────────┐   │  │
│   │  │ Area                    │ Delivery Fee │ [Remove]           │   │  │
│   │  ├─────────────────────────┼──────────────┼────────────────────┤   │  │
│   │  │ Lekki                   │ ₦2,000       │ [×]                │   │  │
│   │  │ Victoria Island         │ ₦2,000       │ [×]                │   │  │
│   │  │ Ikeja                   │ ₦2,500       │ [×]                │   │  │
│   │  │ Mainland                │ ₦3,000       │ [×]                │   │  │
│   │  │ Outside Lagos           │ ₦5,000       │ [×]                │   │  │
│   │  └─────────────────────────┴──────────────┴────────────────────┘   │  │
│   │  [+ Add Delivery Area]                                              │  │
│   │                                                                      │  │
│   │  Dispatch Time: [24-48 hours            ]                          │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐  │
│   │  HUMAN HANDOFF                                                       │  │
│   │                                                                      │  │
│   │  Automatically request human when customer says:                    │  │
│   │  [speak to human, talk to owner, manager, complaint, refund]       │  │
│   │  [+ Add phrase]                                                     │  │
│   │                                                                      │  │
│   │  Notify me via:                                                     │  │
│   │  [✓] Push notification  [✓] WhatsApp  [ ] Email  [ ] SMS           │  │
│   └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 10. Implementation Phases

### Phase 1: Core AI Agent (Weeks 1-4)

```markdown
**Week 1: WhatsApp Integration**
- [ ] Set up Meta Developer account
- [ ] Create WhatsApp Business API app
- [ ] Implement webhook receiver
- [ ] Implement message sender
- [ ] Test basic message echo

**Week 2: Conversation Engine**
- [ ] Create conversation database schema
- [ ] Implement conversation manager
- [ ] Build state machine
- [ ] Create message storage
- [ ] Implement context tracking

**Week 3: AI Agent Core**
- [ ] Integrate Claude API
- [ ] Build system prompt generator
- [ ] Implement tool definitions
- [ ] Create product search tool
- [ ] Build response formatter
- [ ] Handle basic sales flow

**Week 4: Order Flow**
- [ ] Implement cart management
- [ ] Build order creation from conversation
- [ ] Integrate Paystack for payment links
- [ ] Handle payment webhooks
- [ ] Send order confirmations
```

### Phase 2: Business Portal (Weeks 5-6)

```markdown
**Week 5: Dashboard**
- [ ] Build dashboard with stats
- [ ] Create live conversation monitor
- [ ] Implement conversation detail view
- [ ] Add human takeover functionality
- [ ] Build order management view

**Week 6: Configuration**
- [ ] Create agent configuration UI
- [ ] Build product catalog CRUD
- [ ] Implement product import (spreadsheet)
- [ ] Add delivery area management
- [ ] Create notification preferences
```

### Phase 3: Advanced Features (Weeks 7-8)

```markdown
**Week 7: Intelligence**
- [ ] Improve AI prompts
- [ ] Add negotiation logic
- [ ] Implement upselling/cross-selling
- [ ] Build conversation analytics
- [ ] Add AI performance metrics

**Week 8: Polish & Scale**
- [ ] Performance optimization
- [ ] Error handling improvements
- [ ] Multi-business support testing
- [ ] Load testing
- [ ] Documentation
```

---

## 11. Claude Code Task Prompts

### 11.1 WhatsApp Webhook Implementation

```markdown
## Task: Implement WhatsApp Business API Webhook

Create the WhatsApp webhook system:

### 1. Webhook Controller (WhatsAppWebhookController.java):
- GET endpoint for webhook verification (hub.mode, hub.verify_token, hub.challenge)
- POST endpoint for receiving messages
- Verify request signature using app secret
- Parse WhatsApp webhook payload structure
- Handle different message types (text, image, audio, document)
- Process asynchronously (return 200 immediately)

### 2. WhatsApp DTOs:
- WhatsAppWebhookPayload
- WhatsAppEntry
- WhatsAppChange
- WhatsAppValue
- WhatsAppMessage
- WhatsAppMessageText
- WhatsAppMessageImage
- WhatsAppContact

### 3. WhatsApp Service (WhatsAppService.java):
- sendTextMessage(phoneNumberId, recipient, text)
- sendImageMessage(phoneNumberId, recipient, imageUrl, caption)
- sendButtonMessage(phoneNumberId, recipient, text, buttons)
- sendTemplateMessage(phoneNumberId, recipient, templateName, params)
- markAsRead(phoneNumberId, messageId)

### 4. Configuration:
- WhatsAppConfig class with @ConfigurationProperties
- Properties: apiVersion, accessToken, verifyToken, appSecret, phoneNumberId

### 5. Signature Verification:
- Implement HMAC-SHA256 signature verification
- Use x-hub-signature-256 header

### Requirements:
- Use WebClient for API calls
- Handle rate limits (retry with backoff)
- Log all incoming/outgoing messages
- Handle webhook retries (idempotency)

Include integration test with mocked WhatsApp API.
```

### 11.2 Conversation Manager Implementation

```markdown
## Task: Implement Conversation Manager

Build the conversation management system:

### 1. Database Entities:
- Conversation entity (all fields from schema)
- ConversationMessage entity
- ConversationState enum

### 2. Repositories:
- ConversationRepository
  - findByBusinessIdAndCustomerPhone
  - findActiveByBusinessId
  - findNeedingAttention (handed off or stale)
- ConversationMessageRepository
  - findByConversationIdOrderByCreatedAt
  - existsByWhatsAppMessageId (for deduplication)

### 3. ConversationService:
```java
public interface ConversationService {
    // Get or create conversation
    Conversation getOrCreateConversation(UUID businessId, String customerPhone);
    
    // Save messages
    void saveInboundMessage(UUID conversationId, MessageContent content, String whatsappId);
    void saveOutboundMessage(UUID conversationId, MessageContent content, String whatsappId);
    
    // State management
    void updateState(UUID conversationId, ConversationState newState);
    void updateContext(UUID conversationId, Map<String, Object> context);
    
    // Cart management
    void addToCart(UUID conversationId, CartItem item);
    void updateCart(UUID conversationId, List<CartItem> items);
    void clearCart(UUID conversationId);
    
    // Human handoff
    void requestHandoff(UUID conversationId, String reason);
    void takeOver(UUID conversationId, UUID userId);
    void releaseToAI(UUID conversationId);
    
    // Queries
    List<Conversation> getActiveConversations(UUID businessId);
    List<Conversation> getConversationsNeedingAttention(UUID businessId);
    ConversationHistory getHistory(UUID conversationId, int limit);
}
```

### 4. State Machine:
- Define all states and valid transitions
- Implement state transition validation
- Auto-transition based on events (payment received, etc.)

### 5. Context Management:
- Store products discussed
- Store customer preferences detected
- Store negotiation history
- TTL for context (clear after 24 hours inactivity)

### 6. Message Deduplication:
- Check WhatsApp message ID before processing
- Handle webhook retries gracefully

Include unit tests for state machine transitions.
```

### 11.3 AI Agent Engine Implementation

```markdown
## Task: Implement AI Agent Engine

Build the core AI agent that handles conversations:

### 1. AgentService:
```java
@Service
public class AgentService {
    
    /**
     * Process incoming message and generate response
     */
    public AgentResponse processMessage(
        Business business,
        Conversation conversation,
        MessageContent incomingMessage
    );
    
    /**
     * Generate system prompt for Claude
     */
    private String buildSystemPrompt(Business business, Conversation conversation);
    
    /**
     * Execute tool calls from Claude response
     */
    private ToolResult executeTool(String toolName, Map<String, Object> input);
}
```

### 2. System Prompt Builder:
- Include business info (name, type)
- Include agent personality from config
- Include product catalog (relevant products)
- Include sales settings (negotiation rules)
- Include delivery info
- Include conversation context
- Include current cart
- Include recent message history

### 3. Tool Implementations:
- searchProducts(query, filters) → List<Product>
- getProductDetails(productId) → Product
- checkAvailability(productId, variant, quantity) → AvailabilityResult
- calculatePrice(items, discountCode, deliveryArea) → PriceCalculation
- addToCart(productId, variant, quantity) → Cart
- createOrder(customerInfo) → Order
- generatePaymentLink(orderId) → PaymentLink
- checkDeliveryArea(area) → DeliveryInfo
- requestHumanHandoff(reason) → HandoffResult

### 4. Claude API Integration:
- Use claude-3-haiku for most responses (fast, cheap)
- Use claude-3-sonnet for complex negotiation
- Implement tool use (function calling)
- Handle streaming responses (optional)
- Track token usage

### 5. Response Processing:
- Parse Claude response
- Execute any tool calls
- Format response for WhatsApp
- Determine state transition
- Return AgentResponse with:
  - Text response
  - Media attachments (if any)
  - New conversation state
  - Actions taken (order created, etc.)

### 6. Special Handling:
- Detect human handoff requests
- Handle out-of-scope questions
- Handle abusive messages
- Handle payment confirmations ("I've paid")

### 7. Prompt Examples:
Include actual prompt templates for:
- Initial greeting
- Product inquiry
- Negotiation
- Order collection
- Payment sent
- Objection handling

Include comprehensive tests with mocked Claude API.
```

### 11.4 Product Catalog Implementation

```markdown
## Task: Implement Product Catalog System

Build the product catalog management:

### 1. Database Entities:
- Product (from schema)
- ProductVariant
- ProductImage

### 2. Repositories with Search:
- ProductRepository
  - findByBusinessId
  - searchProducts(businessId, query) - Full text search
  - findByCategory(businessId, category)
  - findSimilar(productId) - For recommendations

### 3. ProductService:
```java
public interface ProductService {
    // CRUD
    Product createProduct(UUID businessId, CreateProductRequest request);
    Product updateProduct(UUID productId, UpdateProductRequest request);
    void deleteProduct(UUID productId);
    
    // Queries
    Page<Product> listProducts(UUID businessId, ProductFilters filters, Pageable pageable);
    Product getProduct(UUID productId);
    
    // Search (for AI)
    List<Product> searchProducts(UUID businessId, String query, int limit);
    List<Product> getRecommendations(UUID businessId, UUID productId, int limit);
    
    // Inventory
    boolean checkAvailability(UUID productId, String variantId, int quantity);
    void decrementInventory(UUID productId, String variantId, int quantity);
    
    // Import
    ImportJob importFromSpreadsheet(UUID businessId, MultipartFile file);
    ImportJob importFromInstagram(UUID businessId, String accessToken);
}
```

### 4. Search Implementation:
- Use PostgreSQL full-text search
- Search in: name, description, tags, aiKeywords
- Rank by relevance
- Filter by category, price range, availability

### 5. Product Import:
- Spreadsheet parser (Apache POI)
- Template generation
- Validation with error reporting
- Batch insert

### 6. AI Integration:
- Format products for AI context
- Generate product descriptions (optional AI feature)
- Extract products from Instagram posts (AI)

### 7. Image Handling:
- Upload to Cloudflare R2
- Generate thumbnails
- Return public URLs

### 8. API Endpoints:
- Full CRUD for products
- Variant management
- Image upload
- Bulk import endpoint
- Search endpoint

Include tests for search functionality.
```

### 11.5 Order & Payment Flow Implementation

```markdown
## Task: Implement Order and Payment Flow

Build the complete order and payment system:

### 1. OrderService:
```java
public interface OrderService {
    // Create from conversation
    Order createFromConversation(UUID conversationId, CreateOrderRequest request);
    
    // Generate payment
    PaymentLink generatePaymentLink(UUID orderId);
    
    // Payment processing
    void handlePaymentSuccess(String reference, PaystackWebhookData data);
    void handlePaymentFailure(String reference, PaystackWebhookData data);
    
    // Fulfillment
    void markAsProcessing(UUID orderId);
    void markAsShipped(UUID orderId, ShipmentInfo info);
    void markAsDelivered(UUID orderId);
    void cancelOrder(UUID orderId, String reason);
    
    // Queries
    Page<Order> listOrders(UUID businessId, OrderFilters filters, Pageable pageable);
    OrderStats getStats(UUID businessId, LocalDate from, LocalDate to);
}
```

### 2. Order Creation Flow:
1. Validate cart items
2. Check inventory
3. Calculate totals (subtotal + delivery - discount)
4. Create order record
5. Generate order number
6. Decrement inventory (or reserve)
7. Return order with payment pending

### 3. Payment Flow:
1. Generate Paystack payment link
2. Store payment reference on order
3. Send link to customer via WhatsApp
4. Wait for webhook
5. On success: Update order, notify business, send confirmation
6. On failure: Update order, notify customer

### 4. WhatsApp Notifications:
- Payment link message
- Payment received confirmation
- Order shipped notification
- Delivery confirmation

### 5. Order Number Generation:
- Format: ORD-{YYMM}-{4-digit-sequence}
- Example: ORD-2501-0042
- Per-business sequence

### 6. Dashboard Integration:
- Real-time order notifications
- Order list with filters
- Order detail view
- Quick actions (ship, deliver)

### 7. Analytics:
- Orders by day/week/month
- Revenue tracking
- Conversion rate (conversations → orders)
- Average order value

Include end-to-end test for order flow.
```

### 11.6 Business Dashboard Implementation

```markdown
## Task: Implement Business Dashboard Frontend

Build the React dashboard for business owners:

### 1. Pages:
- /dashboard - Main dashboard with stats
- /conversations - Live chat monitor
- /conversations/:id - Conversation detail
- /orders - Order management
- /orders/:id - Order detail
- /products - Product catalog
- /products/new - Create product
- /products/:id - Edit product
- /settings - General settings
- /settings/agent - AI agent configuration
- /settings/whatsapp - WhatsApp connection
- /analytics - Detailed analytics

### 2. Dashboard Page:
- Stats cards (orders today, revenue, active chats, conversion rate)
- Live conversations list (real-time with WebSocket)
- Recent orders list
- AI performance summary
- Quick actions

### 3. Live Conversation Monitor:
- Real-time conversation list
- Conversation detail with message history
- Human takeover button
- Send manual message input
- Customer info panel
- Cart preview

### 4. Real-time Updates:
- WebSocket connection for:
  - New conversations
  - New messages
  - Order updates
  - Handoff requests
- Toast notifications
- Sound alerts (optional)

### 5. Order Management:
- Filterable order list
- Order detail with:
  - Customer info
  - Items ordered
  - Payment status
  - Fulfillment status
  - Conversation link
- Actions: Mark shipped, Mark delivered, Cancel

### 6. AI Agent Configuration:
- Agent identity settings
- Personality sliders
- Sales/negotiation rules
- Delivery areas CRUD
- Handoff trigger phrases
- Test agent chat

### 7. Product Management:
- Product grid/list view
- Quick edit modal
- Full edit page
- Image upload with preview
- Variant management
- Bulk import

### 8. Mobile Responsive:
- All pages work on mobile
- Bottom navigation on mobile
- Touch-friendly interactions

Include Storybook stories for key components.
```

---

## Summary: New Product Vision

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    INVOICENG AI SALES AGENT PLATFORM                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   BEFORE (Original Concept):                                               │
│   • Manual invoice creation tool                                           │
│   • User creates invoice → Sends via WhatsApp → Tracks payment            │
│   • Value: Saves time on invoicing                                        │
│   • Competition: Many invoice apps                                        │
│                                                                             │
│   AFTER (AI Agent Platform):                                               │
│   • AI handles entire sales conversation                                   │
│   • Customer chats → AI sells → AI creates invoice → Customer pays       │
│   • Value: 24/7 sales agent, higher conversion, more revenue              │
│   • Competition: Very few doing this for African market                   │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────    │
│                                                                             │
│   KEY DIFFERENTIATORS:                                                     │
│   ✅ AI that understands Nigerian commerce culture                         │
│   ✅ WhatsApp-native (not a web dashboard with WhatsApp add-on)           │
│   ✅ Complete transaction (chat → invoice → payment → fulfillment)        │
│   ✅ Handles negotiation ("what's your last price?")                      │
│   ✅ Works 24/7 (captures night/weekend sales)                            │
│                                                                             │
│   TARGET METRICS:                                                          │
│   • Response time: <5 seconds (vs 30+ minutes manual)                     │
│   • Conversion rate: 50-70% (vs 20-40% manual)                           │
│   • Sales increase: 2-3x for typical business                             │
│   • Business owner time saved: 4-6 hours/day                              │
│                                                                             │
│   PRICING MODEL:                                                           │
│   • Free: 50 AI conversations/month                                       │
│   • Starter: ₦15,000/month - 500 conversations                           │
│   • Pro: ₦35,000/month - Unlimited + advanced features                   │
│   • Enterprise: Custom                                                     │
│   • + Transaction fee on payments                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

*This addendum extends the original InvoiceNG Claude Code prompt to transform the product from an invoice tool into an AI-powered sales automation platform.*
