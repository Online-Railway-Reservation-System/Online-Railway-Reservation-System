import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unexpected error occurred. Please try again.';

      if (error.error) {
        if (typeof error.error === 'string') {
          try {
            const parsed = JSON.parse(error.error);
            errorMessage = parsed.message || parsed.error || error.error;
          } catch {
            errorMessage = error.error;
          }
        } else if (error.error.message) {
          errorMessage = error.error.message;
        } else if (error.error.error) {
          errorMessage = error.error.error;
        }
      }

      if (error.status === 0) {
        errorMessage = 'Network connection failure. Please check if the API Gateway is running on port 8080.';
        toastService.error(errorMessage, 'Network Error');
      } else if (error.status === 403) {
        toastService.error('You do not have administrative permission to perform this action.', 'Access Denied (403)');
      } else if (error.status === 404) {
        // Only toast 404 if not a search empty result
        if (!req.url.includes('/search/trains')) {
          toastService.warning(errorMessage, 'Not Found (404)');
        }
      } else if (error.status === 409) {
        toastService.warning(errorMessage, 'Conflict (409)');
      } else if (error.status >= 500) {
        toastService.error(errorMessage, 'Server Error (500)');
      }

      return throwError(() => error);
    })
  );
};
