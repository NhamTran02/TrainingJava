import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { User, UserUpdateRequest, ChangePasswordRequest, UserResponse } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) {}

  getUserById(id: number): Observable<User> {
    return this.http.get<UserResponse>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.result));
  }

  updateUser(id: number, data: UserUpdateRequest): Observable<User> {
    return this.http.put<UserResponse>(`${this.apiUrl}/${id}`, data)
      .pipe(map(response => response.result));
  }

  changePassword(request: ChangePasswordRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/change-password`, request);
  }
}
