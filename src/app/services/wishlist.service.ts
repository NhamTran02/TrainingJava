import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Product } from '../models/product';
import { WishlistResponse } from '../models/wishlist';

@Injectable({
  providedIn: 'root'
})
export class WishlistService {
  private apiUrl = 'http://localhost:8080/api/wishlist';

  constructor(private http: HttpClient) {}

  getWishlist(userId: number): Observable<Product[]> {
    return this.http.get<WishlistResponse>(`${this.apiUrl}/${userId}`)
      .pipe(map(response => response.result));
  }

  addToWishlist(userId: number, productId: number): Observable<any> {
    console.log('Adding to wishlist:', { userId, productId, url: `${this.apiUrl}/${userId}/${productId}` });
    return this.http.post(`${this.apiUrl}/${userId}/${productId}`, {});
  }

  removeFromWishlist(userId: number, productId: number): Observable<any> {
    console.log('Removing from wishlist:', { userId, productId, url: `${this.apiUrl}/${userId}/${productId}` });
    return this.http.delete(`${this.apiUrl}/${userId}/${productId}`);
  }
}
