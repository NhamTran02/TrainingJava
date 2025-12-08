import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

interface ApiResponse<T> {
  code: number;
  message?: string;
  result: T;
}

export interface Brand {
  id: number;
  name: string;
  description?: string;
}

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private apiUrl = 'http://localhost:8080/api/brand';

  constructor(private http: HttpClient) {}

  getAllBrands(): Observable<Brand[]> {
    return this.http.get<ApiResponse<Brand[]>>(this.apiUrl)
      .pipe(map(response => response.result));
  }

  getBrandById(id: number): Observable<Brand> {
    return this.http.get<ApiResponse<Brand>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.result));
  }

  createBrand(brand: { name: string; description?: string }): Observable<Brand> {
    return this.http.post<ApiResponse<Brand>>(this.apiUrl, brand)
      .pipe(map(response => response.result));
  }

  updateBrand(id: number, brand: { name: string; description?: string }): Observable<Brand> {
    return this.http.put<ApiResponse<Brand>>(`${this.apiUrl}/${id}`, brand)
      .pipe(map(response => response.result));
  }

  deleteBrand(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
