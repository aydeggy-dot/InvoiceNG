# InvoiceNG - Claude Code Implementation Prompt

> **Project**: WhatsApp-based Invoice & Payment Collection SaaS for Nigerian SMEs
> **Target Market**: Lagos service providers (fashion designers, caterers, event vendors)
> **Stack**: Java Spring Boot + React + React Native (or Supabase + PWA for MVP)
> **Timeline**: MVP in 4-6 weeks

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technical Architecture](#2-technical-architecture)
3. [Database Schema](#3-database-schema)
4. [API Specification](#4-api-specification)
5. [Integration Guide](#5-integration-guide)
6. [Implementation Phases](#6-implementation-phases)
7. [Code Quality Standards](#7-code-quality-standards)
8. [Testing Requirements](#8-testing-requirements)
9. [Deployment Guide](#9-deployment-guide)
10. [Claude Code Task Prompts](#10-claude-code-task-prompts)

---

## 1. Project Overview

### 1.1 Problem Statement

Nigerian SMEs (especially service providers) lose ₦100K-300K monthly due to:
- No systematic way to track who owes money
- Embarrassment manually chasing payments
- Payment reconciliation chaos ("Did they pay?")
- Hours wasted scrolling WhatsApp for payment confirmations

### 1.2 Solution

A WhatsApp-native invoice and payment collection system that:
- Creates professional invoices in 3 taps
- Generates Paystack payment links
- Shares invoices via WhatsApp
- Auto-tracks payment status
- Sends automatic payment reminders
- Reconciles payments automatically

### 1.3 Target Customer Profile

```yaml
Business Type: Service providers (fashion, events, catering, photography)
Monthly Revenue: ₦500K - ₦5M
Monthly Orders: 20-50
Average Transaction: ₦50K - ₦500K
Pain Point: Loses ₦100K-300K monthly to forgotten/late payments
Location: Lagos, Nigeria (primary), expanding to other Nigerian cities
```

### 1.4 Core User Journey

```
1. Business owner receives order via WhatsApp
2. Opens InvoiceNG app → Creates invoice (customer, items, amount)
3. Taps "Send" → Invoice shared to customer via WhatsApp
4. Customer clicks payment link → Pays via Paystack
5. Business owner gets instant notification
6. Payment auto-reconciled in dashboard
7. If unpaid, automatic reminders sent
```

### 1.5 Success Metrics (MVP)

- 100 active users within 4 weeks of launch
- 50% of users create >5 invoices
- 30% payment collection rate improvement (user-reported)
- <3 minute time to create and send first invoice

---

## 2. Technical Architecture

### 2.1 Stack Decision Matrix

#### Option A: Supabase + PWA (Recommended for MVP)

```yaml
Frontend:
  - Framework: Next.js 14 (App Router)
  - Styling: Tailwind CSS
  - State: React Query + Zustand
  - PWA: next-pwa

Backend:
  - Platform: Supabase
  - Database: PostgreSQL (Supabase managed)
  - Auth: Supabase Auth (Phone OTP)
  - Storage: Supabase Storage
  - Functions: Supabase Edge Functions (Deno)

Integrations:
  - Payments: Paystack API
  - SMS: Termii API
  - WhatsApp: Web Share API (free)
  - PDF: @react-pdf/renderer (client-side)

Hosting:
  - Frontend: Vercel (free tier)
  - Backend: Supabase (free tier)

Timeline: 3-4 weeks
Monthly Cost: ₦0-8,000 (domain only)
```

#### Option B: Java Spring Boot + React + React Native (Production Scale)

```yaml
Backend:
  - Framework: Spring Boot 3.2
  - Language: Java 21
  - Database: PostgreSQL 16
  - ORM: Spring Data JPA + Hibernate
  - Migrations: Flyway
  - Security: Spring Security + JWT
  - API Docs: SpringDoc OpenAPI

Frontend Web:
  - Framework: React 18 + TypeScript
  - Build: Vite
  - Styling: Tailwind CSS
  - State: React Query + Zustand
  - Forms: React Hook Form + Zod

Mobile:
  - Framework: React Native + Expo
  - Navigation: React Navigation
  - State: React Query + Zustand

Integrations:
  - Payments: Paystack API
  - SMS: Termii API
  - WhatsApp: Web Share API → WhatsApp Business API (Phase 3)
  - PDF: Flying Saucer + Thymeleaf
  - Email: Resend
  - AI: Claude API (Phase 3)

Hosting:
  - Backend: Railway ($5-15/month)
  - Database: Railway PostgreSQL (included)
  - Frontend: Vercel (free)
  - Storage: Cloudflare R2 ($0-5/month)

Timeline: 8-12 weeks
Monthly Cost: ₦25,000-50,000
```

### 2.2 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT LAYER                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐        │
│   │   React Web     │    │  React Native   │    │   PWA (Alt)     │        │
│   │   (Vite)        │    │  (Expo)         │    │   (Next.js)     │        │
│   └────────┬────────┘    └────────┬────────┘    └────────┬────────┘        │
│            │                      │                      │                  │
│            └──────────────────────┼──────────────────────┘                  │
│                                   │                                         │
│                                   ▼                                         │
│                         ┌─────────────────┐                                │
│                         │   API Gateway   │                                │
│                         │   (HTTPS/JWT)   │                                │
│                         └────────┬────────┘                                │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                              SERVICE LAYER                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                   │                                         │
│   ┌───────────────────────────────┼───────────────────────────────┐        │
│   │                               ▼                               │        │
│   │                    ┌─────────────────┐                        │        │
│   │                    │  Spring Boot    │                        │        │
│   │                    │  Application    │                        │        │
│   │                    └────────┬────────┘                        │        │
│   │                             │                                 │        │
│   │   ┌─────────────────────────┼─────────────────────────┐      │        │
│   │   │                         │                         │      │        │
│   │   ▼                         ▼                         ▼      │        │
│   │ ┌──────────┐         ┌──────────┐              ┌──────────┐  │        │
│   │ │ Auth     │         │ Invoice  │              │ Payment  │  │        │
│   │ │ Service  │         │ Service  │              │ Service  │  │        │
│   │ └──────────┘         └──────────┘              └──────────┘  │        │
│   │                                                              │        │
│   │ ┌──────────┐         ┌──────────┐              ┌──────────┐  │        │
│   │ │ Customer │         │ PDF      │              │ Reminder │  │        │
│   │ │ Service  │         │ Service  │              │ Service  │  │        │
│   │ └──────────┘         └──────────┘              └──────────┘  │        │
│   │                                                              │        │
│   └──────────────────────────────────────────────────────────────┘        │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                            INTEGRATION LAYER                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│   │ Paystack │  │ Termii   │  │ WhatsApp │  │ Resend   │  │ Claude   │    │
│   │ (Payment)│  │ (SMS)    │  │ (Share)  │  │ (Email)  │  │ (AI)     │    │
│   └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘    │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                              DATA LAYER                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐        │
│   │   PostgreSQL    │    │  Cloudflare R2  │    │     Redis       │        │
│   │   (Primary DB)  │    │  (File Storage) │    │  (Cache/Queue)  │        │
│   └─────────────────┘    └─────────────────┘    └─────────────────┘        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Project Structure

#### Java Backend Structure

```
invoiceng-api/
├── src/
│   ├── main/
│   │   ├── java/com/invoiceng/
│   │   │   ├── InvoiceNgApplication.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   └── PaystackConfig.java
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── InvoiceController.java
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── PaymentController.java
│   │   │   │   ├── WebhookController.java
│   │   │   │   └── DashboardController.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── InvoiceService.java
│   │   │   │   ├── CustomerService.java
│   │   │   │   ├── PaymentService.java
│   │   │   │   ├── PaystackService.java
│   │   │   │   ├── SmsService.java
│   │   │   │   ├── PdfService.java
│   │   │   │   ├── ReminderService.java
│   │   │   │   └── StorageService.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── InvoiceRepository.java
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── PaymentRepository.java
│   │   │   │   └── ReminderRepository.java
│   │   │   │
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Invoice.java
│   │   │   │   ├── InvoiceItem.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Payment.java
│   │   │   │   └── Reminder.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── VerifyOtpRequest.java
│   │   │   │   │   ├── CreateInvoiceRequest.java
│   │   │   │   │   └── CreateCustomerRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── ApiResponse.java
│   │   │   │       ├── AuthResponse.java
│   │   │   │       ├── InvoiceResponse.java
│   │   │   │       └── DashboardResponse.java
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   └── PaymentException.java
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── UserPrincipal.java
│   │   │   │
│   │   │   └── util/
│   │   │       ├── InvoiceNumberGenerator.java
│   │   │       ├── PhoneNumberFormatter.java
│   │   │       └── CurrencyFormatter.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/migration/
│   │       │   ├── V1__initial_schema.sql
│   │       │   ├── V2__add_customers.sql
│   │       │   └── V3__add_reminders.sql
│   │       └── templates/
│   │           └── invoice-template.html
│   │
│   └── test/
│       └── java/com/invoiceng/
│           ├── controller/
│           ├── service/
│           └── integration/
│
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

#### React Web Structure

```
invoiceng-web/
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   │
│   ├── api/
│   │   ├── client.ts
│   │   ├── auth.ts
│   │   ├── invoices.ts
│   │   ├── customers.ts
│   │   └── payments.ts
│   │
│   ├── components/
│   │   ├── ui/
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Card.tsx
│   │   │   ├── Modal.tsx
│   │   │   ├── Toast.tsx
│   │   │   └── Loading.tsx
│   │   ├── layout/
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── MobileNav.tsx
│   │   │   └── Layout.tsx
│   │   ├── invoice/
│   │   │   ├── InvoiceForm.tsx
│   │   │   ├── InvoiceList.tsx
│   │   │   ├── InvoiceCard.tsx
│   │   │   ├── InvoiceDetail.tsx
│   │   │   └── InvoiceItemRow.tsx
│   │   ├── customer/
│   │   │   ├── CustomerSelect.tsx
│   │   │   ├── CustomerForm.tsx
│   │   │   └── CustomerList.tsx
│   │   └── dashboard/
│   │       ├── StatsCard.tsx
│   │       ├── RecentInvoices.tsx
│   │       └── RevenueChart.tsx
│   │
│   ├── pages/
│   │   ├── auth/
│   │   │   ├── Login.tsx
│   │   │   └── VerifyOtp.tsx
│   │   ├── Dashboard.tsx
│   │   ├── Invoices.tsx
│   │   ├── CreateInvoice.tsx
│   │   ├── InvoiceDetail.tsx
│   │   ├── Customers.tsx
│   │   ├── Settings.tsx
│   │   └── NotFound.tsx
│   │
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useInvoices.ts
│   │   ├── useCustomers.ts
│   │   ├── useWhatsAppShare.ts
│   │   └── useToast.ts
│   │
│   ├── store/
│   │   ├── authStore.ts
│   │   └── uiStore.ts
│   │
│   ├── utils/
│   │   ├── formatCurrency.ts
│   │   ├── formatDate.ts
│   │   ├── formatPhone.ts
│   │   └── validators.ts
│   │
│   ├── types/
│   │   ├── invoice.ts
│   │   ├── customer.ts
│   │   ├── user.ts
│   │   └── api.ts
│   │
│   └── styles/
│       └── globals.css
│
├── public/
│   ├── manifest.json
│   └── icons/
│
├── index.html
├── package.json
├── tsconfig.json
├── tailwind.config.js
├── vite.config.ts
└── README.md
```

#### React Native Structure

```
invoiceng-mobile/
├── src/
│   ├── App.tsx
│   │
│   ├── api/
│   │   └── (same as web)
│   │
│   ├── components/
│   │   ├── ui/
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Card.tsx
│   │   │   └── Loading.tsx
│   │   ├── invoice/
│   │   │   ├── InvoiceForm.tsx
│   │   │   ├── InvoiceList.tsx
│   │   │   └── InvoiceCard.tsx
│   │   └── customer/
│   │       ├── CustomerPicker.tsx
│   │       └── CustomerForm.tsx
│   │
│   ├── screens/
│   │   ├── auth/
│   │   │   ├── LoginScreen.tsx
│   │   │   └── OtpScreen.tsx
│   │   ├── DashboardScreen.tsx
│   │   ├── InvoicesScreen.tsx
│   │   ├── CreateInvoiceScreen.tsx
│   │   ├── InvoiceDetailScreen.tsx
│   │   ├── CustomersScreen.tsx
│   │   └── SettingsScreen.tsx
│   │
│   ├── navigation/
│   │   ├── AppNavigator.tsx
│   │   ├── AuthNavigator.tsx
│   │   └── MainNavigator.tsx
│   │
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useWhatsAppShare.ts
│   │   └── useNotifications.ts
│   │
│   ├── store/
│   │   └── (same as web)
│   │
│   ├── utils/
│   │   └── (same as web)
│   │
│   └── types/
│       └── (same as web)
│
├── app.json
├── package.json
├── tsconfig.json
├── babel.config.js
└── README.md
```

---

## 3. Database Schema

### 3.1 Entity Relationship Diagram

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     users       │       │   customers     │       │    invoices     │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │───┐   │ id (PK)         │───┐   │ id (PK)         │
│ phone           │   │   │ user_id (FK)    │◄──┼───│ user_id (FK)    │
│ email           │   │   │ name            │   │   │ customer_id(FK) │◄──┐
│ business_name   │   │   │ phone           │   │   │ invoice_number  │   │
│ business_address│   │   │ email           │   │   │ items (JSONB)   │   │
│ bank_name       │   │   │ address         │   │   │ subtotal        │   │
│ bank_code       │   │   │ notes           │   │   │ tax             │   │
│ account_number  │   │   │ payment_score   │   │   │ total           │   │
│ account_name    │   │   │ total_invoices  │   │   │ status          │   │
│ logo_url        │   │   │ total_paid      │   │   │ due_date        │   │
│ subscription_tier│  │   │ created_at      │   │   │ notes           │   │
│ created_at      │   │   │ updated_at      │   │   │ payment_ref     │   │
│ updated_at      │   │   └─────────────────┘   │   │ payment_link    │   │
└─────────────────┘   │                         │   │ pdf_url         │   │
        │             │                         │   │ paid_at         │   │
        │             │                         │   │ created_at      │   │
        │             │                         │   │ updated_at      │   │
        │             │                         │   └─────────────────┘   │
        │             │                         │           │             │
        │             │                         │           │             │
        │             │                         │           ▼             │
        │             │                         │   ┌─────────────────┐   │
        │             │                         │   │    payments     │   │
        │             │                         │   ├─────────────────┤   │
        │             │                         │   │ id (PK)         │   │
        │             │                         └───│ invoice_id (FK) │   │
        │             │                             │ amount          │   │
        │             │                             │ reference       │   │
        │             │                             │ channel         │   │
        │             │                             │ status          │   │
        │             │                             │ paystack_ref    │   │
        │             │                             │ paid_at         │   │
        │             │                             │ created_at      │   │
        │             │                             └─────────────────┘   │
        │             │                                                   │
        │             │   ┌─────────────────┐       ┌─────────────────┐   │
        │             │   │   reminders     │       │invoice_schedules│   │
        │             │   ├─────────────────┤       ├─────────────────┤   │
        │             │   │ id (PK)         │       │ id (PK)         │   │
        │             └───│ user_id (FK)    │       │ user_id (FK)    │───┘
        │                 │ invoice_id (FK) │       │ customer_id(FK) │
        │                 │ type            │       │ name            │
        │                 │ scheduled_at    │       │ items (JSONB)   │
        │                 │ sent_at         │       │ amount          │
        │                 │ status          │       │ frequency       │
        │                 │ channel         │       │ day_of_month    │
        │                 │ created_at      │       │ next_invoice_at │
        │                 └─────────────────┘       │ requires_approval│
        │                                           │ status          │
        │                                           │ created_at      │
        │                 ┌─────────────────┐       └─────────────────┘
        │                 │  otp_requests   │
        │                 ├─────────────────┤
        │                 │ id (PK)         │
        └─────────────────│ phone           │
                          │ otp_hash        │
                          │ pin_id          │
                          │ attempts        │
                          │ verified        │
                          │ expires_at      │
                          │ created_at      │
                          └─────────────────┘
```

### 3.2 SQL Schema (Flyway Migrations)

```sql
-- V1__initial_schema.sql

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
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

-- OTP requests table
CREATE TABLE otp_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone VARCHAR(15) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    pin_id VARCHAR(100), -- Termii pin_id for verification
    attempts INTEGER DEFAULT 0,
    verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for OTP lookup
CREATE INDEX idx_otp_phone_expires ON otp_requests(phone, expires_at);

-- Customers table
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(255),
    address TEXT,
    notes TEXT,
    payment_score INTEGER DEFAULT 100, -- 0-100, higher is better
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

-- Invoices table
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_id UUID REFERENCES customers(id) ON DELETE SET NULL,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- Invoice items stored as JSONB
    -- Format: [{"name": "Dress", "quantity": 1, "price": 50000, "description": "Custom ankara dress"}]
    items JSONB NOT NULL DEFAULT '[]',
    
    -- Amounts
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
    payment_ref VARCHAR(100) UNIQUE, -- Our internal reference
    payment_link TEXT, -- Paystack payment link
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

-- Payments table (for tracking all payment attempts/records)
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    amount DECIMAL(12, 2) NOT NULL,
    reference VARCHAR(100) NOT NULL, -- Our reference
    paystack_reference VARCHAR(100), -- Paystack's reference
    channel VARCHAR(50), -- card, bank_transfer, ussd
    status VARCHAR(20) DEFAULT 'pending', -- pending, success, failed
    paid_at TIMESTAMP,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for payment lookup
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_reference ON payments(reference);

-- Reminders table
CREATE TABLE reminders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL, -- before_due, on_due, after_due
    scheduled_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending', -- pending, sent, failed, cancelled
    channel VARCHAR(20) DEFAULT 'whatsapp', -- whatsapp, sms, email
    message TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for reminder queries
CREATE INDEX idx_reminders_scheduled ON reminders(scheduled_at) WHERE status = 'pending';
CREATE INDEX idx_reminders_invoice ON reminders(invoice_id);

-- Updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE ON invoices
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

```sql
-- V2__add_invoice_schedules.sql

-- Recurring invoice schedules
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
    day_of_week INTEGER, -- 0-6 for weekly
    day_of_month INTEGER, -- 1-31 for monthly
    
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
CREATE INDEX idx_schedules_next_date ON invoice_schedules(next_invoice_date) WHERE status = 'active';

-- Pending approvals for scheduled invoices
CREATE TABLE invoice_approvals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    schedule_id UUID NOT NULL REFERENCES invoice_schedules(id) ON DELETE CASCADE,
    draft_invoice JSONB NOT NULL,
    status VARCHAR(20) DEFAULT 'pending', -- pending, approved, rejected, expired
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_approvals_pending ON invoice_approvals(status, expires_at) WHERE status = 'pending';

-- Apply updated_at trigger
CREATE TRIGGER update_schedules_updated_at BEFORE UPDATE ON invoice_schedules
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

```sql
-- V3__add_subscriptions.sql

-- Subscription tiers and limits
CREATE TABLE subscription_tiers (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    monthly_price DECIMAL(10, 2) NOT NULL,
    invoice_limit INTEGER, -- NULL = unlimited
    reminder_limit INTEGER, -- per invoice
    pdf_enabled BOOLEAN DEFAULT FALSE,
    recurring_enabled BOOLEAN DEFAULT FALSE,
    ai_credits_monthly INTEGER DEFAULT 0,
    team_members_limit INTEGER DEFAULT 1,
    transaction_fee_percent DECIMAL(4, 2) NOT NULL,
    transaction_fee_flat DECIMAL(6, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default tiers
INSERT INTO subscription_tiers (id, name, monthly_price, invoice_limit, reminder_limit, pdf_enabled, recurring_enabled, ai_credits_monthly, team_members_limit, transaction_fee_percent, transaction_fee_flat) VALUES
('free', 'Free', 0, 10, 0, FALSE, FALSE, 5, 1, 2.00, 100),
('starter', 'Starter', 3000, 50, 3, TRUE, FALSE, 20, 1, 1.50, 100),
('pro', 'Pro', 7500, NULL, NULL, TRUE, TRUE, 75, 1, 1.00, 50),
('business', 'Business', 20000, NULL, NULL, TRUE, TRUE, 200, 5, 0.75, 25);

-- User subscriptions
CREATE TABLE user_subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tier_id VARCHAR(20) NOT NULL REFERENCES subscription_tiers(id),
    status VARCHAR(20) DEFAULT 'active', -- active, cancelled, expired
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    paystack_subscription_code VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI credits tracking
CREATE TABLE ai_credits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    credits_remaining INTEGER NOT NULL DEFAULT 0,
    credits_used_this_month INTEGER DEFAULT 0,
    reset_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI usage log
CREATE TABLE ai_usage_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    feature VARCHAR(50) NOT NULL, -- chat_extraction, smart_description, voice_to_invoice
    credits_used INTEGER NOT NULL,
    input_tokens INTEGER,
    output_tokens INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_subscriptions_user ON user_subscriptions(user_id);
CREATE INDEX idx_ai_credits_user ON ai_credits(user_id);
```

### 3.3 JSONB Item Schema

```typescript
// Invoice items JSONB structure
interface InvoiceItem {
  id: string;          // UUID for each item
  name: string;        // "Custom Ankara Dress"
  description?: string; // "Red and gold pattern, knee length"
  quantity: number;    // 1
  price: number;       // 50000 (in Naira, no decimals for simplicity)
  total: number;       // quantity * price
}

// Example:
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Custom Ankara Dress",
    "description": "Red and gold pattern, knee length",
    "quantity": 1,
    "price": 50000,
    "total": 50000
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Alterations",
    "description": "Sleeve adjustment",
    "quantity": 1,
    "price": 5000,
    "total": 5000
  }
]
```

---

## 4. API Specification

### 4.1 Authentication Endpoints

```yaml
# Auth API

POST /api/v1/auth/request-otp:
  description: Request OTP for phone number
  request:
    body:
      phone: string # "08012345678" or "+2348012345678"
  response:
    200:
      message: "OTP sent successfully"
      expiresIn: 600 # seconds
    429:
      error: "Too many requests. Try again in {seconds} seconds"

POST /api/v1/auth/verify-otp:
  description: Verify OTP and get access token
  request:
    body:
      phone: string
      otp: string # 6 digits
  response:
    200:
      token: string # JWT
      user:
        id: uuid
        phone: string
        businessName: string | null
        isNewUser: boolean
    401:
      error: "Invalid or expired OTP"

POST /api/v1/auth/refresh:
  description: Refresh access token
  headers:
    Authorization: "Bearer {refreshToken}"
  response:
    200:
      token: string
      expiresIn: number

POST /api/v1/auth/logout:
  description: Invalidate refresh token
  headers:
    Authorization: "Bearer {token}"
  response:
    200:
      message: "Logged out successfully"
```

### 4.2 User Endpoints

```yaml
# User API

GET /api/v1/users/me:
  description: Get current user profile
  headers:
    Authorization: "Bearer {token}"
  response:
    200:
      id: uuid
      phone: string
      email: string | null
      businessName: string | null
      businessAddress: string | null
      bankName: string | null
      bankCode: string | null
      accountNumber: string | null
      accountName: string | null
      logoUrl: string | null
      subscriptionTier: "free" | "starter" | "pro" | "business"
      invoiceCountThisMonth: number
      createdAt: timestamp

PUT /api/v1/users/me:
  description: Update user profile
  headers:
    Authorization: "Bearer {token}"
  request:
    body:
      email?: string
      businessName?: string
      businessAddress?: string
      bankName?: string
      bankCode?: string
      accountNumber?: string
      accountName?: string
  response:
    200:
      # Updated user object

POST /api/v1/users/me/logo:
  description: Upload business logo
  headers:
    Authorization: "Bearer {token}"
    Content-Type: multipart/form-data
  request:
    body:
      logo: File # PNG, JPG, max 2MB
  response:
    200:
      logoUrl: string
```

### 4.3 Customer Endpoints

```yaml
# Customer API

GET /api/v1/customers:
  description: List all customers
  headers:
    Authorization: "Bearer {token}"
  query:
    search?: string # Search by name or phone
    page?: number # Default 1
    limit?: number # Default 20, max 100
    sortBy?: "name" | "createdAt" | "totalPaid"
    sortOrder?: "asc" | "desc"
  response:
    200:
      data:
        - id: uuid
          name: string
          phone: string
          email: string | null
          totalInvoices: number
          totalPaid: number
          totalOutstanding: number
          paymentScore: number
          createdAt: timestamp
      pagination:
        page: number
        limit: number
        total: number
        totalPages: number

GET /api/v1/customers/{id}:
  description: Get customer details
  response:
    200:
      id: uuid
      name: string
      phone: string
      email: string | null
      address: string | null
      notes: string | null
      totalInvoices: number
      totalPaid: number
      totalOutstanding: number
      paymentScore: number
      recentInvoices:
        - id: uuid
          invoiceNumber: string
          total: number
          status: string
          createdAt: timestamp
      createdAt: timestamp

POST /api/v1/customers:
  description: Create new customer
  request:
    body:
      name: string # Required
      phone: string # Required
      email?: string
      address?: string
      notes?: string
  response:
    201:
      # Customer object

PUT /api/v1/customers/{id}:
  description: Update customer
  request:
    body:
      name?: string
      phone?: string
      email?: string
      address?: string
      notes?: string
  response:
    200:
      # Updated customer object

DELETE /api/v1/customers/{id}:
  description: Delete customer
  response:
    204: # No content
```

### 4.4 Invoice Endpoints

```yaml
# Invoice API

GET /api/v1/invoices:
  description: List all invoices
  headers:
    Authorization: "Bearer {token}"
  query:
    status?: "draft" | "sent" | "viewed" | "paid" | "overdue" | "cancelled"
    customerId?: uuid
    fromDate?: date
    toDate?: date
    search?: string # Search invoice number
    page?: number
    limit?: number
    sortBy?: "createdAt" | "dueDate" | "total"
    sortOrder?: "asc" | "desc"
  response:
    200:
      data:
        - id: uuid
          invoiceNumber: string
          customer:
            id: uuid
            name: string
            phone: string
          items: InvoiceItem[]
          subtotal: number
          tax: number
          total: number
          status: string
          dueDate: date
          paymentLink: string | null
          pdfUrl: string | null
          createdAt: timestamp
      pagination:
        page: number
        limit: number
        total: number
        totalPages: number
      summary:
        totalAmount: number
        paidAmount: number
        pendingAmount: number
        overdueAmount: number

GET /api/v1/invoices/{id}:
  description: Get invoice details
  response:
    200:
      id: uuid
      invoiceNumber: string
      customer:
        id: uuid
        name: string
        phone: string
        email: string | null
      items: InvoiceItem[]
      subtotal: number
      tax: number
      discount: number
      total: number
      status: string
      issueDate: date
      dueDate: date
      notes: string | null
      terms: string | null
      paymentRef: string
      paymentLink: string
      pdfUrl: string | null
      sentAt: timestamp | null
      viewedAt: timestamp | null
      paidAt: timestamp | null
      payments:
        - id: uuid
          amount: number
          channel: string
          status: string
          paidAt: timestamp
      reminders:
        - id: uuid
          type: string
          scheduledAt: timestamp
          status: string
      createdAt: timestamp

POST /api/v1/invoices:
  description: Create new invoice
  request:
    body:
      customerId?: uuid # Either customerId or customerData
      customerData?: # Create new customer inline
        name: string
        phone: string
        email?: string
      items:
        - name: string
          description?: string
          quantity: number
          price: number
      tax?: number # Percentage, e.g., 7.5 for VAT
      discount?: number # Flat amount
      dueDate: date # YYYY-MM-DD
      notes?: string
      terms?: string
      sendImmediately?: boolean # Generate payment link
  response:
    201:
      id: uuid
      invoiceNumber: string
      # Full invoice object
      paymentLink: string # If sendImmediately
      whatsappMessage: string # Pre-formatted message

PUT /api/v1/invoices/{id}:
  description: Update draft invoice
  request:
    body:
      # Same as create, all fields optional
  response:
    200:
      # Updated invoice object
  errors:
    400: "Cannot edit sent/paid invoice"

DELETE /api/v1/invoices/{id}:
  description: Delete draft invoice
  response:
    204: # No content
  errors:
    400: "Cannot delete sent/paid invoice"

POST /api/v1/invoices/{id}/send:
  description: Mark as sent and generate payment link
  response:
    200:
      paymentLink: string
      whatsappMessage: string
      status: "sent"

POST /api/v1/invoices/{id}/cancel:
  description: Cancel invoice
  response:
    200:
      status: "cancelled"

POST /api/v1/invoices/{id}/duplicate:
  description: Create copy of invoice
  response:
    201:
      # New invoice object (as draft)

GET /api/v1/invoices/{id}/pdf:
  description: Generate/get PDF
  response:
    200:
      pdfUrl: string
```

### 4.5 Payment Endpoints

```yaml
# Payment API

POST /api/v1/payments/initialize:
  description: Initialize payment for invoice
  request:
    body:
      invoiceId: uuid
      callbackUrl?: string
  response:
    200:
      authorizationUrl: string # Paystack checkout URL
      accessCode: string
      reference: string

GET /api/v1/payments/verify/{reference}:
  description: Verify payment status
  response:
    200:
      status: "success" | "pending" | "failed"
      amount: number
      paidAt: timestamp | null
      channel: string | null

# Webhook (called by Paystack)
POST /api/v1/webhooks/paystack:
  description: Paystack payment webhook
  headers:
    x-paystack-signature: string
  request:
    body:
      event: "charge.success" | "charge.failed"
      data:
        reference: string
        amount: number
        channel: string
        paid_at: string
        # ... other Paystack data
  response:
    200: # Always return 200 to acknowledge
```

### 4.6 Dashboard Endpoints

```yaml
# Dashboard API

GET /api/v1/dashboard/stats:
  description: Get dashboard statistics
  headers:
    Authorization: "Bearer {token}"
  query:
    period?: "week" | "month" | "quarter" | "year" # Default: month
  response:
    200:
      overview:
        totalRevenue: number
        totalInvoices: number
        paidInvoices: number
        pendingInvoices: number
        overdueInvoices: number
        collectionRate: number # Percentage
      comparison:
        revenueChange: number # Percentage vs previous period
        invoiceChange: number
      recentActivity:
        - type: "invoice_created" | "payment_received" | "invoice_overdue"
          invoiceId: uuid
          invoiceNumber: string
          customerName: string
          amount: number
          timestamp: timestamp

GET /api/v1/dashboard/revenue-chart:
  description: Get revenue chart data
  query:
    period?: "week" | "month" | "quarter" | "year"
  response:
    200:
      data:
        - date: date
          revenue: number
          invoiceCount: number

GET /api/v1/dashboard/top-customers:
  description: Get top customers by revenue
  query:
    limit?: number # Default 5
  response:
    200:
      data:
        - customerId: uuid
          customerName: string
          totalPaid: number
          invoiceCount: number
```

### 4.7 Reminder Endpoints (Phase 2+)

```yaml
# Reminder API

GET /api/v1/invoices/{id}/reminders:
  description: Get reminders for invoice
  response:
    200:
      data:
        - id: uuid
          type: "before_due" | "on_due" | "after_due"
          scheduledAt: timestamp
          sentAt: timestamp | null
          status: "pending" | "sent" | "failed" | "cancelled"
          channel: "whatsapp" | "sms" | "email"

POST /api/v1/invoices/{id}/reminders:
  description: Schedule manual reminder
  request:
    body:
      scheduledAt: timestamp
      channel?: "whatsapp" | "sms" | "email"
      message?: string # Custom message
  response:
    201:
      # Reminder object

DELETE /api/v1/reminders/{id}:
  description: Cancel scheduled reminder
  response:
    204:
```

---

## 5. Integration Guide

### 5.1 Paystack Integration

```java
// PaystackConfig.java
@Configuration
@ConfigurationProperties(prefix = "paystack")
@Data
public class PaystackConfig {
    private String secretKey;
    private String publicKey;
    private String baseUrl = "https://api.paystack.co";
    private String callbackUrl;
}

// PaystackService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackService {
    
    private final PaystackConfig config;
    private final WebClient webClient;
    
    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
            .baseUrl(config.getBaseUrl())
            .defaultHeader("Authorization", "Bearer " + config.getSecretKey())
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    
    /**
     * Initialize a payment transaction
     */
    public PaystackInitResponse initializeTransaction(
            String reference,
            BigDecimal amount,
            String email,
            String customerName,
            Map<String, String> metadata
    ) {
        // Convert to kobo (smallest unit)
        int amountInKobo = amount.multiply(BigDecimal.valueOf(100)).intValue();
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("reference", reference);
        requestBody.put("amount", amountInKobo);
        requestBody.put("email", email);
        requestBody.put("callback_url", config.getCallbackUrl());
        requestBody.put("channels", List.of("card", "bank", "ussd", "bank_transfer"));
        
        // Add metadata
        Map<String, Object> meta = new HashMap<>(metadata);
        meta.put("customer_name", customerName);
        requestBody.put("metadata", meta);
        
        try {
            PaystackApiResponse response = webClient.post()
                .uri("/transaction/initialize")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PaystackApiResponse.class)
                .block();
            
            if (response != null && response.isStatus()) {
                return PaystackInitResponse.builder()
                    .authorizationUrl(response.getData().getAuthorizationUrl())
                    .accessCode(response.getData().getAccessCode())
                    .reference(response.getData().getReference())
                    .build();
            }
            
            throw new PaymentException("Failed to initialize payment: " + 
                (response != null ? response.getMessage() : "Unknown error"));
                
        } catch (WebClientResponseException e) {
            log.error("Paystack API error: {}", e.getResponseBodyAsString());
            throw new PaymentException("Payment service error", e);
        }
    }
    
    /**
     * Verify a transaction
     */
    public PaystackVerifyResponse verifyTransaction(String reference) {
        try {
            PaystackApiResponse response = webClient.get()
                .uri("/transaction/verify/{reference}", reference)
                .retrieve()
                .bodyToMono(PaystackApiResponse.class)
                .block();
            
            if (response != null && response.isStatus()) {
                PaystackData data = response.getData();
                return PaystackVerifyResponse.builder()
                    .status(data.getStatus())
                    .reference(data.getReference())
                    .amount(BigDecimal.valueOf(data.getAmount()).divide(BigDecimal.valueOf(100)))
                    .channel(data.getChannel())
                    .paidAt(data.getPaidAt())
                    .build();
            }
            
            return PaystackVerifyResponse.builder()
                .status("failed")
                .build();
                
        } catch (WebClientResponseException e) {
            log.error("Paystack verify error: {}", e.getResponseBodyAsString());
            throw new PaymentException("Failed to verify payment", e);
        }
    }
    
    /**
     * Verify webhook signature
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(
                config.getSecretKey().getBytes(StandardCharsets.UTF_8), 
                "HmacSHA512"
            );
            sha512Hmac.init(keySpec);
            byte[] hash = sha512Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Hex.encodeHexString(hash);
            return computedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }
}

// WebhookController.java
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    
    private final PaystackService paystackService;
    private final PaymentService paymentService;
    
    @PostMapping("/paystack")
    public ResponseEntity<Void> handlePaystackWebhook(
            @RequestBody String payload,
            @RequestHeader("x-paystack-signature") String signature
    ) {
        // Verify signature
        if (!paystackService.verifyWebhookSignature(payload, signature)) {
            log.warn("Invalid Paystack webhook signature");
            return ResponseEntity.status(401).build();
        }
        
        try {
            PaystackWebhookEvent event = objectMapper.readValue(payload, PaystackWebhookEvent.class);
            
            switch (event.getEvent()) {
                case "charge.success":
                    paymentService.handleSuccessfulPayment(event.getData());
                    break;
                case "charge.failed":
                    paymentService.handleFailedPayment(event.getData());
                    break;
                default:
                    log.info("Unhandled Paystack event: {}", event.getEvent());
            }
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error processing Paystack webhook", e);
            // Still return 200 to prevent retries
            return ResponseEntity.ok().build();
        }
    }
}
```

### 5.2 Termii SMS Integration

```java
// SmsConfig.java
@Configuration
@ConfigurationProperties(prefix = "termii")
@Data
public class SmsConfig {
    private String apiKey;
    private String senderId;
    private String baseUrl = "https://api.ng.termii.com";
}

// SmsService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {
    
    private final SmsConfig config;
    private final WebClient webClient;
    
    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
            .baseUrl(config.getBaseUrl())
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    
    /**
     * Send OTP for phone verification
     */
    public OtpResponse sendOtp(String phoneNumber) {
        String formattedPhone = formatPhoneNumber(phoneNumber);
        
        Map<String, Object> requestBody = Map.of(
            "api_key", config.getApiKey(),
            "message_type", "NUMERIC",
            "to", formattedPhone,
            "from", config.getSenderId(),
            "channel", "generic",
            "pin_attempts", 3,
            "pin_time_to_live", 10,
            "pin_length", 6,
            "pin_placeholder", "< 1234 >",
            "message_text", "Your InvoiceNG verification code is < 1234 >. Valid for 10 minutes. Do not share this code.",
            "pin_type", "NUMERIC"
        );
        
        try {
            TermiiOtpResponse response = webClient.post()
                .uri("/api/sms/otp/send")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(TermiiOtpResponse.class)
                .block();
            
            if (response != null && "200".equals(response.getStatus())) {
                return OtpResponse.builder()
                    .pinId(response.getPinId())
                    .phone(formattedPhone)
                    .expiresAt(Instant.now().plusSeconds(600))
                    .build();
            }
            
            throw new SmsException("Failed to send OTP");
            
        } catch (WebClientResponseException e) {
            log.error("Termii API error: {}", e.getResponseBodyAsString());
            throw new SmsException("SMS service error", e);
        }
    }
    
    /**
     * Verify OTP
     */
    public boolean verifyOtp(String pinId, String otp) {
        Map<String, Object> requestBody = Map.of(
            "api_key", config.getApiKey(),
            "pin_id", pinId,
            "pin", otp
        );
        
        try {
            TermiiVerifyResponse response = webClient.post()
                .uri("/api/sms/otp/verify")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(TermiiVerifyResponse.class)
                .block();
            
            return response != null && "Verified".equals(response.getVerified());
            
        } catch (WebClientResponseException e) {
            log.error("Termii verify error: {}", e.getResponseBodyAsString());
            return false;
        }
    }
    
    /**
     * Send regular SMS (for reminders)
     */
    public void sendSms(String phoneNumber, String message) {
        String formattedPhone = formatPhoneNumber(phoneNumber);
        
        Map<String, Object> requestBody = Map.of(
            "api_key", config.getApiKey(),
            "to", formattedPhone,
            "from", config.getSenderId(),
            "sms", message,
            "type", "plain",
            "channel", "generic"
        );
        
        try {
            webClient.post()
                .uri("/api/sms/send")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
                
        } catch (WebClientResponseException e) {
            log.error("Failed to send SMS: {}", e.getResponseBodyAsString());
            throw new SmsException("Failed to send SMS", e);
        }
    }
    
    /**
     * Format Nigerian phone number to international format
     */
    private String formatPhoneNumber(String phone) {
        // Remove any spaces or dashes
        phone = phone.replaceAll("[\\s-]", "");
        
        // Convert 08012345678 to 2348012345678
        if (phone.startsWith("0")) {
            return "234" + phone.substring(1);
        }
        
        // Already international format with +
        if (phone.startsWith("+")) {
            return phone.substring(1);
        }
        
        return phone;
    }
}
```

### 5.3 WhatsApp Share (Web Share API)

```typescript
// hooks/useWhatsAppShare.ts
import { useState, useCallback } from 'react';

interface ShareData {
  invoiceNumber: string;
  customerName: string;
  customerPhone: string;
  total: number;
  dueDate: string;
  paymentLink: string;
  businessName?: string;
}

export function useWhatsAppShare() {
  const [isSharing, setIsSharing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-NG', {
      style: 'currency',
      currency: 'NGN',
      minimumFractionDigits: 0,
    }).format(amount);
  };

  const generateMessage = (data: ShareData): string => {
    const businessName = data.businessName || 'InvoiceNG';
    
    return `
📄 *Invoice #${data.invoiceNumber}*

Hello ${data.customerName},

Please find your invoice details below:

💰 *Amount Due:* ${formatCurrency(data.total)}
📅 *Due Date:* ${data.dueDate}

Pay securely here:
${data.paymentLink}

Thank you for your business!

— ${businessName}
    `.trim();
  };

  const shareViaWhatsApp = useCallback(async (data: ShareData) => {
    setIsSharing(true);
    setError(null);

    try {
      const message = generateMessage(data);
      
      // Format phone number for WhatsApp
      let phone = data.customerPhone.replace(/[\s-]/g, '');
      if (phone.startsWith('0')) {
        phone = '234' + phone.substring(1);
      } else if (phone.startsWith('+')) {
        phone = phone.substring(1);
      }

      // Try Web Share API first (works best on mobile)
      if (navigator.share && navigator.canShare) {
        try {
          await navigator.share({
            title: `Invoice #${data.invoiceNumber}`,
            text: message,
          });
          return { success: true, method: 'native' };
        } catch (shareError) {
          // User cancelled or share not supported, fall through to URL method
          if ((shareError as Error).name === 'AbortError') {
            return { success: false, cancelled: true };
          }
        }
      }

      // Fallback: Open WhatsApp URL
      const encodedMessage = encodeURIComponent(message);
      const whatsappUrl = `https://wa.me/${phone}?text=${encodedMessage}`;
      
      // Open in new window/tab
      const newWindow = window.open(whatsappUrl, '_blank');
      
      if (!newWindow) {
        // Popup blocked, try direct navigation
        window.location.href = whatsappUrl;
      }

      return { success: true, method: 'url' };

    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to share';
      setError(errorMessage);
      return { success: false, error: errorMessage };
    } finally {
      setIsSharing(false);
    }
  }, []);

  // Generate shareable link without opening WhatsApp
  const getShareLink = useCallback((data: ShareData): string => {
    const message = generateMessage(data);
    let phone = data.customerPhone.replace(/[\s-]/g, '');
    if (phone.startsWith('0')) {
      phone = '234' + phone.substring(1);
    }
    return `https://wa.me/${phone}?text=${encodeURIComponent(message)}`;
  }, []);

  return {
    shareViaWhatsApp,
    getShareLink,
    isSharing,
    error,
  };
}

// Usage in component
function InvoiceActions({ invoice }: { invoice: Invoice }) {
  const { shareViaWhatsApp, isSharing } = useWhatsAppShare();

  const handleShare = async () => {
    const result = await shareViaWhatsApp({
      invoiceNumber: invoice.invoiceNumber,
      customerName: invoice.customer.name,
      customerPhone: invoice.customer.phone,
      total: invoice.total,
      dueDate: format(new Date(invoice.dueDate), 'MMMM d, yyyy'),
      paymentLink: invoice.paymentLink,
      businessName: invoice.user?.businessName,
    });

    if (result.success) {
      // Optionally mark invoice as sent
      await markInvoiceAsSent(invoice.id);
    }
  };

  return (
    <Button 
      onClick={handleShare} 
      disabled={isSharing}
      className="bg-green-500 hover:bg-green-600"
    >
      {isSharing ? (
        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
      ) : (
        <MessageCircle className="mr-2 h-4 w-4" />
      )}
      Send via WhatsApp
    </Button>
  );
}
```

### 5.4 React Native WhatsApp Share

```typescript
// hooks/useWhatsAppShare.ts (React Native)
import { Linking, Share, Platform } from 'react-native';

interface ShareData {
  invoiceNumber: string;
  customerName: string;
  customerPhone: string;
  total: number;
  dueDate: string;
  paymentLink: string;
  businessName?: string;
}

export function useWhatsAppShare() {
  const formatCurrency = (amount: number) => {
    return `₦${amount.toLocaleString()}`;
  };

  const generateMessage = (data: ShareData): string => {
    const businessName = data.businessName || 'InvoiceNG';
    
    return `
📄 *Invoice #${data.invoiceNumber}*

Hello ${data.customerName},

Please find your invoice details below:

💰 *Amount Due:* ${formatCurrency(data.total)}
📅 *Due Date:* ${data.dueDate}

Pay securely here:
${data.paymentLink}

Thank you for your business!

— ${businessName}
    `.trim();
  };

  const formatPhone = (phone: string): string => {
    phone = phone.replace(/[\s-]/g, '');
    if (phone.startsWith('0')) {
      return '234' + phone.substring(1);
    }
    if (phone.startsWith('+')) {
      return phone.substring(1);
    }
    return phone;
  };

  const shareViaWhatsApp = async (data: ShareData): Promise<{ success: boolean }> => {
    const message = generateMessage(data);
    const phone = formatPhone(data.customerPhone);

    try {
      // Check if WhatsApp is installed
      const whatsappUrl = `whatsapp://send?phone=${phone}&text=${encodeURIComponent(message)}`;
      const canOpen = await Linking.canOpenURL(whatsappUrl);

      if (canOpen) {
        await Linking.openURL(whatsappUrl);
        return { success: true };
      }

      // Fallback: Try web URL
      const webUrl = `https://wa.me/${phone}?text=${encodeURIComponent(message)}`;
      await Linking.openURL(webUrl);
      return { success: true };

    } catch (error) {
      console.error('WhatsApp share error:', error);
      
      // Final fallback: Use native share
      try {
        await Share.share({
          message: message,
          title: `Invoice #${data.invoiceNumber}`,
        });
        return { success: true };
      } catch {
        return { success: false };
      }
    }
  };

  return { shareViaWhatsApp };
}
```

---

## 6. Implementation Phases

### Phase 1: MVP Core (Weeks 1-4)

#### Week 1: Project Setup & Auth

```markdown
**Day 1-2: Backend Setup**
- [ ] Initialize Spring Boot project with dependencies
- [ ] Configure PostgreSQL connection
- [ ] Set up Flyway migrations
- [ ] Create initial database schema (V1)
- [ ] Configure CORS and security basics

**Day 3-4: Authentication**
- [ ] Implement OTP request endpoint
- [ ] Integrate Termii SMS API
- [ ] Implement OTP verification
- [ ] Create JWT token generation
- [ ] Add JWT authentication filter
- [ ] Create user registration flow

**Day 5: Frontend Setup**
- [ ] Initialize React/Vite project
- [ ] Set up Tailwind CSS
- [ ] Configure React Query
- [ ] Create API client with interceptors
- [ ] Build Login screen
- [ ] Build OTP verification screen

**Day 6-7: Testing & Polish**
- [ ] Test auth flow end-to-end
- [ ] Handle edge cases (expired OTP, rate limits)
- [ ] Add error handling and loading states
```

#### Week 2: Invoice CRUD

```markdown
**Day 1-2: Backend Invoice APIs**
- [ ] Create Invoice entity and repository
- [ ] Implement create invoice endpoint
- [ ] Implement list invoices with filtering
- [ ] Implement get invoice details
- [ ] Implement update/delete draft invoices
- [ ] Add invoice number generation logic

**Day 3-4: Customer Management**
- [ ] Create Customer entity and repository
- [ ] Implement customer CRUD endpoints
- [ ] Add customer search functionality
- [ ] Link customers to invoices
- [ ] Auto-create customer from invoice

**Day 5-6: Frontend Invoice UI**
- [ ] Build invoice list page
- [ ] Build create invoice form
- [ ] Implement customer selection/creation
- [ ] Build invoice items management (add/edit/remove)
- [ ] Build invoice detail view

**Day 7: Polish**
- [ ] Form validation
- [ ] Error handling
- [ ] Loading states
- [ ] Empty states
```

#### Week 3: Payments & WhatsApp

```markdown
**Day 1-2: Paystack Integration**
- [ ] Configure Paystack credentials
- [ ] Implement payment initialization
- [ ] Create payment link generation
- [ ] Set up webhook endpoint
- [ ] Implement signature verification
- [ ] Handle successful payment webhook
- [ ] Update invoice status on payment

**Day 3-4: WhatsApp Sharing**
- [ ] Implement Web Share API hook
- [ ] Create invoice message template
- [ ] Add share button to invoice
- [ ] Handle share success/failure
- [ ] Mark invoice as sent after sharing

**Day 5-6: Payment Flow Frontend**
- [ ] Create payment status page
- [ ] Handle payment callback
- [ ] Show payment confirmation
- [ ] Update invoice list after payment
- [ ] Add payment history to invoice detail

**Day 7: Integration Testing**
- [ ] Test full flow: create → share → pay
- [ ] Test webhook processing
- [ ] Verify data consistency
```

#### Week 4: Dashboard & Polish

```markdown
**Day 1-2: Dashboard Backend**
- [ ] Implement stats endpoint
- [ ] Calculate totals (revenue, paid, pending)
- [ ] Get recent activity
- [ ] Revenue chart data

**Day 3-4: Dashboard Frontend**
- [ ] Build stats cards
- [ ] Build recent invoices list
- [ ] Build simple revenue chart
- [ ] Add quick actions

**Day 5-6: User Profile**
- [ ] Build settings page
- [ ] Business profile form
- [ ] Bank details form
- [ ] Profile update API

**Day 7: Final Polish**
- [ ] Bug fixes
- [ ] Performance optimization
- [ ] Error boundary implementation
- [ ] PWA configuration (if applicable)
```

### Phase 2: Monetization Features (Weeks 5-8)

```markdown
**Week 5: PDF Generation**
- [ ] Set up Flying Saucer + Thymeleaf
- [ ] Design invoice PDF template
- [ ] Implement PDF generation service
- [ ] Set up Cloudflare R2 storage
- [ ] Add PDF download/view to invoice

**Week 6: Automatic Reminders**
- [ ] Create reminders table and entity
- [ ] Build reminder scheduler (Spring @Scheduled)
- [ ] Implement reminder sending (SMS/WhatsApp)
- [ ] Add reminder settings to invoice
- [ ] Build reminder management UI

**Week 7: Subscription & Limits**
- [ ] Create subscription tier tables
- [ ] Implement invoice count limits
- [ ] Build upgrade prompts
- [ ] Create pricing page
- [ ] Integrate Paystack subscriptions

**Week 8: Polish & Launch Prep**
- [ ] Performance testing
- [ ] Security audit
- [ ] Error monitoring (Sentry)
- [ ] Analytics setup (Mixpanel)
- [ ] Documentation
```

### Phase 3: Growth Features (Months 3-6)

```markdown
**Recurring Invoices**
- [ ] Invoice schedules table
- [ ] Schedule creation UI
- [ ] Approval workflow
- [ ] Schedule processing job

**AI Features**
- [ ] Claude API integration
- [ ] WhatsApp → Invoice extraction
- [ ] Smart descriptions
- [ ] Credit system

**Mobile App**
- [ ] React Native setup
- [ ] Auth screens
- [ ] Invoice screens
- [ ] Push notifications
- [ ] App store submission
```

---

## 7. Code Quality Standards

### 7.1 Java Backend Standards

```java
// Naming Conventions
- Classes: PascalCase (InvoiceService, PaystackConfig)
- Methods: camelCase (createInvoice, findByStatus)
- Constants: UPPER_SNAKE_CASE (MAX_INVOICE_ITEMS)
- Packages: lowercase (com.invoiceng.service)

// Package Structure
- controller: REST endpoints only, thin layer
- service: Business logic, transactions
- repository: Data access only
- entity: JPA entities
- dto: Request/response objects
- exception: Custom exceptions
- config: Configuration classes
- util: Utility/helper classes

// API Response Standards
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Map<String, String> errors;
    private String timestamp = Instant.now().toString();
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .build();
    }
}

