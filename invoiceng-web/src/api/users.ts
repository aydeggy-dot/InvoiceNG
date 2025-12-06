import apiClient from './client';
import type { ApiResponse, User } from '@/types';

export interface UpdateUserRequest {
  email?: string;
  businessName?: string;
  businessAddress?: string;
  bankName?: string;
  bankCode?: string;
  accountNumber?: string;
  accountName?: string;
}

export const usersApi = {
  getMe: async (): Promise<User> => {
    const response = await apiClient.get<ApiResponse<User>>('/users/me');
    return response.data.data;
  },

  updateMe: async (data: UpdateUserRequest): Promise<User> => {
    const response = await apiClient.put<ApiResponse<User>>('/users/me', data);
    return response.data.data;
  },
};
