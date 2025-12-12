import apiClient from './client';
import type {
  ApiResponse,
  Analytics,
  QuickSummary,
} from '@/types';

export const analyticsApi = {
  getAnalytics: async (days = 30): Promise<Analytics> => {
    const response = await apiClient.get<ApiResponse<Analytics>>('/analytics', {
      params: { days },
    });
    return response.data.data;
  },

  getQuickSummary: async (): Promise<QuickSummary> => {
    const response = await apiClient.get<ApiResponse<QuickSummary>>('/analytics/summary');
    return response.data.data;
  },
};