// Exception Handling
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.<Void>builder()
                .success(false)
                .message("Validation failed")
                .errors(errors)
                .build());
    }
}

// Service Layer Best Practices
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final PaystackService paystackService;
    
    @Transactional
    public Invoice createInvoice(CreateInvoiceRequest request, UUID userId) {
        // Validate
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ValidationException("Invoice must have at least one item");
        }
        
        // Get or create customer
        Customer customer = customerService.getOrCreate(
            userId, 
            request.getCustomerId(), 
            request.getCustomerData()
        );
        
        // Create invoice
        Invoice invoice = Invoice.builder()
            .user(User.builder().id(userId).build())
            .customer(customer)
            .invoiceNumber(generateInvoiceNumber(userId))
            .items(request.getItems())
            .subtotal(calculateSubtotal(request.getItems()))
            .total(calculateTotal(request))
            .dueDate(request.getDueDate())
            .status(InvoiceStatus.DRAFT)
            .build();
        
        return invoiceRepository.save(invoice);
    }
}
```

### 7.2 TypeScript/React Standards

```typescript
// Type Definitions
// types/invoice.ts
export interface Invoice {
  id: string;
  invoiceNumber: string;
  customer: Customer;
  items: InvoiceItem[];
  subtotal: number;
  tax: number;
  total: number;
  status: InvoiceStatus;
  dueDate: string;
  paymentLink: string | null;
  pdfUrl: string | null;
  createdAt: string;
}

