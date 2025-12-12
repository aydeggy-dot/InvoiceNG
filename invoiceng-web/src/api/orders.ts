import apiClient from './client';
import type {
  ApiResponse,
  WhatsAppOrder,
  PaginatedResponse,
} from '@/types';

export interface OrderFilters {
  paymentStatus?: string;
  fulfillmentStatus?: string;
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export const ordersApi = {
  getAll: async (params?: OrderFilters): Promise<PaginatedResponse<WhatsAppOrder>> => {
    const response = await apiClient.get<ApiResponse<PaginatedResponse<WhatsAppOrder>>>('/whatsapp-orders', { params });
    return response.data.data;
  },

  getById: async (id: string): Promise<WhatsAppOrder> => {
    const response = await apiClient.get<ApiResponse<WhatsAppOrder>>(`/whatsapp-orders/${id}`);
    return response.data.data;
  },

  getByOrderNumber: async (orderNumber: string): Promise<WhatsAppOrder> => {
    const response = await apiClient.get<ApiResponse<WhatsAppOrder>>(`/whatsapp-orders/by-number/${orderNumber}`);
    return response.data.data;
  },

  markAsPaid: async (id: string, paymentReference?: string, paymentMethod?: string): Promise<WhatsAppOrder> => {
    const response = await apiClient.post<ApiResponse<WhatsAppOrder>>(`/whatsapp-orders/${id}/mark-paid`, null, {
      params: { paymentReference, paymentMethod },
    });
    return response.data.data;
  },

  ship: async (id: string, trackingNumber?: string): Promise<WhatsAppOrder> => {
    const response = await apiClient.post<ApiResponse<WhatsAppOrder>>(`/whatsapp-orders/${id}/ship`, null, {
      params: { trackingNumber },
    });
    return response.data.data;
  },

  deliver: async (id: string): Promise<WhatsAppOrder> => {
    const response = await apiClient.post<ApiResponse<WhatsAppOrder>>(`/whatsapp-orders/${id}/deliver`);
    return response.data.data;
  },

  cancel: async (id: string): Promise<WhatsAppOrder> => {
    const response = await apiClient.post<ApiResponse<WhatsAppOrder>>(`/whatsapp-orders/${id}/cancel`);
    return response.data.data;
  },
};
