export interface ProductVariant {
  id: number;
  size: string;
  color: string;
  regularPrice: number;
  salePrice: number;
  stockQuantity: number;
  productId: number;
}

export interface ProductImage {
  id: number;
  url: string;
  isThumbnail: boolean;
  productId: number;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  brandId: number;
  brandName: string;
  categoryId: number;
  categoryName: string;
  deleted: boolean;
  variants: ProductVariant[];
  images: ProductImage[];
  // Computed fields for display
  regularPrice?: number;
  salePrice?: number;
  imageUrl?: string;
}

export interface PagedResponse<T> {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  content: T[];
}

export interface ApiResponse<T> {
  code: number;
  message?: string;
  result: T;
}