export type InvoiceStatus = 'draft' | 'sent' | 'viewed' | 'paid' | 'overdue' | 'cancelled';

export interface InvoiceItem {
  id: string;
  name: string;
  description?: string;
  quantity: number;
  price: number;
  total: number;
}

export interface CreateInvoiceRequest {
  customerId?: string;
  customerData?: CreateCustomerRequest;
  items: Omit<InvoiceItem, 'id' | 'total'>[];
  tax?: number;
  discount?: number;
  dueDate: string;
  notes?: string;
  terms?: string;
}

// API Client Pattern
// api/client.ts
import axios from 'axios';
import { useAuthStore } from '@/store/authStore';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;

// React Query Hooks Pattern
// hooks/useInvoices.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { invoiceApi } from '@/api/invoices';
import { useToast } from '@/hooks/useToast';

export function useInvoices(filters?: InvoiceFilters) {
  return useQuery({
    queryKey: ['invoices', filters],
    queryFn: () => invoiceApi.list(filters),
  });
}

export function useInvoice(id: string) {
  return useQuery({
    queryKey: ['invoices', id],
    queryFn: () => invoiceApi.get(id),
    enabled: !!id,
  });
}

export function useCreateInvoice() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: invoiceApi.create,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
      toast({
        title: 'Invoice created',
        description: `Invoice #${data.invoiceNumber} has been created.`,
      });
    },
    onError: (error: Error) => {
      toast({
        title: 'Error',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}

// Component Pattern
// components/invoice/InvoiceCard.tsx
import { memo } from 'react';
import { format } from 'date-fns';
import { Badge } from '@/components/ui/Badge';
import { Card } from '@/components/ui/Card';
import { formatCurrency } from '@/utils/formatCurrency';
import type { Invoice } from '@/types/invoice';

interface InvoiceCardProps {
  invoice: Invoice;
  onClick?: () => void;
}

const statusColors: Record<string, string> = {
  draft: 'bg-gray-100 text-gray-800',
  sent: 'bg-blue-100 text-blue-800',
  viewed: 'bg-purple-100 text-purple-800',
  paid: 'bg-green-100 text-green-800',
  overdue: 'bg-red-100 text-red-800',
  cancelled: 'bg-gray-100 text-gray-500',
};

export const InvoiceCard = memo(function InvoiceCard({ 
  invoice, 
  onClick 
}: InvoiceCardProps) {
  return (
    <Card 
      className="p-4 hover:shadow-md transition-shadow cursor-pointer"
      onClick={onClick}
    >
      <div className="flex justify-between items-start">
        <div>
          <p className="font-medium text-gray-900">
            #{invoice.invoiceNumber}
          </p>
          <p className="text-sm text-gray-500">
            {invoice.customer.name}
          </p>
        </div>
        <Badge className={statusColors[invoice.status]}>
          {invoice.status}
        </Badge>
      </div>
      
      <div className="mt-4 flex justify-between items-end">
        <div>
          <p className="text-2xl font-semibold">
            {formatCurrency(invoice.total)}
          </p>
          <p className="text-xs text-gray-500">
            Due: {format(new Date(invoice.dueDate), 'MMM d, yyyy')}
          </p>
        </div>
      </div>
    </Card>
  );
});
```

### 7.3 File Organization Rules

```
✅ DO:
- One component per file
- Co-locate tests with source files
- Group by feature, not by type
- Use barrel exports (index.ts) sparingly
- Keep files under 300 lines

❌ DON'T:
- Mix business logic in components
- Create deeply nested folders (max 3 levels)
- Put unrelated code in utils/
- Create God components
- Duplicate types across files
```

---

## 8. Testing Requirements

### 8.1 Backend Testing

```java
// Unit Test Example
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    
    @Mock
    private InvoiceRepository invoiceRepository;
    
    @Mock
    private CustomerService customerService;
    
    @InjectMocks
    private InvoiceService invoiceService;
    
    @Test
    void createInvoice_WithValidData_ShouldSucceed() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
            .customerData(new CreateCustomerRequest("John Doe", "08012345678"))
            .items(List.of(
                new InvoiceItemRequest("Service", 1, BigDecimal.valueOf(50000))
            ))
            .dueDate(LocalDate.now().plusDays(7))
            .build();
        
        Customer mockCustomer = Customer.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .build();
        
        when(customerService.getOrCreate(any(), any(), any()))
            .thenReturn(mockCustomer);
        when(invoiceRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));
        
        // Act
        Invoice result = invoiceService.createInvoice(request, userId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomer()).isEqualTo(mockCustomer);
        assertThat(result.getTotal()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(result.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        
        verify(invoiceRepository).save(any(Invoice.class));
    }
    
    @Test
    void createInvoice_WithNoItems_ShouldThrowException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
            .customerData(new CreateCustomerRequest("John Doe", "08012345678"))
            .items(List.of())
            .dueDate(LocalDate.now().plusDays(7))
            .build();
        
        // Act & Assert
        assertThatThrownBy(() -> invoiceService.createInvoice(request, userId))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("at least one item");
    }
}

