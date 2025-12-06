-- V2__add_invoice_schedules.sql
-- Recurring invoice schedules for Phase 2+

-- ═══════════════════════════════════════════════════════════════
-- INVOICE SCHEDULES TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE invoice_schedules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,

    -- Schedule details
    name VARCHAR(255) NOT NULL,
    items JSONB NOT NULL DEFAULT '[]',
    amount DECIMAL(12, 2) NOT NULL,

    -- Frequency: daily, weekly, biweekly, monthly, quarterly, yearly
    frequency VARCHAR(20) NOT NULL,
    day_of_week INTEGER,
    day_of_month INTEGER,

    -- Scheduling
    start_date DATE NOT NULL,
    end_date DATE,
    next_invoice_date DATE NOT NULL,
    last_invoice_date DATE,

    -- Approval
    requires_approval BOOLEAN DEFAULT FALSE,
    auto_send BOOLEAN DEFAULT TRUE,

    -- Status: active, paused, completed, cancelled
    status VARCHAR(20) DEFAULT 'active',

    -- Stats
    invoices_generated INTEGER DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_schedules_user ON invoice_schedules(user_id);
CREATE INDEX idx_schedules_customer ON invoice_schedules(customer_id);
CREATE INDEX idx_schedules_next_date ON invoice_schedules(next_invoice_date) WHERE status = 'active';
CREATE INDEX idx_schedules_status ON invoice_schedules(user_id, status);

-- ═══════════════════════════════════════════════════════════════
-- INVOICE APPROVALS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE invoice_approvals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    schedule_id UUID NOT NULL REFERENCES invoice_schedules(id) ON DELETE CASCADE,
    draft_invoice JSONB NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_approvals_pending ON invoice_approvals(status, expires_at) WHERE status = 'pending';
CREATE INDEX idx_approvals_schedule ON invoice_approvals(schedule_id);

-- Apply updated_at trigger
CREATE TRIGGER update_schedules_updated_at
    BEFORE UPDATE ON invoice_schedules
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
