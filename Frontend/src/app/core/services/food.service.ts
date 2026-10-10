import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import { FoodMenuItem, FoodOrderResponse } from '../models/food.models';

@Injectable({
  providedIn: 'root'
})
export class FoodService {
  private http = inject(HttpClient);
  private foodUrl = `${environment.apiBaseUrl}/api/v1/food`;
  private adminFoodUrl = `${environment.apiBaseUrl}/api/v1/admin/food`;

  getAvailableMenu(): Observable<ApiResponse<FoodMenuItem[]>> {
    return this.http.get<ApiResponse<FoodMenuItem[]>>(`${this.foodUrl}/menu`);
  }

  getFoodMenu(): Observable<FoodMenuItem[]> {
    return this.getAvailableMenu().pipe(map(res => res.data || []));
  }

  getFoodOrderByReservation(reservationId: number): Observable<ApiResponse<FoodOrderResponse>> {
    return this.http.get<ApiResponse<FoodOrderResponse>>(`${this.foodUrl}/order/${reservationId}`);
  }

  orderFood(reservationId: number, orderRequest: any): Observable<ApiResponse<FoodOrderResponse>> {
    return this.http.post<ApiResponse<FoodOrderResponse>>(`${this.foodUrl}/order/${reservationId}`, orderRequest);
  }

  // Admin Catering Management
  getAllMenuItems(): Observable<ApiResponse<FoodMenuItem[]>> {
    return this.http.get<ApiResponse<FoodMenuItem[]>>(`${this.adminFoodUrl}/menu`);
  }

  createMenuItem(item: Partial<FoodMenuItem>): Observable<ApiResponse<FoodMenuItem>> {
    return this.http.post<ApiResponse<FoodMenuItem>>(`${this.adminFoodUrl}/menu`, item);
  }

  updateMenuItem(id: number, item: Partial<FoodMenuItem>): Observable<ApiResponse<FoodMenuItem>> {
    return this.http.put<ApiResponse<FoodMenuItem>>(`${this.adminFoodUrl}/menu/${id}`, item);
  }

  deleteMenuItem(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminFoodUrl}/menu/${id}`);
  }

  updateAvailability(id: number, available: boolean): Observable<ApiResponse<FoodMenuItem>> {
    return this.http.patch<ApiResponse<FoodMenuItem>>(`${this.adminFoodUrl}/menu/${id}/availability?available=${available}`, {});
  }
}
