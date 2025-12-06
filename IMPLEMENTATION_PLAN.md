# InvoiceNG Implementation Plan
## Supabase + Next.js PWA Stack

> **Stack**: Next.js 14 (App Router) + Supabase + Tailwind CSS + PWA
> **Timeline**: 3-4 weeks for MVP
> **Cost**: Minimal (free tiers + domain)

---

## Project Structure

```
invoiceng/
├── src/
│   ├── app/                      # Next.js App Router
│   │   ├── (auth)/              # Auth group (login, verify)
│   │   │   ├── login/
│   │   │   │   └── page.tsx
│   │   │   └── verify/
│   │   │       └── page.tsx
│   │   ├── (dashboard)/         # Protected routes group
│   │   │   ├── layout.tsx       # Dashboard layout with sidebar
│   │   │   ├── page.tsx         # Dashboard home
│   │   │   ├── invoices/
│   │   │   │   ├── page.tsx     # Invoice list
│   │   │   │   ├── new/
│   │   │   │   │   └── page.tsx # Create invoice
│   │   │   │   └── [id]/
│   │   │   │       └── page.tsx # Invoice detail
│   │   │   ├── customers/
│   │   │   │   └── page.tsx
│   │   │   └── settings/
│   │   │       └── page.tsx
│   │   ├── pay/                 # Public payment page
│   │   │   └── [ref]/
│   │   │       └── page.tsx
│   │   ├── api/                 # API routes
│   │   │   ├── webhooks/
│   │   │   │   └── paystack/
│   │   │   │       └── route.ts
│   │   │   └── sms/
│   │   │       └── route.ts
│   │   ├── layout.tsx
│   │   ├── page.tsx             # Landing/redirect
│   │   └── globals.css
│   │
│   ├── components/
│   │   ├── ui/                  # Reusable UI components
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── card.tsx
│   │   │   ├── modal.tsx
│   │   │   ├── toast.tsx
│   │   │   ├── loading.tsx
│   │   │   ├── badge.tsx
│   │   │   └── select.tsx
│   │   ├── layout/
│   │   │   ├── header.tsx
│   │   │   ├── sidebar.tsx
│   │   │   ├── mobile-nav.tsx
│   │   │   └── dashboard-layout.tsx
│   │   ├── invoice/
│   │   │   ├── invoice-form.tsx
│   │   │   ├── invoice-list.tsx
│   │   │   ├── invoice-card.tsx
│   │   │   ├── invoice-detail.tsx
│   │   │   ├── invoice-item-row.tsx
│   │   │   └── invoice-actions.tsx
│   │   ├── customer/
│   │   │   ├── customer-select.tsx
│   │   │   ├── customer-form.tsx
│   │   │   └── customer-list.tsx
│   │   └── dashboard/
│   │       ├── stats-card.tsx
│   │       ├── recent-invoices.tsx
│   │       └── activity-feed.tsx
│   │
│   ├── lib/
│   │   ├── supabase/
│   │   │   ├── client.ts        # Browser client
│   │   │   ├── server.ts        # Server client
│   │   │   ├── middleware.ts    # Auth middleware
│   │   │   └── database.types.ts # Generated types
│   │   ├── paystack.ts          # Paystack API wrapper
│   │   ├── termii.ts            # Termii SMS wrapper
│   │   └── pdf.ts               # Client-side PDF generation
│   │
│   ├── hooks/
│   │   ├── use-auth.ts
│   │   ├── use-invoices.ts
│   │   ├── use-customers.ts
│   │   ├── use-whatsapp-share.ts
│   │   ├── use-toast.ts
│   │   └── use-dashboard.ts
│   │
│   ├── stores/
│   │   ├── auth-store.ts
│   │   └── ui-store.ts
│   │
│   ├── types/
│   │   ├── invoice.ts
│   │   ├── customer.ts
│   │   ├── user.ts
│   │   └── database.ts
│   │
│   └── utils/
│       ├── format-currency.ts
│       ├── format-date.ts
│       ├── format-phone.ts
│       ├── validators.ts
│       └── cn.ts                # Tailwind class helper
│
├── supabase/
│   ├── migrations/
│   │   ├── 20250101000000_initial_schema.sql
│   │   ├── 20250101000001_add_rls_policies.sql
│   │   └── 20250101000002_add_functions.sql
│   ├── functions/               # Edge functions
│   │   ├── send-otp/
│   │   │   └── index.ts
│   │   ├── verify-otp/
│   │   │   └── index.ts
│   │   ├── process-payment-webhook/
│   │   │   └── index.ts
│   │   └── send-reminder/
│   │       └── index.ts
│   └── config.toml
│
├── public/
│   ├── manifest.json
│   ├── sw.js                    # Service worker
│   └── icons/
│       ├── icon-192.png
│       └── icon-512.png
│
├── .env.local                   # Local environment
├── .env.example                 # Example env file
├── next.config.js
├── tailwind.config.js
├── tsconfig.json
├── package.json
└── README.md
```