// Integration Test Example
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class InvoiceControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    private String authToken;
    
    @BeforeEach
    void setUp() {
        // Create test user and get token
        User user = userRepository.save(User.builder()
            .phone("08012345678")
            .businessName("Test Business")
            .build());
        authToken = jwtProvider.generateToken(user.getId());
    }
    
    @Test
    void createInvoice_ShouldReturn201() throws Exception {
        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
            .customerData(new CreateCustomerRequest("John Doe", "08087654321"))
            .items(List.of(
                new InvoiceItemRequest("Ankara Dress", 1, BigDecimal.valueOf(50000))
            ))
            .dueDate(LocalDate.now().plusDays(7))
            .build();
        
        mockMvc.perform(post("/api/v1/invoices")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.invoiceNumber").isNotEmpty())
            .andExpect(jsonPath("$.data.total").value(50000))
            .andExpect(jsonPath("$.data.status").value("draft"));
    }
}
```

### 8.2 Frontend Testing

```typescript
// Component Test Example
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { CreateInvoicePage } from './CreateInvoicePage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
  },
});

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClient}>
    {children}
  </QueryClientProvider>
);

describe('CreateInvoicePage', () => {
  it('should create invoice with valid data', async () => {
    const user = userEvent.setup();
    render(<CreateInvoicePage />, { wrapper });
    
    // Fill customer details
    await user.type(screen.getByLabelText(/customer name/i), 'John Doe');
    await user.type(screen.getByLabelText(/phone/i), '08012345678');
    
    // Add item
    await user.type(screen.getByLabelText(/item name/i), 'Ankara Dress');
    await user.type(screen.getByLabelText(/quantity/i), '1');
    await user.type(screen.getByLabelText(/price/i), '50000');
    
    // Set due date
    await user.type(screen.getByLabelText(/due date/i), '2025-01-15');
    
    // Submit
    await user.click(screen.getByRole('button', { name: /create invoice/i }));
    
    await waitFor(() => {
      expect(screen.getByText(/invoice created/i)).toBeInTheDocument();
    });
  });
  
  it('should show validation errors for empty form', async () => {
    const user = userEvent.setup();
    render(<CreateInvoicePage />, { wrapper });
    
    await user.click(screen.getByRole('button', { name: /create invoice/i }));
    
    expect(screen.getByText(/customer name is required/i)).toBeInTheDocument();
    expect(screen.getByText(/at least one item required/i)).toBeInTheDocument();
  });
});

