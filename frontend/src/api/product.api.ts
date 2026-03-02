import axiosClient from "./axiosClient";
import type { ApiResponse } from "../types/api-response";
import type { Pagination } from "../types/pagination";
import type { Product, AdminProduct, ProductOrderView, ProductFilter } from "../types/product";
import type { CategoryProduct } from "../types/categoryproduct";


export const getProducts = async (params?: {
  page?: number;
  size?: number;
}) => {
  const res = await axiosClient.get<ApiResponse<Pagination<Product>>>(
    "/api/v1/products",
    { params }
  );
  return res.data.data;
};

export const getHomepageProducts = async () : Promise<CategoryProduct[]> => {
  const res = await axiosClient.get<ApiResponse<CategoryProduct[]>>(
    "api/v1/products/all-categories"
  );
  return res.data.data;
};

export const getProductById = async (id: number): Promise<Product> => {
  const res = await axiosClient.get<ApiResponse<Product>>(
    `/api/v1/products/${id}`
  );

  return res.data.data;
};

export const getProductsByCategory = (
  categoryId: number,
  params?: { page?: number; size?: number }
) =>
  axiosClient.get<ApiResponse<Pagination<Product>>>(
    `/api/v1/products/category/${categoryId}`,
    { params }
);

export const filterProducts = async (params?: ProductFilter) : Promise<Pagination<Product>> => {
  const res = await axiosClient.get<ApiResponse<Pagination<Product>>>(
    "/api/v1/products/filter",
    { params }
  );
  return res.data.data;
};

export const createProduct = (formData: FormData) =>
  axiosClient.post<ApiResponse<Product>>(
    "/api/v1/products",
    formData,
    { headers: { "Content-Type": "multipart/form-data" } }
);

export const updateProduct = (id: number, formData: FormData) =>
  axiosClient.put<ApiResponse<Product>>(
    `/api/v1/products/${id}`,
    formData,
    { headers: { "Content-Type": "multipart/form-data" } }
);

export const toggleProductStatus = (id: number) =>
  axiosClient.put<ApiResponse<Product>>(
    `/api/v1/products/${id}/toggle-status`
);

export const adjustProductStock = (
  id: number,
  quantity: number
) =>
  axiosClient.post<ApiResponse<string>>(
    `/api/v1/products/${id}/adjust-stock`,
    null,
    { params: { quantity } }
);

export const deleteProduct = (id: number) =>
  axiosClient.delete<ApiResponse<string>>(
    `/api/v1/products/${id}`
);


export const getInternalProductForOrder = (id: number) =>
  axiosClient.get<ApiResponse<ProductOrderView>>(
    `/api/v1/products/internal/${id}`
);

export const getAdminProducts = (params?: {
  page?: number;
  size?: number;
}) =>
  axiosClient.get<ApiResponse<Pagination<AdminProduct>>>(
    "/api/v1/products/internal",
    { params }
);

export const filterAdminProducts = (params: {
  name?: string;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
}) =>
  axiosClient.get<ApiResponse<Pagination<AdminProduct>>>(
    "/api/v1/products/internal/filter",
    { params }
);