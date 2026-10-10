import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastService = inject(ToastService);

  if (authService.isAuthenticated() && authService.isAdmin()) {
    return true;
  }

  toastService.error('Administrator access required. Please login with an admin account.', 'Unauthorized');
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
