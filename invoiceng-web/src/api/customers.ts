import apiClient from './client';
import type {
  ApiResponse,
  Customer,
  CreateCustomerRequest,
  UpdateCustomerRequest,
  PaginatedResponse,
} from '@/types';

export const customersApi = {
  getAll: async (params?: { search?: string; page?: number; limit?: number }): Promise<Customer[]> => {
    const response = await apiClient.get<ApiResponse<PaginatedResponse<Customer>>>('/customers', { params });
    return response.data.data.data;
  },

  getById: async (id: string): Promise<Customer> => {
    const response = await apiClient.get<ApiResponse<Customer>>(`/customers/${id}`);
    return response.data.data;
  },

  create: async (data: CreateCustomerRequest): Promise<Customer> => {
    const response = await apiClient.post<ApiResponse<Customer>>('/customers', data);
    return response.data.data;
  },

  update: async (id: string, data: Partial<UpdateCustomerRequest>): Promise<Customer> => {
    const response = await apiClient.put<ApiResponse<Customer>>(`/customers/${id}`, data);
    return response.data.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/customers/${id}`);
  },
};
