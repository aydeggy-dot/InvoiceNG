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

// WhatsApp Conversation types
export interface Conversation {
  id: string;
  customerPhone: string;
  customerName: string | null;
  customerWhatsappId: string | null;
  state: string;
  context: Record<string, unknown>;
  cart: unknown;
  isActive: boolean;
  lastMessageAt: string | null;
  messageCount: number;
  isHandedOff: boolean;
  handedOffAt: string | null;
  handedOffReason: string | null;
  outcome: string | null;
  orderId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ConversationMessage {
  id: string;
  conversationId: string;
  direction: 'inbound' | 'outbound';
  messageType: string;
  content: string;
  mediaUrl: string | null;
  whatsappMessageId: string | null;
  intentDetected: string | null;
  entitiesExtracted: Record<string, unknown> | null;
  aiConfidence: number | null;
  createdAt: string;
}

// WhatsApp Order types
export type PaymentStatus = 'pending' | 'paid' | 'failed' | 'refunded';
export type FulfillmentStatus = 'pending' | 'shipped' | 'delivered' | 'cancelled';

export interface WhatsAppOrderItem {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  total: number;
}

export interface WhatsAppOrder {
  id: string;
  orderNumber: string;
  customerName: string;
  customerPhone: string;
  customerEmail: string | null;
  deliveryAddress: string;
  deliveryArea: string | null;
  deliveryFee: number;
  deliveryNotes: string | null;
  items: WhatsAppOrderItem[];
  subtotal: number;
  discountAmount: number | null;
  discountReason: string | null;
  total: number;
  paymentStatus: PaymentStatus;
  paymentMethod: string | null;
  paymentReference: string | null;
  paymentLink: string | null;
  paidAt: string | null;
  fulfillmentStatus: FulfillmentStatus;
  shippedAt: string | null;
  deliveredAt: string | null;
  trackingNumber: string | null;
  internalNotes: string | null;
  source: string;
  createdAt: string;
  updatedAt: string;
}

// Product types
export type ProductStatus = 'active' | 'draft' | 'archived';

export interface ProductImage {
  id: string;
  url: string;
  altText: string | null;
  position: number;
  isMain: boolean;
}

export interface ProductVariant {
  id: string;
  name: string;
  sku: string | null;
  options: Record<string, unknown>;
  price: number;
  quantity: number;
  imageUrl: string | null;
  inStock: boolean;
}

export interface Product {
  id: string;
  name: string;
  description: string | null;
  shortDescription: string | null;
  category: string | null;
  subcategory: string | null;
  tags: string[];
  price: number;
  compareAtPrice: number | null;
  costPrice: number | null;
  minPrice: number | null;
  hasVariants: boolean;
  variantOptions: Record<string, unknown> | null;
  trackInventory: boolean;
  quantity: number;
  allowBackorder: boolean;
  aiKeywords: string[];
  aiNotes: string | null;
  status: ProductStatus;
  inStock: boolean;
  images: ProductImage[];
  variants: ProductVariant[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductRequest {
  name: string;
  description?: string;
  shortDescription?: string;
  category?: string;
  subcategory?: string;
  tags?: string[];
  price: number;
  compareAtPrice?: number;
  costPrice?: number;
  trackInventory?: boolean;
  quantity?: number;
  allowBackorder?: boolean;
  aiKeywords?: string[];
  aiNotes?: string;
  status?: ProductStatus;
}

export interface UpdateProductRequest {
  name?: string;
  description?: string;
  shortDescription?: string;
  category?: string;
  subcategory?: string;
  tags?: string[];
  price?: number;
  compareAtPrice?: number;
  costPrice?: number;
  trackInventory?: boolean;
  quantity?: number;
  allowBackorder?: boolean;
  aiKeywords?: string[];
  aiNotes?: string;
  status?: ProductStatus;
}

// Analytics types
export interface TimeSeriesData {
  date: string;
  value?: number;
  count?: number;
}

export interface TopProductData {
  productName: string;
  orderCount: number;
  revenue: number;
}

export interface Analytics {
  // Conversation metrics
  totalConversations: number;
  activeConversations: number;
  convertedConversations: number;
  abandonedConversations: number;
  handoffConversations: number;
  conversionRate: number;

  // Order metrics
  totalOrders: number;
  pendingOrders: number;
  paidOrders: number;
  shippedOrders: number;
  deliveredOrders: number;
  cancelledOrders: number;
  totalRevenue: number;
  averageOrderValue: number;

  // Message metrics
  totalMessages: number;
  inboundMessages: number;
  outboundMessages: number;

  // Product metrics
  totalProducts: number;
  activeProducts: number;
  outOfStockProducts: number;

  // Time-series data
  revenueByDay: TimeSeriesData[];
  ordersByDay: TimeSeriesData[];
  conversationsByDay: TimeSeriesData[];

  // Top products
  topProducts?: TopProductData[];
}

export interface QuickSummary {
  conversationsToday: number;
  conversationsThisWeek: number;
  ordersToday: number;
  ordersThisWeek: number;
  revenueThisMonth: number;
  pendingHandoffs: number;
}
