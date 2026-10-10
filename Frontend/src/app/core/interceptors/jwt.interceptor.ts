import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

let isRefreshing = false;

export const jwtInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  let modifiedReq = req;

  // Add Authorization header and user identity headers if token exists
  if (token && !req.headers.has('Authorization')) {
    const user = authService.currentUser();
    const headersToAdd: Record<string, string> = {
      Authorization: `Bearer ${token}`
    };
    if (user) {
      if (user.id) headersToAdd['X-User-Id'] = String(user.id);
      if (user.email) headersToAdd['X-User-Email'] = user.email;
      if (user.role) headersToAdd['X-User-Roles'] = user.role;
    }
    modifiedReq = req.clone({
      setHeaders: headersToAdd
    });
  }

  // Add Idempotency-Key for booking or payment mutations if not present
  if ((req.url.includes('/api/v1/reservations') || req.url.includes('/api/v1/payments')) && req.method === 'POST') {
    if (!req.headers.has('Idempotency-Key')) {
      const idempotencyKey = crypto.randomUUID();
      modifiedReq = modifiedReq.clone({
        setHeaders: {
          'Idempotency-Key': idempotencyKey
        }
      });
    }
  }

  return next(modifiedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Avoid refresh loop on auth endpoints
      const isAuthEndpoint = req.url.includes('/api/v1/auth/login') ||
                             req.url.includes('/api/v1/auth/register') ||
                             req.url.includes('/api/v1/auth/refresh-token') ||
                             req.url.includes('/api/v1/auth/forgot-password') ||
                             req.url.includes('/api/v1/auth/verify-otp') ||
                             req.url.includes('/api/v1/auth/reset-password');

      if (error.status === 401 && !isAuthEndpoint) {
        if (!isRefreshing && authService.getRefreshToken()) {
          isRefreshing = true;
          return authService.refreshToken().pipe(
            switchMap(res => {
              isRefreshing = false;
              if (res.success && res.data) {
                const newReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${res.data.accessToken}`
                  }
                });
                return next(newReq);
              }
              authService.logout();
              return throwError(() => error);
            }),
            catchError(refreshErr => {
              isRefreshing = false;
              authService.logout();
              return throwError(() => refreshErr);
            })
          );
        } else {
          authService.logout();
        }
      }

      return throwError(() => error);
    })
  );
};