// Hook Test Example
import { renderHook, act, waitFor } from '@testing-library/react';
import { useWhatsAppShare } from './useWhatsAppShare';

describe('useWhatsAppShare', () => {
  const mockShare = vi.fn();
  
  beforeEach(() => {
    vi.stubGlobal('navigator', {
      share: mockShare,
      canShare: () => true,
    });
  });
  
  it('should share via Web Share API', async () => {
    mockShare.mockResolvedValueOnce(undefined);
    
    const { result } = renderHook(() => useWhatsAppShare());
    
    await act(async () => {
      const shareResult = await result.current.shareViaWhatsApp({
        invoiceNumber: 'INV-001',
        customerName: 'John',
        customerPhone: '08012345678',
        total: 50000,
        dueDate: 'January 15, 2025',
        paymentLink: 'https://paystack.com/pay/xyz',
      });
      
      expect(shareResult.success).toBe(true);
    });
    
    expect(mockShare).toHaveBeenCalled();
  });
});
```

### 8.3 E2E Testing

```typescript
// Playwright E2E Test
import { test, expect } from '@playwright/test';

test.describe('Invoice Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Login
    await page.goto('/login');
    await page.fill('[name="phone"]', '08012345678');
    await page.click('button:has-text("Send OTP")');
    
    // Mock OTP verification
    await page.fill('[name="otp"]', '123456');
    await page.click('button:has-text("Verify")');
    
    await expect(page).toHaveURL('/dashboard');
  });
  
  test('should create and share invoice', async ({ page }) => {
    // Navigate to create invoice
    await page.click('a:has-text("Create Invoice")');
    await expect(page).toHaveURL('/invoices/create');
    
    // Fill form
    await page.fill('[name="customerName"]', 'Mrs. Adebayo');
    await page.fill('[name="customerPhone"]', '08087654321');
    
    // Add item
    await page.fill('[name="items.0.name"]', 'Wedding Aso-Oke');
    await page.fill('[name="items.0.quantity"]', '1');
    await page.fill('[name="items.0.price"]', '150000');
    
    // Set due date
    await page.fill('[name="dueDate"]', '2025-02-01');
    
    // Create invoice
    await page.click('button:has-text("Create Invoice")');
    
    // Wait for success
    await expect(page.locator('.toast')).toContainText('Invoice created');
    
    // Should be on invoice detail page
    await expect(page).toHaveURL(/\/invoices\/[\w-]+/);
    
    // Check invoice details
    await expect(page.locator('[data-testid="invoice-total"]')).toContainText('₦150,000');
    await expect(page.locator('[data-testid="customer-name"]')).toContainText('Mrs. Adebayo');
    
    // Share via WhatsApp (will open WhatsApp URL)
    const [popup] = await Promise.all([
      page.waitForEvent('popup'),
      page.click('button:has-text("Send via WhatsApp")'),
    ]);
    
    expect(popup.url()).toContain('wa.me');
  });
});
```

---

## 9. Deployment Guide

### 9.1 Environment Variables

```bash
# .env.production

