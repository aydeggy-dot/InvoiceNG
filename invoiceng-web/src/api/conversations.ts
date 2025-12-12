import apiClient from './client';
import type {
  ApiResponse,
  Conversation,
  ConversationMessage,
  PaginatedResponse,
} from '@/types';

export interface ConversationFilters {
  status?: string;
  handedOff?: boolean;
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export const conversationsApi = {
  getAll: async (params?: ConversationFilters): Promise<PaginatedResponse<Conversation>> => {
    const response = await apiClient.get<ApiResponse<PaginatedResponse<Conversation>>>('/conversations', { params });
    return response.data.data;
  },

  getById: async (id: string): Promise<Conversation> => {
    const response = await apiClient.get<ApiResponse<Conversation>>(`/conversations/${id}`);
    return response.data.data;
  },

  getMessages: async (id: string, limit = 100): Promise<ConversationMessage[]> => {
    const response = await apiClient.get<ApiResponse<ConversationMessage[]>>(`/conversations/${id}/messages`, {
      params: { limit },
    });
    return response.data.data;
  },

  sendMessage: async (id: string, content: string): Promise<ConversationMessage> => {
    const response = await apiClient.post<ApiResponse<ConversationMessage>>(`/conversations/${id}/messages`, {
      content,
    });
    return response.data.data;
  },

  getActive: async (): Promise<Conversation[]> => {
    const response = await apiClient.get<ApiResponse<Conversation[]>>('/conversations/active');
    return response.data.data;
  },

  getHandoff: async (page = 1, limit = 20): Promise<Conversation[]> => {
    const response = await apiClient.get<ApiResponse<Conversation[]>>('/conversations/handoff', {
      params: { page, limit },
    });
    return response.data.data;
  },

  requestHandoff: async (id: string, reason?: string): Promise<Conversation> => {
    const response = await apiClient.post<ApiResponse<Conversation>>(`/conversations/${id}/handoff`, {
      reason,
    });
    return response.data.data;
  },

  resolveHandoff: async (id: string): Promise<Conversation> => {
    const response = await apiClient.post<ApiResponse<Conversation>>(`/conversations/${id}/resolve`);
    return response.data.data;
  },

  close: async (id: string): Promise<Conversation> => {
    const response = await apiClient.post<ApiResponse<Conversation>>(`/conversations/${id}/close`);
    return response.data.data;
  },
};
