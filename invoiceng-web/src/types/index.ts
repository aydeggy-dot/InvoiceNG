// User types
export interface User {
  id: string;
  phone: string;
  email: string | null;
  businessName: string | null;
  businessAddress: string | null;
  bankName: string | null;
  bankCode: string | null;
  accountNumber: string | null;
  accountName: string | null;
  logoUrl: string | null;
  subscriptionTier: string;
  invoiceCountThisMonth: number;
  createdAt: string;
}

// Customer types
export interface Customer {
  id: string;
  name: string;
  phone: string;
  email: string | null;
  address: string | null;
  notes: string | null;
  paymentScore: number;
  totalInvoices: number;
  totalPaid: number;
  totalOutstanding: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCustomerRequest {
  name: string;
  phone: string;
  email?: string;
  address?: string;
  notes?: string;
}

export interface UpdateCustomerRequest {
  name?: string;
  phone?: string;
  email?: string;
  address?: string;
  notes?: string;
}

// Invoice types
export type InvoiceStatus = 'draft' | 'sent' | 'viewed' | 'paid' | 'overdue' | 'cancelled';

export interface InvoiceItem {
  id?: string;
  name: string;
  description?: string;
  quantity: number;
  price: number;
  total: number;
}

export interface Invoice {
  id: string;
  invoiceNumber: string;
  customerId?: string;
  customer?: {
    id: string;
    name: string;
    phone: string;
    email: string | null;
  } | null;
  items: InvoiceItem[];
  subtotal: number;
  tax: number;
  discount: number;
  total: number;
  paidAmount: number;
  status: string;
  issueDate: string;
  dueDate: string;
  notes: string | null;
  sentAt?: string | null;
  paidAt?: string | null;
  createdAt: string;
  updatedAt: string;
  businessName?: string;
}

export interface CreateInvoiceRequest {
  customerId: string;
  dueDate: string;
  items: {
    name: string;
    description?: string;
    quantity: number;
    price: number;
  }[];
  notes?: string;
}

export interface UpdateInvoiceRequest {
  customerId?: string;
  items?: Omit<InvoiceItem, 'id' | 'total'>[];
  tax?: number;
  discount?: number;
  dueDate?: string;
  notes?: string;
  terms?: string;
}

export interface InvoiceSummary {
  totalAmount: number;
  paidAmount: number;
  pendingAmount: number;
  overdueAmount: number;
  totalCount: number;
  paidCount: number;
  pendingCount: number;
  overdueCount: number;
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  errors?: Record<string, string>;
  timestamp: string;
}

export interface PaginationInfo {
  page: number;
  limit: number;
  total: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: PaginationInfo;
}

export interface InvoiceListResponse {
  data: Invoice[];
  pagination: PaginationInfo;
  summary: InvoiceSummary;
}

// Auth types
export interface AuthResponse {
  token: string;
  refreshToken: string;
  expiresIn: number;
  user: User;
  isNewUser: boolean;
}

export interface OtpResponse {
  message: string;
  expiresIn: number;
}

// Dashboard types
export interface DashboardOverview {
  totalRevenue: number;
  pendingAmount: number;
  totalInvoices: number;
  totalCustomers: number;
  paidInvoices: number;
  pendingInvoices: number;
  overdueInvoices: number;
  draftInvoices: number;
}

export interface DashboardComparison {
  revenueChange: number;
  invoiceChange: number;
}

export interface ActivityItem {
  type: 'invoice_created' | 'payment_received' | 'invoice_overdue';
  invoiceId: string;
  invoiceNumber: string;
  customerName: string;
  amount: number;
  timestamp: string;
}

export interface DashboardStats {
  overview: DashboardOverview;
  comparison: DashboardComparison;
  recentActivity: ActivityItem[];
}
