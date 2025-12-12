-- V4__ai_agent_platform.sql
-- AI Agent Platform: WhatsApp Integration, Conversations, Products, Orders

-- ============================================================
-- USERS TABLE UPDATES (Business enhancements)
-- ============================================================
ALTER TABLE users ADD COLUMN IF NOT EXISTS business_type VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS instagram_handle VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS facebook_page VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS whatsapp_phone_number_id VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS whatsapp_access_token TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS whatsapp_connected BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS whatsapp_connected_at TIMESTAMP;

-- ============================================================
-- AI AGENT CONFIGURATION
-- ============================================================
CREATE TABLE IF NOT EXISTS agent_configs (
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
    handoff_triggers JSONB,
    handoff_notification_method VARCHAR(20) DEFAULT 'push',

    -- Response Templates (JSON)
    templates JSONB,

    -- Delivery Settings
    delivery_areas JSONB,
    default_delivery_fee DECIMAL(10, 2) DEFAULT 0,
    dispatch_time VARCHAR(100) DEFAULT '24-48 hours',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(business_id)
);

-- ============================================================
-- PRODUCT CATALOG
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
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
    min_price DECIMAL(12, 2),

    -- Variants
    has_variants BOOLEAN DEFAULT FALSE,
    variant_options JSONB,

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

CREATE INDEX IF NOT EXISTS idx_products_business ON products(business_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(business_id, category);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(business_id, status);

-- ============================================================
-- PRODUCT VARIANTS
-- ============================================================
CREATE TABLE IF NOT EXISTS product_variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,

    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    options JSONB NOT NULL,
    price DECIMAL(12, 2),
    quantity INTEGER DEFAULT 0,
    image_url TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_variants_product ON product_variants(product_id);

-- ============================================================
-- PRODUCT IMAGES
-- ============================================================
CREATE TABLE IF NOT EXISTS product_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,

    url TEXT NOT NULL,
    alt_text VARCHAR(255),
    position INTEGER DEFAULT 0,
    is_main BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_images_product ON product_images(product_id);

-- ============================================================
-- WHATSAPP ORDERS (separate from invoices)
-- ============================================================
CREATE TABLE IF NOT EXISTS whatsapp_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

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
    payment_status VARCHAR(20) DEFAULT 'pending',
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),
    payment_link TEXT,
    paid_at TIMESTAMP,

    -- Fulfillment
    fulfillment_status VARCHAR(20) DEFAULT 'unfulfilled',
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    tracking_number VARCHAR(100),

    -- Notes
    internal_notes TEXT,

    -- Source
    source VARCHAR(20) DEFAULT 'whatsapp',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_wa_orders_business ON whatsapp_orders(business_id);
CREATE INDEX IF NOT EXISTS idx_wa_orders_status ON whatsapp_orders(business_id, payment_status, fulfillment_status);
CREATE INDEX IF NOT EXISTS idx_wa_orders_customer ON whatsapp_orders(customer_phone);

-- ============================================================
-- CONVERSATIONS
-- ============================================================
CREATE TABLE IF NOT EXISTS conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- Customer Info
    customer_phone VARCHAR(20) NOT NULL,
    customer_name VARCHAR(255),
    customer_whatsapp_id VARCHAR(100),

    -- State
    state VARCHAR(50) DEFAULT 'greeting',
    context JSONB DEFAULT '{}',

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
    outcome VARCHAR(20),
    order_id UUID REFERENCES whatsapp_orders(id),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(business_id, customer_phone)
);

CREATE INDEX IF NOT EXISTS idx_conversations_business ON conversations(business_id);
CREATE INDEX IF NOT EXISTS idx_conversations_active ON conversations(business_id, is_active);
CREATE INDEX IF NOT EXISTS idx_conversations_customer ON conversations(customer_phone);

-- ============================================================
-- CONVERSATION MESSAGES
-- ============================================================
CREATE TABLE IF NOT EXISTS conversation_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,

    -- Message Info
    direction VARCHAR(10) NOT NULL,
    message_type VARCHAR(20) DEFAULT 'text',
    content TEXT,
    media_url TEXT,

    -- WhatsApp IDs
    whatsapp_message_id VARCHAR(100),

    -- AI Processing
    intent_detected VARCHAR(50),
    entities_extracted JSONB,
    ai_confidence DECIMAL(3, 2),

    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_messages_whatsapp_id ON conversation_messages(whatsapp_message_id) WHERE whatsapp_message_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_messages_conversation ON conversation_messages(conversation_id);

-- ============================================================
-- AI USAGE TRACKING
-- ============================================================
CREATE TABLE IF NOT EXISTS ai_usage (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    business_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    conversation_id UUID REFERENCES conversations(id),

    -- Usage Details
    operation VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    input_tokens INTEGER NOT NULL,
    output_tokens INTEGER NOT NULL,

    -- Cost (in USD cents for precision)
    cost_cents INTEGER NOT NULL,

    -- Timing
    latency_ms INTEGER,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ai_usage_business ON ai_usage(business_id);
CREATE INDEX IF NOT EXISTS idx_ai_usage_date ON ai_usage(created_at);

-- ============================================================
-- DAILY STATS
-- ============================================================
CREATE TABLE IF NOT EXISTS daily_stats (
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

CREATE INDEX IF NOT EXISTS idx_daily_stats_business ON daily_stats(business_id, date);
