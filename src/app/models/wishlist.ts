import { Product } from './product';

export interface WishlistResponse {
  code: number;
  message?: string;
  result: Product[];
}