---

## Phase 1: Foundation (Week 1)

### Day 1-2: Project Setup

#### Tasks:
1. **Initialize Next.js Project**
   - Next.js 14 with App Router
   - TypeScript configuration
   - Tailwind CSS setup
   - ESLint + Prettier

2. **Configure Supabase**
   - Create Supabase project
   - Install `@supabase/supabase-js` and `@supabase/ssr`
   - Create client utilities (browser + server)
   - Set up middleware for auth

3. **Create Database Schema**
   - Users table
   - OTP requests table
   - Customers table
   - Invoices table
   - Payments table
   - Row Level Security (RLS) policies

4. **Base UI Components**
   - Button (primary, secondary, outline, ghost, destructive)
   - Input (with label, error, disabled states)
   - Card
   - Loading spinner
   - Toast notifications

#### Dependencies:
```json
{
  "dependencies": {
    "next": "^14.0.0",
    "@supabase/supabase-js": "^2.39.0",
    "@supabase/ssr": "^0.1.0",
    "@tanstack/react-query": "^5.17.0",
    "zustand": "^4.4.7",
    "react-hook-form": "^7.49.0",
    "@hookform/resolvers": "^3.3.4",
    "zod": "^3.22.4",
    "date-fns": "^3.0.0",
    "lucide-react": "^0.303.0",
    "clsx": "^2.1.0",
    "tailwind-merge": "^2.2.0",
    "next-pwa": "^5.6.0"
  }
}
```

### Day 3-4: Authentication System

#### Database Schema (Supabase):
```sql
-- Users table (extends Supabase auth.users)
CREATE TABLE public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
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
    invoice_count_reset_at TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- OTP requests (for phone verification)
CREATE TABLE public.otp_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone VARCHAR(15) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    pin_id VARCHAR(100),
    attempts INTEGER DEFAULT 0,
    verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- RLS Policies
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own profile" ON public.profiles
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update own profile" ON public.profiles
    FOR UPDATE USING (auth.uid() = id);
```

#### Auth Flow:
1. User enters phone number
2. Next.js API route calls Termii to send OTP
3. Store OTP hash + pin_id in otp_requests table
4. User enters OTP
5. Verify with Termii API
6. If new user: create Supabase auth user + profile
7. If existing: sign in with custom token
8. Redirect to dashboard

#### Implementation Files:
- `src/app/(auth)/login/page.tsx` - Phone input form
- `src/app/(auth)/verify/page.tsx` - OTP verification
- `src/app/api/sms/route.ts` - Termii API handler
- `src/lib/termii.ts` - Termii SDK wrapper
- `src/hooks/use-auth.ts` - Auth state management
- `supabase/functions/send-otp/index.ts` - Edge function for OTP

### Day 5-6: Dashboard Layout

#### Components:
1. **Dashboard Layout**
   - Responsive sidebar (hidden on mobile)
   - Header with user menu
   - Mobile bottom navigation

2. **Protected Route Wrapper**
   - Check Supabase session
   - Redirect to login if unauthenticated
   - Loading state during check

3. **Basic Dashboard Page**
   - Welcome message
   - Empty state prompting first invoice

### Day 7: Customer Management

#### Database:
```sql
CREATE TABLE public.customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(255),
    address TEXT,
    notes TEXT,
    payment_score INTEGER DEFAULT 100,
    total_invoices INTEGER DEFAULT 0,
    total_paid DECIMAL(15, 2) DEFAULT 0,
    total_outstanding DECIMAL(15, 2) DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, phone)
);

ALTER TABLE public.customers ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can manage own customers" ON public.customers
    FOR ALL USING (auth.uid() = user_id);
```