# ═══════════════════════════════════════════════════════════════
# APPLICATION
# ═══════════════════════════════════════════════════════════════
NODE_ENV=production
VITE_APP_NAME=InvoiceNG
VITE_API_URL=https://api.invoiceng.com

# ═══════════════════════════════════════════════════════════════
# DATABASE (Railway)
# ═══════════════════════════════════════════════════════════════
DATABASE_URL=postgresql://postgres:xxxxx@containers-us-west-xxx.railway.app:5432/railway
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=xxxxx

# ═══════════════════════════════════════════════════════════════
# JWT
# ═══════════════════════════════════════════════════════════════
JWT_SECRET=your-256-bit-secret-generate-with-openssl
JWT_EXPIRATION=86400000

# ═══════════════════════════════════════════════════════════════
# PAYSTACK
# ═══════════════════════════════════════════════════════════════
PAYSTACK_SECRET_KEY=sk_live_xxxxx
PAYSTACK_PUBLIC_KEY=pk_live_xxxxx
PAYSTACK_CALLBACK_URL=https://invoiceng.com/payment/callback

# ═══════════════════════════════════════════════════════════════
# TERMII (SMS)
# ═══════════════════════════════════════════════════════════════
TERMII_API_KEY=your-termii-api-key
TERMII_SENDER_ID=InvoiceNG

# ═══════════════════════════════════════════════════════════════
# CLOUDFLARE R2 (Storage)
# ═══════════════════════════════════════════════════════════════
R2_ACCOUNT_ID=xxxxx
R2_ACCESS_KEY_ID=xxxxx
R2_SECRET_ACCESS_KEY=xxxxx
R2_BUCKET_NAME=invoiceng-files
R2_PUBLIC_URL=https://files.invoiceng.com

# ═══════════════════════════════════════════════════════════════
# EMAIL (Resend)
# ═══════════════════════════════════════════════════════════════
RESEND_API_KEY=re_xxxxx
EMAIL_FROM=InvoiceNG <noreply@invoiceng.com>

