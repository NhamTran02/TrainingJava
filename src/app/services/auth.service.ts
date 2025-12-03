import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, throwError } from 'rxjs';
import { 
  User, 
  LoginRequest, 
  RegisterRequest, 
  LoginResponse,
  RegisterResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  LogoutRequest,
  LogoutResponse
} from '../models/user';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private refreshTokenTimeout?: any;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
    this.startRefreshTokenTimer();
  }

  private loadUserFromStorage(): void {
    try {
      const storedUser = localStorage.getItem('currentUser');
      const accessToken = localStorage.getItem('accessToken');
      
      if (storedUser && storedUser !== 'undefined' && accessToken) {
        this.currentUserSubject.next(JSON.parse(storedUser));
      }
    } catch (error) {
      console.error('Error loading user from localStorage:', error);
      this.clearStorage();
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.code === 200 && response.result) {
          const user: User = {
            id: response.result.userId,
            username: credentials.username,
            email: '',
          };
          
          localStorage.setItem('currentUser', JSON.stringify(user));
          localStorage.setItem('accessToken', response.result.accessToken);
          localStorage.setItem('refreshToken', response.result.refreshToken);
          
          this.currentUserSubject.next(user);
          this.startRefreshTokenTimer();
        }
      }),
      catchError(error => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  register(data: RegisterRequest): Observable<RegisterResponse> {
    // Thêm roleIds mặc định nếu không có
    const registerData = {
      ...data,
      roleIds: data.roleIds || [2] // 2 = USER role
    };
    
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, registerData).pipe(
      catchError(error => {
        console.error('Register error:', error);
        return throwError(() => error);
      })
    );
  }

  refreshToken(): Observable<RefreshTokenResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('No refresh token'));
    }

    const request: RefreshTokenRequest = { refreshToken };
    
    return this.http.post<RefreshTokenResponse>(`${this.apiUrl}/refresh`, request).pipe(
      tap(response => {
        if (response.code === 200 && response.result) {
          localStorage.setItem('accessToken', response.result.accessToken);
          localStorage.setItem('refreshToken', response.result.refreshToken);
          this.startRefreshTokenTimer();
        }
      }),
      catchError(error => {
        console.error('Refresh token error:', error);
        this.logout();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    const refreshToken = localStorage.getItem('refreshToken');
    
    if (refreshToken) {
      const request: LogoutRequest = { refreshToken };
      
      this.http.post<LogoutResponse>(`${this.apiUrl}/logout`, request).subscribe({
        next: (response) => {
          console.log('Logout response:', response.message);
        },
        error: (error) => {
          console.error('Logout error:', error);
        },
        complete: () => {
          this.clearAuthData();
        }
      });
    } else {
      this.clearAuthData();
    }
  }

  private clearAuthData(): void {
    this.stopRefreshTokenTimer();
    this.clearStorage();
    this.currentUserSubject.next(null);
    // Không tự động redirect, để component tự quyết định
  }

  private clearStorage(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  private startRefreshTokenTimer(): void {
    const accessToken = localStorage.getItem('accessToken');
    
    if (!accessToken) return;

    try {
      // Parse JWT để lấy expiry time
      const jwtToken = JSON.parse(atob(accessToken.split('.')[1]));
      const expires = new Date(jwtToken.exp * 1000);
      const timeout = expires.getTime() - Date.now() - (60 * 1000); // Refresh 1 phút trước khi hết hạn
      
      if (timeout > 0) {
        this.refreshTokenTimeout = setTimeout(() => {
          this.refreshToken().subscribe();
        }, timeout);
      }
    } catch (error) {
      console.error('Error parsing token:', error);
    }
  }

  private stopRefreshTokenTimer(): void {
    if (this.refreshTokenTimeout) {
      clearTimeout(this.refreshTokenTimeout);
    }
  }

  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value && !!localStorage.getItem('accessToken');
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }
}