#### Components:
- Customer list with search
- Add customer form (modal)
- Customer select dropdown (for invoice form)

---

## Phase 2: Core Features (Week 2)

### Day 1-2: Invoice CRUD

#### Database:
```sql
CREATE TABLE public.invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    customer_id UUID REFERENCES public.customers(id) ON DELETE SET NULL,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    items JSONB NOT NULL DEFAULT '[]',
    subtotal DECIMAL(12, 2) NOT NULL,
    tax DECIMAL(12, 2) DEFAULT 0,
    discount DECIMAL(12, 2) DEFAULT 0,
    total DECIMAL(12, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'draft',
    issue_date DATE DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    notes TEXT,
    terms TEXT,
    payment_ref VARCHAR(100) UNIQUE,
    payment_link TEXT,
    paystack_access_code VARCHAR(100),
    pdf_url TEXT,
    sent_at TIMESTAMPTZ,
    viewed_at TIMESTAMPTZ,
    paid_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_invoices_user ON public.invoices(user_id);
CREATE INDEX idx_invoices_status ON public.invoices(user_id, status);
CREATE INDEX idx_invoices_payment_ref ON public.invoices(payment_ref);

ALTER TABLE public.invoices ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can manage own invoices" ON public.invoices
    FOR ALL USING (auth.uid() = user_id);

-- Invoice number generation function
CREATE OR REPLACE FUNCTION generate_invoice_number(p_user_id UUID)
RETURNS VARCHAR(50) AS $$
DECLARE
    v_prefix VARCHAR(10);
    v_year_month VARCHAR(6);
    v_sequence INTEGER;
    v_invoice_number VARCHAR(50);
BEGIN
    v_year_month := TO_CHAR(NOW(), 'YYYYMM');

    SELECT COALESCE(MAX(
        CAST(SUBSTRING(invoice_number FROM 'INV-\d{6}-(\d+)') AS INTEGER)
    ), 0) + 1
    INTO v_sequence
    FROM public.invoices
    WHERE user_id = p_user_id
    AND invoice_number LIKE 'INV-' || v_year_month || '-%';

    v_invoice_number := 'INV-' || v_year_month || '-' || LPAD(v_sequence::TEXT, 5, '0');

    RETURN v_invoice_number;
END;
$$ LANGUAGE plpgsql;
```

#### Invoice Form Features:
- Customer selection (or inline create)
- Dynamic item rows (add/remove/reorder)
- Auto-calculate totals
- Due date picker
- Notes field
- Save as draft / Create & Send

#### Invoice List:
- Status filter tabs (All, Draft, Sent, Paid, Overdue)
- Search by invoice number or customer
- Sort by date, amount, status
- Pagination

### Day 3-4: Paystack Payment Integration

#### Implementation:
```typescript
// src/lib/paystack.ts
const PAYSTACK_SECRET = process.env.PAYSTACK_SECRET_KEY!;
const PAYSTACK_BASE_URL = 'https://api.paystack.co';

export async function initializePayment({
  email,
  amount,
  reference,
  callbackUrl,
  metadata
}: {
  email: string;
  amount: number; // in Naira
  reference: string;
  callbackUrl: string;
  metadata: Record<string, any>;
}) {
  const response = await fetch(`${PAYSTACK_BASE_URL}/transaction/initialize`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${PAYSTACK_SECRET}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      email,
      amount: amount * 100, // Convert to kobo
      reference,
      callback_url: callbackUrl,
      channels: ['card', 'bank', 'ussd', 'bank_transfer'],
      metadata
    })
  });

  const data = await response.json();
  return data;
}

export async function verifyPayment(reference: string) {
  const response = await fetch(
    `${PAYSTACK_BASE_URL}/transaction/verify/${reference}`,
    {
      headers: {
        'Authorization': `Bearer ${PAYSTACK_SECRET}`
      }
    }
  );

  return response.json();
}
```

