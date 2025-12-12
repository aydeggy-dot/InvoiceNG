package com.invoiceng.controller;

import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.entity.*;
import com.invoiceng.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Development-only controller for seeding test data.
 * Only available when running with 'dev' profile.
 */
@RestController
@RequestMapping("/api/v1/dev")
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class DevSeedController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WhatsAppOrderRepository orderRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;
    private final AgentConfigRepository agentConfigRepository;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listAllUsers() {
        log.info("Listing all users");

        List<Map<String, Object>> users = userRepository.findAll().stream().map(user -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId().toString());
            userInfo.put("phone", user.getPhone());
            userInfo.put("businessName", user.getBusinessName());
            return userInfo;
        }).toList();

        return ResponseEntity.ok(ApiResponse.success(users, "Found " + users.size() + " users"));
    }

    @GetMapping("/users/by-phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserByPhone(@PathVariable String phoneNumber) {
        log.info("Looking up user by phone: {}", phoneNumber);

        // Normalize phone number - add +234 prefix if needed
        final String normalizedPhone;
        if (phoneNumber.startsWith("0")) {
            normalizedPhone = "+234" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("+")) {
            normalizedPhone = "+" + phoneNumber;
        } else {
            normalizedPhone = phoneNumber;
        }

        User user = userRepository.findByPhone(normalizedPhone)
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + normalizedPhone));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId().toString());
        userInfo.put("phone", user.getPhone());
        userInfo.put("businessName", user.getBusinessName());

        return ResponseEntity.ok(ApiResponse.success(userInfo, "User found"));
    }

    @PostMapping("/seed/by-phone/{phoneNumber}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Integer>>> seedDataByPhone(@PathVariable String phoneNumber) {
        log.info("Seeding test data for phone: {}", phoneNumber);

        // Normalize phone number - add +234 prefix if needed
        final String normalizedPhone;
        if (phoneNumber.startsWith("0")) {
            normalizedPhone = "+234" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("+")) {
            normalizedPhone = "+" + phoneNumber;
        } else {
            normalizedPhone = phoneNumber;
        }

        // Try multiple phone formats
        User user = userRepository.findByPhone(normalizedPhone)
                .or(() -> userRepository.findByPhone(phoneNumber))
                .or(() -> userRepository.findByPhone("0" + phoneNumber.replaceFirst("^\\+234", "")))
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + phoneNumber + " (tried: " + normalizedPhone + ")"));

        return seedDataForUser(user);
    }

    @PostMapping("/seed/{userId}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Integer>>> seedData(@PathVariable UUID userId) {
        log.info("Seeding test data for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return seedDataForUser(user);
    }

    private ResponseEntity<ApiResponse<Map<String, Integer>>> seedDataForUser(User user) {
        log.info("Seeding test data for user: {} ({})", user.getId(), user.getPhone());

        Map<String, Integer> counts = new HashMap<>();

        // 1. Clean up existing data for this user using JDBC
        cleanupExistingData(user.getId());

        // 2. Seed Products
        int productCount = seedProducts(user);
        counts.put("products", productCount);

        // 3. Seed Agent Config
        seedAgentConfig(user);
        counts.put("agentConfigs", 1);

        // 4. Seed Conversations with Messages
        int conversationCount = seedConversations(user);
        counts.put("conversations", conversationCount);

        // 5. Seed Orders
        int orderCount = seedOrders(user);
        counts.put("orders", orderCount);

        // 6. Update user business info
        updateUserBusinessInfo(user);

        log.info("Seeding completed: {}", counts);

        return ResponseEntity.ok(ApiResponse.success(counts, "Test data seeded successfully"));
    }

    private void cleanupExistingData(UUID businessId) {
        log.info("Cleaning up existing data for business: {}", businessId);

        // Delete in correct order due to foreign keys
        jdbcTemplate.update("DELETE FROM conversation_messages WHERE conversation_id IN (SELECT id FROM conversations WHERE business_id = ?)", businessId);
        jdbcTemplate.update("DELETE FROM conversations WHERE business_id = ?", businessId);
        jdbcTemplate.update("DELETE FROM whatsapp_orders WHERE business_id = ?", businessId);
        jdbcTemplate.update("DELETE FROM product_images WHERE product_id IN (SELECT id FROM products WHERE business_id = ?)", businessId);
        jdbcTemplate.update("DELETE FROM product_variants WHERE product_id IN (SELECT id FROM products WHERE business_id = ?)", businessId);
        jdbcTemplate.update("DELETE FROM products WHERE business_id = ?", businessId);
        jdbcTemplate.update("DELETE FROM agent_configs WHERE business_id = ?", businessId);
    }

    private int seedProducts(User user) {
        List<Product> products = new ArrayList<>();

        // Fashion Products
        products.add(createProduct(user, "Ankara Print Dress", "Fashion",
                "Beautiful handmade Ankara dress with modern design", 25000.00, 50));
        products.add(createProduct(user, "Adire Tie-Dye Shirt", "Fashion",
                "Traditional Nigerian tie-dye shirt", 15000.00, 30));
        products.add(createProduct(user, "Aso-Oke Cap (Fila)", "Fashion",
                "Handwoven traditional cap", 8000.00, 100));
        products.add(createProduct(user, "Beaded Necklace Set", "Accessories",
                "Colorful African beaded jewelry set", 12000.00, 45));
        products.add(createProduct(user, "Leather Sandals", "Footwear",
                "Handcrafted leather sandals", 18000.00, 25));

        // Beauty Products
        products.add(createProduct(user, "Shea Butter (Raw)", "Beauty",
                "100% natural unrefined shea butter - 500g", 5000.00, 200));
        products.add(createProduct(user, "Black Soap", "Beauty",
                "Traditional African black soap", 2500.00, 150));
        products.add(createProduct(user, "Hibiscus Tea (Zobo)", "Food & Drinks",
                "Dried hibiscus flowers for zobo drink - 500g", 3000.00, 80));

        // Electronics
        products.add(createProduct(user, "Solar Power Bank 20000mAh", "Electronics",
                "High capacity solar power bank", 35000.00, 15));
        products.add(createProduct(user, "Wireless Earbuds", "Electronics",
                "Bluetooth 5.0 wireless earbuds", 12000.00, 40));
        products.add(createProduct(user, "Phone Ring Light", "Electronics",
                "LED ring light for selfies", 8500.00, 60));

        // Home & Kitchen
        products.add(createProduct(user, "Mortar and Pestle Set", "Home & Kitchen",
                "Traditional wooden mortar and pestle", 15000.00, 20));
        products.add(createProduct(user, "Palm Wine Gourd", "Home & Kitchen",
                "Decorative calabash gourd", 6000.00, 35));

        // Out of stock item
        Product outOfStock = createProduct(user, "Limited Edition Agbada", "Fashion",
                "Premium hand-embroidered Agbada - sold out!", 150000.00, 0);
        outOfStock.setStatus("active");
        products.add(outOfStock);

        // Draft item
        Product draft = createProduct(user, "Coming Soon - Smart Watch", "Electronics",
                "New smart watch launching soon", 45000.00, 0);
        draft.setStatus("draft");
        products.add(draft);

        productRepository.saveAll(products);
        return products.size();
    }

    private Product createProduct(User user, String name, String category,
                                   String description, double price, int quantity) {
        return Product.builder()
                .business(user)
                .name(name)
                .category(category)
                .description(description)
                .shortDescription(description.length() > 100 ? description.substring(0, 100) : description)
                .price(BigDecimal.valueOf(price))
                .quantity(quantity)
                .trackInventory(true)
                .status(quantity > 0 ? "active" : "active")
                .build();
    }

    private void seedAgentConfig(User user) {
        AgentConfig config = AgentConfig.builder()
                .business(user)
                .agentName("Ayo")
                .greetingMessage("Hello! Welcome to " + (user.getBusinessName() != null ? user.getBusinessName() : "our store") +
                        "! I'm Ayo, your AI shopping assistant. How can I help you today?")
                .afterHoursBehavior("ai_only")
                .handoffNotificationMethod("push")
                .defaultDeliveryFee(BigDecimal.valueOf(1500))
                .dispatchTime("24-48 hours within Lagos, 3-5 days outside Lagos")
                .build();

        agentConfigRepository.save(config);
    }

    private int seedConversations(User user) {
        List<Conversation> conversations = new ArrayList<>();

        // Active conversation - browsing
        Conversation conv1 = createConversation(user, "+2348012345001", "Chidi Okonkwo",
                "browsing", false, null);
        conv1 = conversationRepository.save(conv1);
        addMessages(conv1, Arrays.asList(
                new String[]{"inbound", "Hello, I want to buy some things"},
                new String[]{"outbound", "Hello Chidi! Welcome to our store. I'm happy to help you find what you need. What are you looking for today?"},
                new String[]{"inbound", "Do you have any Ankara dresses?"},
                new String[]{"outbound", "Yes! We have beautiful Ankara Print Dresses for N25,000. They're handmade with modern designs. Would you like to see pictures?"}
        ));
        conversations.add(conv1);

        // Active conversation - in cart
        Conversation conv2 = createConversation(user, "+2348012345002", "Ngozi Adebayo",
                "cart", false, null);
        conv2 = conversationRepository.save(conv2);
        addMessages(conv2, Arrays.asList(
                new String[]{"inbound", "Hi there"},
                new String[]{"outbound", "Hello! Welcome! How can I help you today?"},
                new String[]{"inbound", "I want to buy shea butter and black soap"},
                new String[]{"outbound", "Great choices! Our Raw Shea Butter (500g) is N5,000 and Black Soap is N2,500. Would you like to add both to your cart?"},
                new String[]{"inbound", "Yes, 2 shea butter and 3 black soap"},
                new String[]{"outbound", "I've added 2x Shea Butter (N10,000) and 3x Black Soap (N7,500) to your cart. Your total is N17,500. Ready to checkout?"}
        ));
        conversations.add(conv2);

        // Conversation needing handoff
        Conversation conv3 = createConversation(user, "+2348012345003", "Emeka Johnson",
                "browsing", true, "Customer requesting custom order");
        conv3 = conversationRepository.save(conv3);
        addMessages(conv3, Arrays.asList(
                new String[]{"inbound", "Hello, I need a custom Agbada for my wedding"},
                new String[]{"outbound", "Congratulations on your upcoming wedding! For custom Agbada orders, let me connect you with our design team who can discuss your specific requirements."},
                new String[]{"inbound", "Yes please, I need it in 3 weeks"},
                new String[]{"outbound", "I understand the urgency. Let me hand this over to a human agent who can better assist with custom orders and timeline. Please hold on."}
        ));
        conversations.add(conv3);

        // Completed conversation - converted to order
        Conversation conv4 = createConversation(user, "+2348012345004", "Fatima Bello",
                "completed", false, null);
        conv4.setOutcome("converted");
        conv4 = conversationRepository.save(conv4);
        addMessages(conv4, Arrays.asList(
                new String[]{"inbound", "Hello"},
                new String[]{"outbound", "Hi there! Welcome! How can I help you?"},
                new String[]{"inbound", "I want wireless earbuds"},
                new String[]{"outbound", "We have great Wireless Earbuds with Bluetooth 5.0 for N12,000. Would you like to order?"},
                new String[]{"inbound", "Yes, please"},
                new String[]{"outbound", "Excellent! Please share your delivery address and I'll process your order."},
                new String[]{"inbound", "15 Admiralty Way, Lekki, Lagos"},
                new String[]{"outbound", "Order confirmed! Total: N12,000 + N1,500 delivery = N13,500. I'll send you the payment link shortly."}
        ));
        conversations.add(conv4);

        // More active conversations
        Conversation conv5 = createConversation(user, "+2348012345005", "Unknown",
                "greeting", false, null);
        conv5 = conversationRepository.save(conv5);
        addMessages(conv5, Collections.singletonList(
                new String[]{"inbound", "Hi"}
        ));
        conversations.add(conv5);

        Conversation conv6 = createConversation(user, "+2348012345006", "Amara Nwosu",
                "browsing", false, null);
        conv6 = conversationRepository.save(conv6);
        addMessages(conv6, Arrays.asList(
                new String[]{"inbound", "Good morning"},
                new String[]{"outbound", "Good morning! Welcome to our store. How may I assist you today?"},
                new String[]{"inbound", "How much is the solar power bank?"},
                new String[]{"outbound", "Our Solar Power Bank (20000mAh) is N35,000. It's perfect for keeping your devices charged on the go!"}
        ));
        conversations.add(conv6);

        return conversations.size();
    }

    private Conversation createConversation(User user, String phone, String name,
                                             String state, boolean handedOff, String handoffReason) {
        Conversation conv = Conversation.builder()
                .business(user)
                .customerPhone(phone)
                .customerName(name)
                .state(state)
                .isActive(!state.equals("completed"))
                .isHandedOff(handedOff)
                .handedOffReason(handoffReason)
                .messageCount(0)
                .lastMessageAt(LocalDateTime.now().minusMinutes(new Random().nextInt(120)))
                .build();

        if (handedOff) {
            conv.setHandedOffAt(LocalDateTime.now().minusMinutes(30));
        }

        return conv;
    }

    private void addMessages(Conversation conversation, List<String[]> messages) {
        List<ConversationMessage> msgList = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now().minusMinutes(messages.size() * 5);

        for (String[] msg : messages) {
            ConversationMessage message = ConversationMessage.builder()
                    .conversation(conversation)
                    .direction(msg[0])
                    .messageType("text")
                    .content(msg[1])
                    .build();
            msgList.add(message);
            time = time.plusMinutes(2 + new Random().nextInt(3));
        }

        messageRepository.saveAll(msgList);

        conversation.setMessageCount(messages.size());
        conversation.setLastMessageAt(LocalDateTime.now().minusMinutes(new Random().nextInt(60)));
        conversationRepository.save(conversation);
    }

    private int seedOrders(User user) {
        List<WhatsAppOrder> orders = new ArrayList<>();

        // Generate unique order prefix using user ID and timestamp
        String orderPrefix = "ORD-" + user.getId().toString().substring(0, 8).toUpperCase() + "-";

        // Paid and delivered order
        orders.add(createOrder(user, orderPrefix + "001", "Adaeze Obi", "+2348011111111",
                "25 Marina Road, Lagos Island",
                createOrderItems("Ankara Print Dress", 1, 25000),
                25000.00, 1500.00, "paid", "delivered"));

        // Paid and shipped order
        orders.add(createOrder(user, orderPrefix + "002", "Kunle Bakare", "+2348022222222",
                "10 Allen Avenue, Ikeja, Lagos",
                createOrderItems("Wireless Earbuds", 2, 12000),
                24000.00, 1500.00, "paid", "shipped"));

        // Paid and pending fulfillment
        orders.add(createOrder(user, orderPrefix + "003", "Blessing Eze", "+2348033333333",
                "5 Okigwe Road, Owerri, Imo State",
                createMultiOrderItems(
                        new Object[]{"Shea Butter (Raw)", 3, 5000},
                        new Object[]{"Black Soap", 5, 2500}
                ),
                27500.00, 2500.00, "paid", "pending"));

        // Pending payment orders
        orders.add(createOrder(user, orderPrefix + "004", "Ibrahim Musa", "+2348044444444",
                "15 Ahmadu Bello Way, Kaduna",
                createOrderItems("Solar Power Bank 20000mAh", 1, 35000),
                35000.00, 3000.00, "pending", "pending"));

        orders.add(createOrder(user, orderPrefix + "005", "Chiamaka Nwachukwu", "+2348055555555",
                "8 Trans Amadi Road, Port Harcourt",
                createMultiOrderItems(
                        new Object[]{"Beaded Necklace Set", 2, 12000},
                        new Object[]{"Leather Sandals", 1, 18000}
                ),
                42000.00, 2000.00, "pending", "pending"));

        // More paid orders
        orders.add(createOrder(user, orderPrefix + "006", "Tunde Adeleke", "+2348066666666",
                "20 Awolowo Road, Ikoyi, Lagos",
                createOrderItems("Phone Ring Light", 3, 8500),
                25500.00, 1500.00, "paid", "delivered"));

        orders.add(createOrder(user, orderPrefix + "007", "Yetunde Afolabi", "+2348077777777",
                "12 Challenge Road, Ibadan",
                createOrderItems("Adire Tie-Dye Shirt", 2, 15000),
                30000.00, 2000.00, "paid", "shipped"));

        // Cancelled order
        WhatsAppOrder cancelled = createOrder(user, orderPrefix + "008", "Victor Uche", "+2348088888888",
                "7 Sapele Road, Benin City",
                createOrderItems("Mortar and Pestle Set", 1, 15000),
                15000.00, 2500.00, "failed", "cancelled");
        orders.add(cancelled);

        orderRepository.saveAll(orders);
        return orders.size();
    }

    private List<Map<String, Object>> createOrderItems(String productName, int quantity, double price) {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productName", productName);
        item.put("quantity", quantity);
        item.put("price", price);
        item.put("total", price * quantity);
        items.add(item);
        return items;
    }

    @SafeVarargs
    private List<Map<String, Object>> createMultiOrderItems(Object[]... itemsData) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Object[] data : itemsData) {
            Map<String, Object> item = new HashMap<>();
            item.put("productName", data[0]);
            item.put("quantity", data[1]);
            item.put("price", data[2]);
            item.put("total", ((Number) data[2]).doubleValue() * ((Number) data[1]).intValue());
            items.add(item);
        }
        return items;
    }

    private WhatsAppOrder createOrder(User user, String orderNumber, String customerName,
                                       String phone, String address, List<Map<String, Object>> items,
                                       double subtotal, double deliveryFee,
                                       String paymentStatus, String fulfillmentStatus) {
        WhatsAppOrder order = WhatsAppOrder.builder()
                .business(user)
                .orderNumber(orderNumber)
                .customerName(customerName)
                .customerPhone(phone)
                .deliveryAddress(address)
                .deliveryFee(BigDecimal.valueOf(deliveryFee))
                .items(items)
                .subtotal(BigDecimal.valueOf(subtotal))
                .total(BigDecimal.valueOf(subtotal + deliveryFee))
                .paymentStatus(paymentStatus)
                .fulfillmentStatus(fulfillmentStatus)
                .source("whatsapp")
                .build();

        if ("paid".equals(paymentStatus)) {
            order.setPaidAt(LocalDateTime.now().minusDays(new Random().nextInt(7)));
            order.setPaymentMethod("bank_transfer");
        }

        if ("shipped".equals(fulfillmentStatus)) {
            order.setShippedAt(LocalDateTime.now().minusDays(new Random().nextInt(3)));
            order.setTrackingNumber("TRK" + System.currentTimeMillis());
        }

        if ("delivered".equals(fulfillmentStatus)) {
            order.setShippedAt(LocalDateTime.now().minusDays(5));
            order.setDeliveredAt(LocalDateTime.now().minusDays(2));
            order.setTrackingNumber("TRK" + System.currentTimeMillis());
        }

        return order;
    }

    private void updateUserBusinessInfo(User user) {
        if (user.getBusinessName() == null) {
            user.setBusinessName("AfriShop Lagos");
        }
        if (user.getBusinessType() == null) {
            user.setBusinessType("retail");
        }
        user.setWhatsappConnected(true);
        user.setWhatsappConnectedAt(LocalDateTime.now().minusDays(30));
        userRepository.save(user);
    }

    @PostMapping("/link-whatsapp")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> linkWhatsApp(
            @RequestParam String phone,
            @RequestParam String phoneNumberId) {
        log.info("Linking WhatsApp phone number ID {} to user with phone {}", phoneNumberId, phone);

        // Try to find user by phone in various formats
        User user = userRepository.findByPhone(phone)
                .or(() -> userRepository.findByPhone("+" + phone))
                .or(() -> userRepository.findByPhone("+234" + phone.substring(phone.startsWith("0") ? 1 : 0)))
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + phone));

        user.setWhatsappPhoneNumberId(phoneNumberId);
        user.setWhatsappConnected(true);
        user.setWhatsappConnectedAt(LocalDateTime.now());
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId().toString());
        result.put("phone", user.getPhone());
        result.put("businessName", user.getBusinessName());
        result.put("whatsappPhoneNumberId", phoneNumberId);

        return ResponseEntity.ok(ApiResponse.success(result, "WhatsApp linked successfully"));
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getConversations(@PathVariable UUID userId) {
        log.info("Getting conversations for user: {}", userId);

        List<Conversation> conversations = conversationRepository.findByBusinessIdAndIsActiveTrueOrderByLastMessageAtDesc(userId);

        List<Map<String, Object>> result = conversations.stream().map(conv -> {
            Map<String, Object> convMap = new HashMap<>();
            convMap.put("id", conv.getId().toString());
            convMap.put("customerPhone", conv.getCustomerPhone());
            convMap.put("customerName", conv.getCustomerName());
            convMap.put("state", conv.getState());
            convMap.put("messageCount", conv.getMessageCount());
            convMap.put("lastMessageAt", conv.getLastMessageAt());
            convMap.put("isActive", conv.getIsActive());
            convMap.put("isHandedOff", conv.getIsHandedOff());
            return convMap;
        }).toList();

        return ResponseEntity.ok(ApiResponse.success(result, "Found " + result.size() + " conversations"));
    }

    @GetMapping("/conversations/{userId}/messages/{conversationId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getConversationMessages(
            @PathVariable UUID userId,
            @PathVariable UUID conversationId) {
        log.info("Getting messages for conversation: {}", conversationId);

        List<com.invoiceng.entity.ConversationMessage> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        List<Map<String, Object>> result = messages.stream().map(msg -> {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("direction", msg.getDirection());
            msgMap.put("content", msg.getContent());
            msgMap.put("messageType", msg.getMessageType());
            msgMap.put("createdAt", msg.getCreatedAt());
            return msgMap;
        }).toList();

        return ResponseEntity.ok(ApiResponse.success(result, "Found " + result.size() + " messages"));
    }
}
