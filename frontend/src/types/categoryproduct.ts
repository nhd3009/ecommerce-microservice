import type { Product } from "./product";

export interface CategoryProduct {
    categoryId: number;
    categoryName: string;
    products: Product[];
}