# ═══════════════════════════════════════════════════════════════
# MONITORING
# ═══════════════════════════════════════════════════════════════
SENTRY_DSN=https://xxxxx@sentry.io/xxxxx
MIXPANEL_TOKEN=xxxxx
```

### 9.2 Docker Configuration

```dockerfile
# Dockerfile (Backend)
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --from=builder /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

```yaml
# docker-compose.yml (Local Development)
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: invoiceng
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  api:
    build: ./invoiceng-api
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DATABASE_URL=jdbc:postgresql://postgres:5432/invoiceng
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      - postgres

  web:
    build: ./invoiceng-web
    ports:
      - "3000:3000"
    environment:
      - VITE_API_URL=http://localhost:8080
    depends_on:
      - api

volumes:
  postgres_data:
```

### 9.3 Railway Deployment

```toml
# railway.toml
[build]
builder = "DOCKERFILE"
dockerfilePath = "Dockerfile"

[deploy]
startCommand = "java -jar app.jar"
healthcheckPath = "/actuator/health"
healthcheckTimeout = 30
restartPolicyType = "ON_FAILURE"
restartPolicyMaxRetries = 3

[[services]]
name = "api"
```

```yaml
# railway.yaml (alternative)
services:
  - name: invoiceng-api
    build:
      dockerfile: Dockerfile
    deploy:
      healthcheck:
        path: /actuator/health
        interval: 30s
        timeout: 10s
    env:
      SPRING_PROFILES_ACTIVE: production
```

### 9.4 Vercel Configuration

```json
// vercel.json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "framework": "vite",
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ],
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        { "key": "X-Content-Type-Options", "value": "nosniff" },
        { "key": "X-Frame-Options", "value": "DENY" },
        { "key": "X-XSS-Protection", "value": "1; mode=block" }
      ]
    }
  ]
}
```

### 9.5 CI/CD Pipeline

```yaml
# .github/workflows/deploy.yml
name: Deploy

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test-backend:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_DB: test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests
        working-directory: ./invoiceng-api
        run: ./mvnw test
        env:
          DATABASE_URL: jdbc:postgresql://localhost:5432/test
          SPRING_DATASOURCE_USERNAME: test
          SPRING_DATASOURCE_PASSWORD: test

  test-frontend:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: ./invoiceng-web/package-lock.json
      
      - name: Install dependencies
        working-directory: ./invoiceng-web
        run: npm ci
      
      - name: Run tests
        working-directory: ./invoiceng-web
        run: npm test
      
      - name: Run linter
        working-directory: ./invoiceng-web
        run: npm run lint

  deploy-backend:
    needs: [test-backend]
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Deploy to Railway
        uses: bervProject/railway-deploy@main
        with:
          railway_token: ${{ secrets.RAILWAY_TOKEN }}
          service: invoiceng-api

  deploy-frontend:
    needs: [test-frontend]
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          working-directory: ./invoiceng-web
```

---

## 10. Claude Code Task Prompts

### 10.1 Project Initialization

```markdown
## Task: Initialize InvoiceNG Backend Project

Create a new Spring Boot 3.2 project with the following:

1. **Maven Configuration** (pom.xml):
   - Java 21
   - Spring Boot 3.2.x
   - Spring Web, Security, Data JPA
   - PostgreSQL driver
   - Flyway for migrations
   - Lombok
   - JWT (jjwt)
   - Validation
   - WebFlux (for WebClient)
   - SpringDoc OpenAPI
   - DevTools (dev only)

2. **Application Properties**:
   - Configure for PostgreSQL
   - Set up profiles (dev, prod)
   - Configure Flyway
   - Add Paystack, Termii placeholders

3. **Base Package Structure**:
   - com.invoiceng
   - Create all subpackages (controller, service, repository, entity, dto, config, exception, security, util)

4. **Base Configuration Classes**:
   - SecurityConfig (CORS, CSRF, JWT filter chain)
   - WebConfig
   - JwtConfig

5. **Global Exception Handler**:
   - ResourceNotFoundException
   - ValidationException
   - UnauthorizedException
   - Generic error handling

6. **API Response Wrapper**:
   - ApiResponse<T> with success, message, data, errors, timestamp

Run: Create all files in the correct package structure, ready to implement features.
```

### 10.2 Authentication Implementation

```markdown
## Task: Implement Phone OTP Authentication

Implement complete authentication flow:

### 1. Database (Flyway migration):
- users table (id, phone, email, business_name, etc.)
- otp_requests table (id, phone, otp_hash, pin_id, attempts, verified, expires_at)

### 2. Entities:
- User entity with all fields from schema
- OtpRequest entity

### 3. Repositories:
- UserRepository (findByPhone)
- OtpRequestRepository (findByPhoneAndNotExpired)

### 4. DTOs:
- Request: RequestOtpDto (phone), VerifyOtpDto (phone, otp)
- Response: AuthResponse (token, user, isNewUser)

### 5. Services:
- AuthService:
  - requestOtp(phone) - validate phone, rate limit, send via Termii, save hash
  - verifyOtp(phone, otp) - verify via Termii, create/get user, return JWT
  - refreshToken(refreshToken)
- SmsService:
  - sendOtp(phone) - integrate Termii API
  - verifyOtp(pinId, otp) - verify via Termii

### 6. Security:
- JwtTokenProvider (generate, validate, getUserId)
- JwtAuthenticationFilter
- UserPrincipal

### 7. Controller:
- POST /api/v1/auth/request-otp
- POST /api/v1/auth/verify-otp
- POST /api/v1/auth/refresh

### Requirements:
- Phone format: accept 08012345678 or +2348012345678, store as 2348012345678
- OTP expires in 10 minutes
- Max 3 attempts per OTP
- Rate limit: 3 requests per phone per 15 minutes
- JWT expiry: 24 hours

Include unit tests for AuthService.
```

### 10.3 Invoice CRUD Implementation

```markdown
## Task: Implement Invoice CRUD Operations

Implement complete invoice management:

### 1. Database (Flyway migration V2):
- invoices table (all fields from schema)
- Add GIN index on items JSONB
- Add indexes on user_id, customer_id, status, due_date

### 2. Entities:
- Invoice entity with JSONB items handling
- InvoiceStatus enum (DRAFT, SENT, VIEWED, PAID, OVERDUE, CANCELLED)

### 3. DTOs:
- Request:
  - CreateInvoiceRequest (customerId OR customerData, items[], tax, discount, dueDate, notes, terms)
  - UpdateInvoiceRequest (all optional)
  - InvoiceItemDto (name, description, quantity, price)
- Response:
  - InvoiceResponse (full invoice with customer)
  - InvoiceListResponse (paginated with summary)
  - InvoiceSummary (totalAmount, paidAmount, pendingAmount, overdueAmount)

### 4. Repository:
- InvoiceRepository with custom queries:
  - findByUserIdWithFilters(userId, status, customerId, fromDate, toDate, pageable)
  - getSummaryByUserId(userId)
  - findOverdueInvoices()

### 5. Service:
- InvoiceService:
  - createInvoice(request, userId) - validate items, get/create customer, generate invoice number, save
  - getInvoice(id, userId) - with authorization check
  - listInvoices(userId, filters, pageable) - with summary
  - updateInvoice(id, request, userId) - only draft invoices
  - deleteInvoice(id, userId) - only draft invoices
  - sendInvoice(id, userId) - generate payment link, update status
  - cancelInvoice(id, userId)
  - duplicateInvoice(id, userId) - create copy as draft

### 6. Invoice Number Generation:
- Format: INV-{YYYYMM}-{5-digit-sequence}
- Example: INV-202501-00001
- Sequence resets monthly

### 7. Controller:
- GET /api/v1/invoices (with query params)
- GET /api/v1/invoices/{id}
- POST /api/v1/invoices
- PUT /api/v1/invoices/{id}
- DELETE /api/v1/invoices/{id}
- POST /api/v1/invoices/{id}/send
- POST /api/v1/invoices/{id}/cancel
- POST /api/v1/invoices/{id}/duplicate

### Validation:
- Items array must have at least 1 item
- Each item must have name, quantity > 0, price > 0
- Due date must be today or future
- Total must match calculated total

Include unit tests and one integration test.
```

### 10.4 Payment Integration

```markdown
## Task: Implement Paystack Payment Integration

Implement payment processing with Paystack:

### 1. Database (Flyway migration V3):
- payments table (id, invoice_id, amount, reference, paystack_reference, channel, status, paid_at, metadata)
- Add index on reference

### 2. Configuration:
- PaystackConfig (secretKey, publicKey, baseUrl, callbackUrl)

### 3. DTOs:
- PaystackInitRequest (reference, amount, email, callback_url, metadata, channels)
- PaystackInitResponse (authorization_url, access_code, reference)
- PaystackVerifyResponse (status, reference, amount, channel, paid_at)
- PaystackWebhookEvent (event, data)

### 4. Service:
- PaystackService:
  - initializeTransaction(reference, amount, email, customerName, metadata)
  - verifyTransaction(reference)
  - verifyWebhookSignature(payload, signature)

- PaymentService:
  - initializePayment(invoiceId, userId) - create payment record, call Paystack
  - verifyPayment(reference) - check with Paystack
  - handleSuccessfulPayment(webhookData) - update payment, update invoice, update customer stats
  - handleFailedPayment(webhookData)

### 5. Controller:
- POST /api/v1/payments/initialize
- GET /api/v1/payments/verify/{reference}

### 6. Webhook Controller:
- POST /api/v1/webhooks/paystack
- Verify x-paystack-signature header
- Handle charge.success event
- Handle charge.failed event
- Always return 200 OK

### 7. Invoice Updates on Payment:
- Update invoice status to PAID
- Set paid_at timestamp
- Update customer total_paid and total_outstanding

### Payment Reference Format:
- INV-{invoiceId}-{timestamp}
- Must be unique

### Error Handling:
- Handle Paystack API errors gracefully
- Log all webhook events
- Retry logic for verification

Include tests with mocked Paystack responses.
```

### 10.5 React Frontend Setup

