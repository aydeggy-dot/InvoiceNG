import apiClient from './client';
import type {
  ApiResponse,
  Invoice,
  CreateInvoiceRequest,
  InvoiceListResponse,
} from '@/types';

export const invoicesApi = {
  getAll: async (): Promise<Invoice[]> => {
    const response = await apiClient.get<ApiResponse<InvoiceListResponse>>('/invoices');
    return response.data.data.data;
  },

  getById: async (id: string): Promise<Invoice> => {
    const response = await apiClient.get<ApiResponse<Invoice>>(`/invoices/${id}`);
    return response.data.data;
  },

  create: async (data: CreateInvoiceRequest): Promise<Invoice> => {
    const response = await apiClient.post<ApiResponse<Invoice>>('/invoices', data);
    return response.data.data;
  },

  send: async (id: string): Promise<Invoice> => {
    const response = await apiClient.post<ApiResponse<Invoice>>(`/invoices/${id}/send`);
    return response.data.data;
  },

  cancel: async (id: string): Promise<Invoice> => {
    const response = await apiClient.post<ApiResponse<Invoice>>(`/invoices/${id}/cancel`);
    return response.data.data;
  },

  markPaid: async (id: string): Promise<Invoice> => {
    const response = await apiClient.post<ApiResponse<Invoice>>(`/invoices/${id}/mark-paid`);
    return response.data.data;
  },
};
