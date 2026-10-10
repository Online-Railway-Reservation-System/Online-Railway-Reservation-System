import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RefreshTokenRequest,
  ForgotPasswordRequest,
  VerifyOtpRequest,
  ResetPasswordRequest,
  ChangePasswordRequest,
  User
} from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = `${environment.apiBaseUrl}/api/v1/auth`;

  private currentUserSignal = signal<User | null>(this.loadUserFromStorage());

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.currentUserSignal());
  readonly isAdmin = computed(() => this.currentUserSignal()?.role === 'ADMIN');

  login(credentials: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.handleAuthSuccess(res.data);
        }
      })
    );
  }

  register(data: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/register`, data).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.handleAuthSuccess(res.data);
        }
      })
    );
  }

  refreshToken(): Observable<ApiResponse<AuthResponse>> {
    const refreshToken = this.getRefreshToken();
    const req: RefreshTokenRequest = { refreshToken: refreshToken || '' };
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/refresh-token`, req).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.handleAuthSuccess(res.data);
        } else {
          this.logout();
        }
      })
    );
  }

  forgotPassword(req: ForgotPasswordRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/forgot-password`, req);
  }

  verifyOtp(req: VerifyOtpRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/verify-otp`, req);
  }

  resetPassword(req: ResetPasswordRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/reset-password`, req);
  }

  changePassword(req: ChangePasswordRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/change-password`, req);
  }

  logout() {
    const refreshToken = this.getRefreshToken();
    if (refreshToken) {
      this.http.post<ApiResponse<void>>(`${this.apiUrl}/logout`, { refreshToken }).subscribe({
        error: () => {}
      });
    }
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('current_user');
    this.currentUserSignal.set(null);
    this.router.navigate(['/login']);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('access_token');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refresh_token');
  }

  private handleAuthSuccess(auth: AuthResponse) {
    localStorage.setItem('access_token', auth.accessToken);
    localStorage.setItem('refresh_token', auth.refreshToken);

    const user: User = {
      id: auth.userId,
      fullName: auth.fullName,
      email: auth.email,
      role: auth.role,
      active: true
    };

    localStorage.setItem('current_user', JSON.stringify(user));
    this.currentUserSignal.set(user);
  }

  updateCurrentUser(user: User): void {
    localStorage.setItem('current_user', JSON.stringify(user));
    this.currentUserSignal.set(user);
  }

  private loadUserFromStorage(): User | null {
    const stored = localStorage.getItem('current_user');
    if (!stored) return null;
    try {
      return JSON.parse(stored) as User;
    } catch {
      return null;
    }
  }
}