#### Webhook Handler:
```typescript
// src/app/api/webhooks/paystack/route.ts
import { createHmac } from 'crypto';
import { createClient } from '@/lib/supabase/server';

export async function POST(request: Request) {
  const body = await request.text();
  const signature = request.headers.get('x-paystack-signature');

  // Verify signature
  const hash = createHmac('sha512', process.env.PAYSTACK_SECRET_KEY!)
    .update(body)
    .digest('hex');

  if (hash !== signature) {
    return new Response('Invalid signature', { status: 401 });
  }

  const event = JSON.parse(body);

  if (event.event === 'charge.success') {
    const { reference, amount, channel, paid_at } = event.data;

    const supabase = createClient();

    // Update invoice status
    await supabase
      .from('invoices')
      .update({
        status: 'paid',
        paid_at: paid_at
      })
      .eq('payment_ref', reference);

    // Record payment
    await supabase
      .from('payments')
      .insert({
        invoice_id: event.data.metadata.invoice_id,
        amount: amount / 100,
        reference,
        paystack_reference: event.data.id,
        channel,
        status: 'success',
        paid_at
      });
  }

  return new Response('OK', { status: 200 });
}
```

### Day 5-6: WhatsApp Share Integration

#### Custom Hook:
```typescript
// src/hooks/use-whatsapp-share.ts
export function useWhatsAppShare() {
  const formatMessage = (invoice: Invoice, businessName: string) => {
    return `
📄 *Invoice #${invoice.invoice_number}*

Hello ${invoice.customer.name},

Please find your invoice details below:

💰 *Amount Due:* ₦${invoice.total.toLocaleString()}
📅 *Due Date:* ${format(new Date(invoice.due_date), 'MMMM d, yyyy')}

Pay securely here:
${invoice.payment_link}

Thank you for your business!

— ${businessName}
    `.trim();
  };

  const share = async (invoice: Invoice, businessName: string) => {
    const message = formatMessage(invoice, businessName);
    const phone = formatPhone(invoice.customer.phone);

    // Try Web Share API first
    if (navigator.share) {
      try {
        await navigator.share({
          title: `Invoice #${invoice.invoice_number}`,
          text: message
        });
        return { success: true, method: 'native' };
      } catch (e) {
        if ((e as Error).name === 'AbortError') {
          return { success: false, cancelled: true };
        }
      }
    }

    // Fallback to WhatsApp URL
    const url = `https://wa.me/${phone}?text=${encodeURIComponent(message)}`;
    window.open(url, '_blank');
    return { success: true, method: 'url' };
  };

  return { share };
}
```

### Day 7: Payment Page

#### Public Payment Page:
- Accessible without auth: `/pay/[ref]`
- Shows invoice summary
- Customer can view items, total
- Pay button redirects to Paystack
- Success/failure handling

---

## Phase 3: Dashboard & Polish (Week 3)

### Day 1-2: Dashboard Stats

#### Stats to Display:
- Total Revenue (this month)
- Total Invoices
- Paid vs Pending
- Overdue invoices
- Collection rate (%)

#### Database Functions:
```sql
CREATE OR REPLACE FUNCTION get_dashboard_stats(p_user_id UUID)
RETURNS JSON AS $$
DECLARE
    v_result JSON;
BEGIN
    SELECT json_build_object(
        'total_revenue', COALESCE(SUM(CASE WHEN status = 'paid' THEN total ELSE 0 END), 0),
        'total_invoices', COUNT(*),
        'paid_invoices', COUNT(*) FILTER (WHERE status = 'paid'),
        'pending_invoices', COUNT(*) FILTER (WHERE status IN ('sent', 'viewed')),
        'overdue_invoices', COUNT(*) FILTER (WHERE status = 'overdue'),
        'pending_amount', COALESCE(SUM(CASE WHEN status IN ('sent', 'viewed') THEN total ELSE 0 END), 0),
        'overdue_amount', COALESCE(SUM(CASE WHEN status = 'overdue' THEN total ELSE 0 END), 0)
    )
    INTO v_result
    FROM public.invoices
    WHERE user_id = p_user_id
    AND created_at >= date_trunc('month', NOW());

    RETURN v_result;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

### Day 3-4: Recent Activity & Top Customers

#### Activity Feed:
- Invoice created
- Payment received
- Invoice went overdue
- Reminder sent

#### Top Customers:
- Ranked by total paid
- Show invoice count
- Click to view customer details

### Day 5-6: Settings Page

#### Settings Sections:
1. **Business Profile**
   - Business name
   - Business address
   - Logo upload (Supabase Storage)

2. **Bank Details**
   - Bank name (select from list)
   - Account number
   - Account name

