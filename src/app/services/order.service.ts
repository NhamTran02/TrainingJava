import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { OrderRequest, OrderResponse, OrderApiResponse, OrderListResponse, OrderDetailView, OrderDetailApiResponse } from '../models/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  createOrder(userId: number, orderRequest: OrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderApiResponse>(`${this.apiUrl}/${userId}`, orderRequest)
      .pipe(map(response => response.result));
  }

  getMyOrders(userId: number): Observable<OrderResponse[]> {
    return this.http.get<OrderListResponse>(`${this.apiUrl}/${userId}`)
      .pipe(map(response => response.result));
  }

  cancelOrder(orderId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/cancel/${orderId}`, {});
  }

  getOrderDetail(orderId: number, userId: number): Observable<OrderDetailView> {
    return this.http.get<OrderDetailApiResponse>(`http://localhost:8080/api/order-details/${orderId}/${userId}`)
      .pipe(map(response => response.result));
  }

  // Admin methods
  getAllOrders(page: number = 0, size: number = 20, sortBy: string = 'createdAt', sortDir: string = 'DESC'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}`, {
      params: { page: page.toString(), size: size.toString(), sortBy, sortDir }
    }).pipe(map(response => response.result));
  }

  updateOrderStatus(orderId: number, status: string): Observable<OrderResponse> {
    return this.http.put<OrderApiResponse>(`${this.apiUrl}/${orderId}`, null, {
      params: { status }
    }).pipe(map(response => response.result));
  }
}
