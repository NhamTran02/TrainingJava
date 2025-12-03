import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { Cart, CartResponse, CartItemRequest } from '../models/cart';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api/carts';
  private cartItemCountSubject = new BehaviorSubject<number>(0);
  public cartItemCount$ = this.cartItemCountSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadCartCount();
  }

  private loadCartCount(): void {
    const userId = this.getUserId();
    if (userId) {
      this.getCart(userId).subscribe({
        next: (cart) => {
          this.cartItemCountSubject.next(cart.items.length);
        },
        error: () => {
          this.cartItemCountSubject.next(0);
        }
      });
    }
  }

  private getUserId(): number | null {
    const user = localStorage.getItem('currentUser');
    if (user) {
      try {
        const userData = JSON.parse(user);
        return userData.id;
      } catch {
        return null;
      }
    }
    return null;
  }

  getCart(userId: number): Observable<Cart> {
    return this.http.get<CartResponse>(`${this.apiUrl}/${userId}`)
      .pipe(map(response => response.result));
  }

  // Add or update item in cart
  addToCart(userId: number, request: CartItemRequest): Observable<Cart> {
    return this.http.post<CartResponse>(`${this.apiUrl}/${userId}`, request)
      .pipe(
        map(response => {
          this.cartItemCountSubject.next(response.result.items.length);
          return response.result;
        })
      );
  }

  // Toggle selected status for a single item
  toggleSelected(userId: number, variantId: number, selected: boolean): Observable<void> {
    return this.http.put<any>(`${this.apiUrl}/${userId}/${variantId}?selected=${selected}`, {});
  }

  // Remove item from cart
  removeItem(userId: number, request: CartItemRequest): Observable<Cart> {
    return this.http.request<CartResponse>('delete', `${this.apiUrl}/${userId}`, {
      body: request
    }).pipe(
      map(response => {
        this.cartItemCountSubject.next(response.result.items.length);
        return response.result;
      })
    );
  }

  // Clear entire cart
  clearCart(cartId: number): Observable<Cart> {
    return this.http.delete<CartResponse>(`${this.apiUrl}/clear/${cartId}`)
      .pipe(
        map(response => {
          this.cartItemCountSubject.next(0);
          return response.result;
        })
      );
  }

  updateCartItemCount(count: number): void {
    this.cartItemCountSubject.next(count);
  }
}
