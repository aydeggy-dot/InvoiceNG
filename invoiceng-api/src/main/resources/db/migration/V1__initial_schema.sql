-- V1__initial_schema.sql
-- InvoiceNG Database Schema

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ═══════════════════════════════════════════════════════════════
-- USERS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(255),
    business_name VARCHAR(255),
    business_address TEXT,
    bank_name VARCHAR(100),
    bank_code VARCHAR(10),
    account_number VARCHAR(20),
    account_name VARCHAR(255),
    logo_url TEXT,
    subscription_tier VARCHAR(20) DEFAULT 'free',
    invoice_count_this_month INTEGER DEFAULT 0,
    invoice_count_reset_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for phone lookup
CREATE INDEX idx_users_phone ON users(phone);

-- ═══════════════════════════════════════════════════════════════
-- OTP REQUESTS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE otp_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone VARCHAR(15) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    pin_id VARCHAR(100),
    attempts INTEGER DEFAULT 0,
    verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for OTP lookup
CREATE INDEX idx_otp_phone_expires ON otp_requests(phone, expires_at);

-- ═══════════════════════════════════════════════════════════════
-- CUSTOMERS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(255),
    address TEXT,
    notes TEXT,
    payment_score INTEGER DEFAULT 100,
    total_invoices INTEGER DEFAULT 0,
    total_paid DECIMAL(15, 2) DEFAULT 0,
    total_outstanding DECIMAL(15, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, phone)
);

-- Indexes for customer queries
CREATE INDEX idx_customers_user ON customers(user_id);
CREATE INDEX idx_customers_phone ON customers(user_id, phone);
CREATE INDEX idx_customers_name ON customers(user_id, name);

-- ═══════════════════════════════════════════════════════════════
-- INVOICES TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_id UUID REFERENCES customers(id) ON DELETE SET NULL,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,

    -- Invoice items stored as JSONB
    -- Format: [{"id": "uuid", "name": "Item", "description": "Desc", "quantity": 1, "price": 50000, "total": 50000}]
    items JSONB NOT NULL DEFAULT '[]',

    -- Amounts (stored in Naira)
    subtotal DECIMAL(12, 2) NOT NULL,
    tax DECIMAL(12, 2) DEFAULT 0,
    discount DECIMAL(12, 2) DEFAULT 0,
    total DECIMAL(12, 2) NOT NULL,

    -- Status: draft, sent, viewed, paid, overdue, cancelled
    status VARCHAR(20) DEFAULT 'draft',

    -- Dates
    issue_date DATE DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,

    -- Notes
    notes TEXT,
    terms TEXT,

    -- Payment tracking
    payment_ref VARCHAR(100) UNIQUE,
    payment_link TEXT,
    paystack_access_code VARCHAR(100),

    -- Files
    pdf_url TEXT,

    -- Timestamps
    sent_at TIMESTAMP,
    viewed_at TIMESTAMP,
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for invoice queries
CREATE INDEX idx_invoices_user ON invoices(user_id);
CREATE INDEX idx_invoices_customer ON invoices(customer_id);
CREATE INDEX idx_invoices_status ON invoices(user_id, status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date) WHERE status NOT IN ('paid', 'cancelled');
CREATE INDEX idx_invoices_payment_ref ON invoices(payment_ref);
CREATE INDEX idx_invoices_number ON invoices(invoice_number);
CREATE INDEX idx_invoices_created ON invoices(user_id, created_at DESC);

-- GIN index for JSONB items search
CREATE INDEX idx_invoices_items ON invoices USING GIN (items);

-- ═══════════════════════════════════════════════════════════════
-- PAYMENTS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    amount DECIMAL(12, 2) NOT NULL,
    reference VARCHAR(100) NOT NULL,
    paystack_reference VARCHAR(100),
    channel VARCHAR(50),
    status VARCHAR(20) DEFAULT 'pending',
    paid_at TIMESTAMP,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for payment lookup
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_reference ON payments(reference);
CREATE INDEX idx_payments_status ON payments(status);

-- ═══════════════════════════════════════════════════════════════
-- REMINDERS TABLE
-- ═══════════════════════════════════════════════════════════════
CREATE TABLE reminders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending',
    channel VARCHAR(20) DEFAULT 'whatsapp',
    message TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for reminder queries
CREATE INDEX idx_reminders_scheduled ON reminders(scheduled_at) WHERE status = 'pending';
CREATE INDEX idx_reminders_invoice ON reminders(invoice_id);
CREATE INDEX idx_reminders_user ON reminders(user_id);

-- ═══════════════════════════════════════════════════════════════
-- UPDATED_AT TRIGGER FUNCTION
-- ═══════════════════════════════════════════════════════════════
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoices_updated_at
    BEFORE UPDATE ON invoices
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
