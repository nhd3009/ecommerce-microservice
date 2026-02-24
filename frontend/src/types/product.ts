export type ProductStatus = "ACTIVE" | "INACTIVE" | "OUT_OF_STOCK";

export interface Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  thumbnailUrl?: string;
  imageUrls: string[];
  categoryName: string;
  status: ProductStatus;
  createdAt: string;
  updatedAt: string;
}

export interface AdminProduct extends Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  importPrice: number;
  stockQuantity: number;
  thumbnailUrl?: string;
  imageUrls: string[];
  categoryName: string;
  status: ProductStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ProductOrderView {
  productId: number;
  productName: string;
  sellPrice: number;
  importPrice: number;
  categoryId: number;
  categoryName: string;
  stockQuantity: number;
}

export interface ProductFilter {
  name: string;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
}