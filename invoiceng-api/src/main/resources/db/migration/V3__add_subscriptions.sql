-- V3__add_subscriptions.sql
-- Subscription tiers and limits for monetization

-- ═══════════════════════════════════════════════════════════════
-- SUBSCRIPTION TIERS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE subscription_tiers (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    monthly_price DECIMAL(10, 2) NOT NULL,
    invoice_limit INTEGER,
    reminder_limit INTEGER,
    pdf_enabled BOOLEAN DEFAULT FALSE,
    recurring_enabled BOOLEAN DEFAULT FALSE,
    ai_credits_monthly INTEGER DEFAULT 0,
    team_members_limit INTEGER DEFAULT 1,
    transaction_fee_percent DECIMAL(4, 2) NOT NULL,
    transaction_fee_flat DECIMAL(6, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default tiers
INSERT INTO subscription_tiers (
    id, name, monthly_price, invoice_limit, reminder_limit,
    pdf_enabled, recurring_enabled, ai_credits_monthly,
    team_members_limit, transaction_fee_percent, transaction_fee_flat
) VALUES
    ('free', 'Free', 0, 10, 0, FALSE, FALSE, 5, 1, 2.00, 100),
    ('starter', 'Starter', 3000, 50, 3, TRUE, FALSE, 20, 1, 1.50, 100),
    ('pro', 'Pro', 7500, NULL, NULL, TRUE, TRUE, 75, 1, 1.00, 50),
    ('business', 'Business', 20000, NULL, NULL, TRUE, TRUE, 200, 5, 0.75, 25);

-- ═══════════════════════════════════════════════════════════════
-- USER SUBSCRIPTIONS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE user_subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tier_id VARCHAR(20) NOT NULL REFERENCES subscription_tiers(id),
    status VARCHAR(20) DEFAULT 'active',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    paystack_subscription_code VARCHAR(100),
    paystack_email_token VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_subscriptions_user ON user_subscriptions(user_id);
CREATE INDEX idx_subscriptions_status ON user_subscriptions(status);
CREATE INDEX idx_subscriptions_expires ON user_subscriptions(expires_at) WHERE status = 'active';

-- ═══════════════════════════════════════════════════════════════
-- AI CREDITS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE ai_credits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    credits_remaining INTEGER NOT NULL DEFAULT 0,
    credits_used_this_month INTEGER DEFAULT 0,
    reset_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE UNIQUE INDEX idx_ai_credits_user ON ai_credits(user_id);

-- ═══════════════════════════════════════════════════════════════
-- AI USAGE LOG TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE ai_usage_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    feature VARCHAR(50) NOT NULL,
    credits_used INTEGER NOT NULL,
    input_tokens INTEGER,
    output_tokens INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_ai_usage_user ON ai_usage_log(user_id);
CREATE INDEX idx_ai_usage_created ON ai_usage_log(created_at);

-- Apply updated_at trigger to ai_credits
CREATE TRIGGER update_ai_credits_updated_at
    BEFORE UPDATE ON ai_credits
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
