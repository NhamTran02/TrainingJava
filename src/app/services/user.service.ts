import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { User, UserUpdateRequest, ChangePasswordRequest, UserResponse, UsersListResponse, DeleteUserResponse } from '../models/user';

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

  // Admin methods
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}`);
  }

  deleteUser(id: number): Observable<DeleteUserResponse> {
    return this.http.delete<DeleteUserResponse>(`${this.apiUrl}/${id}`);
  }
}
