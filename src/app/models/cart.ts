export interface VariantResponse {
  id: number;
  size: string;
  color: string;
}

export interface CartItem {
  cartItemId: number;
  productId: number;
  variantResponse: VariantResponse;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  selected: boolean;
  imageUrl: string;
}

export interface Cart {
  cartId: number;
  items: CartItem[];
  total: number;
}

export interface CartResponse {
  code: number;
  result: Cart;
}

export interface CartItemRequest {
  variantId: number;
  quantity: number;
  selected?: boolean;
}
