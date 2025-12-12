import apiClient from './client';
import type {
  ApiResponse,
  Product,
  CreateProductRequest,
  UpdateProductRequest,
  PaginatedResponse,
} from '@/types';

export interface ProductFilters {
  category?: string;
  status?: string;
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export const productsApi = {
  getAll: async (params?: ProductFilters): Promise<PaginatedResponse<Product>> => {
    const response = await apiClient.get<ApiResponse<PaginatedResponse<Product>>>('/products', { params });
    return response.data.data;
  },

  getById: async (id: string): Promise<Product> => {
    const response = await apiClient.get<ApiResponse<Product>>(`/products/${id}`);
    return response.data.data;
  },

  search: async (query: string): Promise<Product[]> => {
    const response = await apiClient.get<ApiResponse<Product[]>>('/products/search', {
      params: { q: query },
    });
    return response.data.data;
  },

  getCategories: async (): Promise<string[]> => {
    const response = await apiClient.get<ApiResponse<string[]>>('/products/categories');
    return response.data.data;
  },

  create: async (data: CreateProductRequest): Promise<Product> => {
    const response = await apiClient.post<ApiResponse<Product>>('/products', data);
    return response.data.data;
  },

  update: async (id: string, data: UpdateProductRequest): Promise<Product> => {
    const response = await apiClient.put<ApiResponse<Product>>(`/products/${id}`, data);
    return response.data.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/products/${id}`);
  },

  addImage: async (id: string, imageData: { url: string; altText?: string; isMain?: boolean }): Promise<Product> => {
    const response = await apiClient.post<ApiResponse<Product>>(`/products/${id}/images`, imageData);
    return response.data.data;
  },

  deleteImage: async (productId: string, imageId: string): Promise<void> => {
    await apiClient.delete(`/products/${productId}/images/${imageId}`);
  },

  updateInventory: async (id: string, adjustment: number): Promise<Product> => {
    const response = await apiClient.patch<ApiResponse<Product>>(`/products/${id}/inventory`, null, {
      params: { adjustment },
    });
    return response.data.data;
  },
};