3. **Invoice Defaults**
   - Default payment terms
   - Default notes

### Day 7: PWA Configuration

#### PWA Setup:
```javascript
// next.config.js
const withPWA = require('next-pwa')({
  dest: 'public',
  disable: process.env.NODE_ENV === 'development',
  register: true,
  skipWaiting: true
});

module.exports = withPWA({
  // Next.js config
});
```

#### Manifest:
```json
{
  "name": "InvoiceNG",
  "short_name": "InvoiceNG",
  "description": "WhatsApp Invoice & Payment Collection for Nigerian SMEs",
  "start_url": "/",
  "display": "standalone",
  "background_color": "#ffffff",
  "theme_color": "#2563eb",
  "icons": [
    {
      "src": "/icons/icon-192.png",
      "sizes": "192x192",
      "type": "image/png"
    },
    {
      "src": "/icons/icon-512.png",
      "sizes": "512x512",
      "type": "image/png"
    }
  ]
}
```

---

## Phase 4: Launch Prep (Week 4)

### Day 1-2: Testing & Bug Fixes

#### Testing Priorities:
1. Auth flow (login, OTP, session)
2. Invoice CRUD operations
3. Payment flow (create → share → pay → webhook)
4. WhatsApp sharing on mobile
5. Dashboard calculations
6. Edge cases (empty states, errors)

### Day 3-4: Performance & UX

#### Optimizations:
- Image optimization
- Code splitting
- Skeleton loaders
- Error boundaries
- Toast notifications
- Loading states

### Day 5-6: Security Review

#### Checklist:
- [ ] RLS policies cover all tables
- [ ] API routes validate auth
- [ ] Webhook signature verification
- [ ] No secrets in client code
- [ ] HTTPS enforced
- [ ] Input sanitization

### Day 7: Deployment

#### Deployment Steps:
1. **Supabase**
   - Run migrations on production
   - Set up edge functions
   - Configure storage buckets
   - Set environment variables

2. **Vercel**
   - Connect GitHub repo
   - Set environment variables
   - Configure domain
   - Test production build

---

## Environment Variables

```env
# .env.local

# Supabase
NEXT_PUBLIC_SUPABASE_URL=your-project-url
NEXT_PUBLIC_SUPABASE_ANON_KEY=your-anon-key
SUPABASE_SERVICE_ROLE_KEY=your-service-role-key

# Paystack
PAYSTACK_SECRET_KEY=sk_test_xxx
NEXT_PUBLIC_PAYSTACK_PUBLIC_KEY=pk_test_xxx
PAYSTACK_WEBHOOK_SECRET=your-webhook-secret

# Termii
TERMII_API_KEY=your-api-key
TERMII_SENDER_ID=InvoiceNG

# App
NEXT_PUBLIC_APP_URL=https://invoiceng.com
```

---

## Cost Estimates

| Service | Free Tier | Estimated Monthly |
|---------|-----------|-------------------|
| Supabase | 500MB DB, 1GB storage, 2M edge function invocations | ₦0 (free tier) |
| Vercel | 100GB bandwidth, serverless functions | ₦0 (free tier) |
| Termii | Pay-per-SMS | ~₦5,000 (100 OTPs @ ₦50) |
| Domain | - | ~₦8,000/year |
| **Total** | - | **~₦5,000/month** |

---

## Success Metrics (Week 1 Post-Launch)

- [ ] 10+ sign-ups
- [ ] 5+ invoices created
- [ ] 1+ payment processed
- [ ] Average time to first invoice < 5 minutes
- [ ] No critical bugs reported

---

## Next Steps After MVP

### Phase 2 Features (Month 2):
- PDF invoice generation
- Automatic payment reminders
- Email notifications
- Invoice duplication

### Phase 3 Features (Month 3+):
- Subscription/payment tiers
- Recurring invoices
- AI-powered features (chat extraction)
- Mobile app (React Native)

---

## Ready to Start?

To begin implementation, run:
```bash
npx create-next-app@latest invoiceng --typescript --tailwind --eslint --app --src-dir
cd invoiceng
npm install @supabase/supabase-js @supabase/ssr @tanstack/react-query zustand react-hook-form @hookform/resolvers zod date-fns lucide-react clsx tailwind-merge
```

Then proceed with the tasks outlined in each day of the plan.
