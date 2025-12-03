// User Profile interfaces
export interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  phoneNumber?: string;
  address?: string;
  roleNames?: string[];
  deleted?: boolean;
}

export interface UserUpdateRequest {
  fullName: string;
  phoneNumber: string;
  address: string;
  password?: string;
}

export interface ChangePasswordRequest {
  username: string;
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface UserResponse {
  code: number;
  result: User;
}

// Auth interfaces
export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
  phoneNumber: string;
  address: string;
  roleIds?: number[];
}

export interface LoginResponse {
  code: number;
  message?: string;
  result: {
    userId: number;
    accessToken: string;
    refreshToken: string;
  };
}

export interface RegisterResponse {
  code: number;
  message: string;
  result?: any;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  code: number;
  result: {
    accessToken: string;
    refreshToken: string;
  };
}

export interface LogoutRequest {
  refreshToken: string;
}

export interface LogoutResponse {
  code: number;
  message: string;
}
