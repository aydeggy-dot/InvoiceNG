import apiClient from './client';
import type { ApiResponse, DashboardStats, Invoice } from '@/types';

export const dashboardApi = {
  getStats: async (): Promise<DashboardStats> => {
    const response = await apiClient.get<ApiResponse<DashboardStats>>('/dashboard/stats');
    return response.data.data;
  },

  getRecentInvoices: async (limit: number = 5): Promise<Invoice[]> => {
    const response = await apiClient.get<ApiResponse<Invoice[]>>(
      `/dashboard/recent-invoices?limit=${limit}`
    );
    return response.data.data;
  },
};
