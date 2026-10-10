import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  // Public Passenger Routes
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () => import('./features/auth/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'search',
    loadComponent: () => import('./features/search/search.component').then(m => m.SearchComponent)
  },
  {
    path: 'seats/:trainId',
    loadComponent: () => import('./features/seats/seats.component').then(m => m.SeatsComponent)
  },
  {
    path: 'pnr-status',
    loadComponent: () => import('./features/pnr-status/pnr-status.component').then(m => m.PnrStatusComponent)
  },
  {
    path: 'ticket/:pnr',
    loadComponent: () => import('./features/tickets/ticket-detail.component').then(m => m.TicketDetailComponent)
  },
  {
    path: 'tickets/:pnr',
    loadComponent: () => import('./features/tickets/ticket-detail.component').then(m => m.TicketDetailComponent)
  },

  // Protected Customer Routes
  {
    path: 'booking',
    canActivate: [authGuard],
    loadComponent: () => import('./features/booking/booking.component').then(m => m.BookingComponent)
  },
  {
    path: 'bookings',
    canActivate: [authGuard],
    loadComponent: () => import('./features/bookings/my-bookings.component').then(m => m.MyBookingsComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'support',
    canActivate: [authGuard],
    loadComponent: () => import('./features/support/support.component').then(m => m.SupportComponent)
  },

  // Protected Administrator Portal
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/admin/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/admin/dashboard.component').then(m => m.AdminDashboardComponent)
      },
      {
        path: 'trains',
        loadComponent: () => import('./features/admin/trains-admin.component').then(m => m.TrainsAdminComponent)
      },
      {
        path: 'stations',
        loadComponent: () => import('./features/admin/stations-admin.component').then(m => m.StationsAdminComponent)
      },
      {
        path: 'routes',
        loadComponent: () => import('./features/admin/routes-admin.component').then(m => m.RoutesAdminComponent)
      },
      {
        path: 'schedules-fares',
        loadComponent: () => import('./features/admin/schedules-fares-admin.component').then(m => m.SchedulesFaresAdminComponent)
      },
      {
        path: 'reservations',
        loadComponent: () => import('./features/admin/reservations-admin.component').then(m => m.ReservationsAdminComponent)
      },
      {
        path: 'customers',
        loadComponent: () => import('./features/admin/customers-admin.component').then(m => m.CustomersAdminComponent)
      },
      {
        path: 'food',
        loadComponent: () => import('./features/admin/food-admin.component').then(m => m.FoodAdminComponent)
      },
      {
        path: 'support',
        loadComponent: () => import('./features/admin/support-admin.component').then(m => m.SupportAdminComponent)
      }
    ]
  },

  // Fallback Route
  {
    path: '**',
    redirectTo: ''
  }
];
