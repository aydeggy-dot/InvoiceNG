import apiClient from './client';
import type { ApiResponse, AuthResponse, OtpResponse } from '@/types';

export const authApi = {
  requestOtp: async (phone: string): Promise<OtpResponse> => {
    const response = await apiClient.post<ApiResponse<OtpResponse>>('/auth/request-otp', {
      phone,
    });
    return response.data.data;
  },

  verifyOtp: async (phone: string, otp: string): Promise<AuthResponse> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>('/auth/verify-otp', {
      phone,
      otp,
    });
    return response.data.data;
  },

  refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(
      '/auth/refresh',
      {},
      {
        headers: {
          Authorization: `Bearer ${refreshToken}`,
        },
      }
    );
    return response.data.data;
  },
};
