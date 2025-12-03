export enum PaymentMethod {
  COD = 'COD',
  VNPAY = 'VNPAY',
  CREDIT_CARD = 'CREDIT_CARD'
}

export enum ShippingType {
  STANDARD = 'STANDARD',
  EXPRESS = 'EXPRESS',
  FAST = 'FAST'
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SHIPPING = 'SHIPPING',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  RETURNED = 'RETURNED'
}

export interface OrderRequest {
  note?: string;
  shippingAddress: string;
  paymentMethod: PaymentMethod;
  shippingType: ShippingType;
}

export interface OrderResponse {
  id: number;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  shippingFee: number;
  trackingNumber?: string;
  paymentUrl?: string;
}

export interface OrderDetailItem {
  productId: number;
  productName: string;
  variant: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface OrderDetailView {
  orderId: number;
  status: string;
  createdAt: string;
  totalAmount: number;      // Tổng tiền sản phẩm (chưa bao gồm ship)
  shippingFee: number;      // Phí vận chuyển
  totalSum: number;         // Tổng cộng (bao gồm ship)
  paymentMethod: string;
  shippingAddress: string;
  trackingNumber: string;
  note?: string;
  buyerName: string;
  buyerPhone: string;
  items: OrderDetailItem[];
}

export interface OrderDetailApiResponse {
  code: number;
  message?: string;
  result: OrderDetailView;
}

export interface OrderApiResponse {
  code: number;
  message?: string;
  result: OrderResponse;
}

export interface OrderListResponse {
  code: number;
  result: OrderResponse[];
}
