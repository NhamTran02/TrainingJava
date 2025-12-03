import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Product, PagedResponse, ApiResponse } from '../models/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/product';

  constructor(private http: HttpClient) {}

  getAllProducts(page: number = 0, size: number = 20, sortBy: string = 'id', sortDir: string = 'ASC'): Observable<PagedResponse<Product>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);
    
    return this.http.get<ApiResponse<PagedResponse<Product>>>(`${this.apiUrl}/get-all-product`, { params })
      .pipe(map(response => response.result));
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<ApiResponse<Product>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.result));
  }

  searchProducts(
    keyword?: string,
    page: number = 0,
    size: number = 20,
    sortBy: string = 'id',
    sortDir: string = 'ASC',
    categoryId?: number,
    brandId?: number,
    minPrice?: number,
    maxPrice?: number,
    sizeShoe?: string,
    color?: string
  ): Observable<ApiResponse<PagedResponse<Product>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    if (keyword) params = params.set('keyword', keyword);
    if (categoryId) params = params.set('categoryId', categoryId.toString());
    if (brandId) params = params.set('brandId', brandId.toString());
    if (minPrice) params = params.set('minPrice', minPrice.toString());
    if (maxPrice) params = params.set('maxPrice', maxPrice.toString());
    if (sizeShoe) params = params.set('sizeShoe', sizeShoe);
    if (color) params = params.set('color', color);
    
    return this.http.get<ApiResponse<PagedResponse<Product>>>(`${this.apiUrl}/search`, { params });
  }

  getRelatedProducts(id: number): Observable<Product[]> {
    return this.http.get<ApiResponse<Product[]>>(`${this.apiUrl}/${id}/related`)
      .pipe(map(response => response.result));
  }

  getProductsByCategory(categoryId: number, page: number = 0, size: number = 20, sortBy: string = 'id', sortDir: string = 'asc'): Observable<PagedResponse<Product>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);
    
    return this.http.get<ApiResponse<PagedResponse<Product>>>(`${this.apiUrl}/${categoryId}/category`, { params })
      .pipe(map(response => response.result));
  }
}