```markdown
## Task: Initialize InvoiceNG React Frontend

Create React + Vite + TypeScript frontend:

### 1. Project Setup:
- Vite with React + TypeScript template
- Tailwind CSS configuration
- Path aliases (@/ for src/)
- ESLint + Prettier configuration

### 2. Dependencies:
- @tanstack/react-query
- axios
- zustand
- react-router-dom
- react-hook-form + @hookform/resolvers + zod
- date-fns
- lucide-react (icons)
- clsx + tailwind-merge

### 3. Folder Structure:
```
src/
├── api/
│   ├── client.ts (axios instance with interceptors)
│   ├── auth.ts
│   ├── invoices.ts
│   └── customers.ts
├── components/
│   ├── ui/ (Button, Input, Card, Modal, Toast, Loading)
│   ├── layout/ (Header, Sidebar, MobileNav, Layout)
│   └── (feature components later)
├── pages/
│   ├── auth/
│   │   ├── Login.tsx
│   │   └── VerifyOtp.tsx
│   ├── Dashboard.tsx
│   └── NotFound.tsx
├── hooks/
│   ├── useAuth.ts
│   └── useToast.ts
├── store/
│   └── authStore.ts (Zustand)
├── types/
│   └── index.ts
├── utils/
│   ├── formatCurrency.ts
│   ├── formatDate.ts
│   └── cn.ts (classname helper)
├── App.tsx
└── main.tsx
```

### 4. API Client Setup:
- Base URL from env
- JWT token injection from store
- 401 handling (logout)
- Error transformation

### 5. Auth Store (Zustand):
- token, user state
- login, logout actions
- persist to localStorage

### 6. Router Setup:
- Public routes: /login, /verify-otp
- Protected routes: /dashboard, /invoices, etc.
- Auth guard component

### 7. Base UI Components:
Create minimal, Tailwind-styled components:
- Button (variants: primary, secondary, outline, ghost)
- Input (with label, error state)
- Card
- Loading spinner

### 8. Layout:
- Responsive layout with sidebar (desktop) / bottom nav (mobile)
- Header with user menu

### 9. Login Flow:
- Login page with phone input
- OTP verification page
- Redirect to dashboard on success

Include all TypeScript types and proper error handling.
```

### 10.6 Invoice UI Implementation

```markdown
## Task: Implement Invoice List and Create Pages

Build invoice management UI:

### 1. Types (src/types/invoice.ts):
- Invoice, InvoiceItem, InvoiceStatus
- CreateInvoiceRequest, UpdateInvoiceRequest
- Customer, CreateCustomerRequest

### 2. API Functions (src/api/invoices.ts):
- listInvoices(filters): Promise<PaginatedResponse<Invoice>>
- getInvoice(id): Promise<Invoice>
- createInvoice(data): Promise<Invoice>
- updateInvoice(id, data): Promise<Invoice>
- deleteInvoice(id): Promise<void>
- sendInvoice(id): Promise<Invoice>

### 3. React Query Hooks (src/hooks/useInvoices.ts):
- useInvoices(filters) - list with caching
- useInvoice(id) - single invoice
- useCreateInvoice() - mutation with cache invalidation
- useUpdateInvoice() - mutation
- useDeleteInvoice() - mutation
- useSendInvoice() - mutation

### 4. Invoice List Page (src/pages/Invoices.tsx):
- Filter tabs: All, Draft, Sent, Paid, Overdue
- Search by invoice number
- Summary stats cards (Total, Paid, Pending, Overdue amounts)
- Invoice list with InvoiceCard component
- Empty state
- Loading skeleton
- Pagination

### 5. InvoiceCard Component:
- Invoice number, customer name
- Amount (large, formatted)
- Status badge (color-coded)
- Due date
- Click to view details

### 6. Create Invoice Page (src/pages/CreateInvoice.tsx):
- React Hook Form + Zod validation
- Customer section:
  - Select existing customer OR
  - Create new (name, phone, email optional)
- Items section:
  - Dynamic item rows (add/remove)
  - Name, quantity, price per row
  - Auto-calculate line total
  - Running subtotal
- Due date picker
- Notes field (optional)
- Subtotal, Tax (optional toggle), Total display
- "Save as Draft" and "Create & Send" buttons

### 7. Invoice Item Row Component:
- Inline editing
- Remove button
- Auto-calculation

### 8. Customer Select Component:
- Searchable dropdown
- Recent customers
- "Add new customer" option
- Inline create form

### 9. Form Validation (Zod schema):
- Customer: name required, phone required (Nigerian format)
- Items: at least 1, each with name, quantity > 0, price > 0
- Due date: required, must be today or future

### 10. UX Requirements:
- Optimistic updates where sensible
- Toast notifications on success/error
- Confirm dialog on delete
- Auto-save draft (debounced)
- Mobile-responsive

Include loading states, error handling, and empty states.
```

### 10.7 WhatsApp Share Integration

```markdown
## Task: Implement WhatsApp Invoice Sharing

Add WhatsApp sharing functionality:

### 1. Custom Hook (src/hooks/useWhatsAppShare.ts):
```typescript
interface ShareData {
  invoiceNumber: string;
  customerName: string;
  customerPhone: string;
  total: number;
  dueDate: string;
  paymentLink: string;
  businessName?: string;
}

function useWhatsAppShare() {
  // Return: { shareViaWhatsApp, getShareLink, isSharing, error }
}
```

### 2. Message Template:
```
📄 *Invoice #INV-202501-00001*

Hello {CustomerName},

Please find your invoice details below:

💰 *Amount Due:* ₦150,000
📅 *Due Date:* January 15, 2025

Pay securely here:
https://paystack.com/pay/xyz123

Thank you for your business!

— {BusinessName}
```

### 3. Implementation:
- Try Web Share API first (navigator.share)
- Fallback to WhatsApp URL scheme (wa.me/phone?text=)
- Handle share cancellation
- Format phone to international format (234...)

### 4. Share Button Component:
- Green WhatsApp-style button
- Loading state while sharing
- Success/error toast

### 5. Invoice Detail Page Updates:
- Add "Send via WhatsApp" button
- Add "Copy Payment Link" button
- Show share confirmation
- Update invoice status to "sent" after successful share

### 6. Invoice Actions Component:
- Dropdown or button group with actions:
  - Send via WhatsApp
  - Copy link
  - Download PDF (placeholder)
  - Edit (if draft)
  - Cancel

### 7. Post-Share Flow:
- Call API to mark invoice as sent
- Update local cache
- Show success message with customer name

### 8. Edge Cases:
- WhatsApp not installed (show helpful message)
- Share cancelled by user
- Invalid phone number
- API error marking as sent

Include responsive design for mobile (where WhatsApp is most used).
```

### 10.8 Dashboard Implementation

```markdown
## Task: Implement Dashboard with Stats

Build the main dashboard:

### 1. API Endpoints:
- GET /api/v1/dashboard/stats (overview, comparison, recentActivity)
- GET /api/v1/dashboard/revenue-chart (data points for chart)
- GET /api/v1/dashboard/top-customers (top 5)

### 2. Backend Implementation:
- DashboardController
- DashboardService with queries:
  - getOverviewStats(userId, period)
  - getRevenueChartData(userId, period)
  - getTopCustomers(userId, limit)

### 3. Frontend Types:
```typescript
interface DashboardStats {
  overview: {
    totalRevenue: number;
    totalInvoices: number;
    paidInvoices: number;
    pendingInvoices: number;
    overdueInvoices: number;
    collectionRate: number;
  };
  comparison: {
    revenueChange: number;
    invoiceChange: number;
  };
  recentActivity: Activity[];
}
```

### 4. Dashboard Page Layout:
```
┌─────────────────────────────────────────────────────────┐
│  Welcome back, {BusinessName}!                 [Period ▼]│
├─────────────────────────────────────────────────────────┤
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │ Revenue  │ │ Invoices │ │ Pending  │ │ Overdue  │   │
│  │ ₦2.5M    │ │    45    │ │    12    │ │    3     │   │
│  │ +15% ▲   │ │  +5 ▲    │ │ ₦450K    │ │ ₦120K    │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Revenue Chart                           Quick Actions  │
│  ┌─────────────────────────────────┐    ┌────────────┐ │
│  │         📈 Line Chart           │    │ + Invoice  │ │
│  │                                 │    │ + Customer │ │
│  │                                 │    │ View All   │ │
│  └─────────────────────────────────┘    └────────────┘ │
│                                                         │
├─────────────────────────────────────────────────────────┤
│  Recent Activity              │  Top Customers          │
│  ┌─────────────────────────┐  │  ┌──────────────────┐  │
│  │ • Payment received...   │  │  │ 1. Mrs Bello ₦1M │  │
│  │ • Invoice created...    │  │  │ 2. Ade & Co ₦800K│  │
│  │ • Invoice overdue...    │  │  │ 3. Chief... ₦500K│  │
│  └─────────────────────────┘  │  └──────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 5. Components:
- StatsCard (icon, label, value, change percentage, trend arrow)
- RevenueChart (using recharts or chart.js)
- ActivityFeed (list with icons, timestamps)
- TopCustomersList
- QuickActions (button group)
- PeriodSelector (dropdown: This Week, This Month, This Quarter)

### 6. React Query Setup:
- useDashboardStats(period)
- useRevenueChart(period)
- useTopCustomers()
- Stale time: 5 minutes
- Refetch on window focus

### 7. Loading States:
- Skeleton loaders for each card
- Chart placeholder
- Activity list skeleton

### 8. Empty States:
- No invoices yet: Show onboarding prompt
- No revenue: Encourage creating first invoice

### 9. Mobile Responsive:
- Stack cards vertically
- Chart below stats
- Activity and customers in tabs

Include proper number formatting (₦2.5M, not ₦2500000).
```

---

## Appendix A: Quick Reference

### Common Commands

```bash
# Backend
./mvnw spring-boot:run                    # Run dev server
./mvnw test                               # Run tests
./mvnw flyway:migrate                     # Run migrations
./mvnw package -DskipTests                # Build JAR

# Frontend
npm run dev                               # Run dev server
npm run build                             # Production build
npm test                                  # Run tests
npm run lint                              # Lint code

# Docker
docker-compose up -d                      # Start all services
docker-compose logs -f api                # View API logs
docker-compose down -v                    # Stop and remove volumes
```

### Important Links

- Paystack Docs: https://paystack.com/docs/api
- Termii Docs: https://developer.termii.com
- WhatsApp URL Scheme: https://faq.whatsapp.com/5913398998672934
- Railway Docs: https://docs.railway.app
- Vercel Docs: https://vercel.com/docs

### Nigerian Phone Number Formats

```
Input           → Stored/API
08012345678     → 2348012345678
+2348012345678  → 2348012345678
2348012345678   → 2348012345678
```

### Currency Formatting

```typescript
// Always display Naira as:
formatCurrency(150000)    // "₦150,000"
formatCurrency(2500000)   // "₦2,500,000" or "₦2.5M" for compact

// Store in database as:
// DECIMAL(12, 2) - 150000.00
// No kobo for simplicity (round to nearest Naira)
```

---

## Appendix B: Troubleshooting

### Common Issues

**1. Paystack webhook not received**
- Check webhook URL is publicly accessible
- Verify SSL certificate is valid
- Check Paystack dashboard for failed deliveries
- Ensure returning 200 OK quickly

**2. SMS not delivered**
- Verify Termii API key
- Check sender ID is approved
- Verify phone number format (234...)
- Check Termii dashboard for delivery status

**3. WhatsApp share not working**
- Web Share API requires HTTPS
- Some browsers don't support it (fallback to URL)
- Phone number must be valid
- Message can't be too long

**4. JWT token issues**
- Check token expiry
- Verify secret key matches
- Ensure clock sync between servers
- Check Authorization header format

---

*This document serves as the complete implementation guide for InvoiceNG. Follow the phases sequentially and reference specific sections as needed during development.*
