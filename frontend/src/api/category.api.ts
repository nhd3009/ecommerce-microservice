import axiosClient from "./axiosClient";
import type { ApiResponse } from "../types/api-response";

export interface Category {
  id: number;
  name: string;
  description?: string;
  status: "ACTIVE" | "INACTIVE";
  createdAt: string;
  updatedAt: string;
}

export const getCategories = () =>
  axiosClient.get<ApiResponse<Category[]>>(
    "/api/v1/categories"
  );

export const getCategoryById = (id: number) =>
  axiosClient.get<ApiResponse<Category>>(
    `/api/v1/categories/${id}`
);

export const createCategory = (category: {
  name: string;
  description?: string;
  status: "ACTIVE" | "INACTIVE";
}) =>
  axiosClient.post<ApiResponse<Category>>(
    "/api/v1/categories",
    category
);

export const updateCategory = (id: number, category: {
  name?: string;
  description?: string;
  status?: "ACTIVE" | "INACTIVE";
}) =>
  axiosClient.put<ApiResponse<Category>>(
    `/api/v1/categories/${id}`,
    category
);

export const toggleCategoryStatus = (id: number) =>
  axiosClient.put<ApiResponse<Category>>(
    `/api/v1/categories/${id}/toggle-status`
);

export const deleteCategory = (id: number) =>
  axiosClient.delete<ApiResponse<null>>(
    `/api/v1/categories/${id}`
);