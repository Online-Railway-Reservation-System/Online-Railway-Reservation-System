import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import {
  RefundCalculateRequest,
  RefundCalculateResponse,
  RefundResponse
} from '../models/payment.models';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private http = inject(HttpClient);
  private refundsUrl = `${environment.apiBaseUrl}/api/v1/refunds`;
  private adminPaymentsUrl = `${environment.apiBaseUrl}/api/v1/admin/payments`;

  calculateRefund(request: RefundCalculateRequest): Observable<ApiResponse<RefundCalculateResponse>> {
    return this.http.post<ApiResponse<RefundCalculateResponse>>(`${this.refundsUrl}/calculate`, request);
  }

  getRefundByPnr(pnr: string): Observable<ApiResponse<RefundResponse>> {
    return this.http.get<ApiResponse<RefundResponse>>(`${this.refundsUrl}/pnr/${pnr}`);
  }

  getRefundByReservationId(reservationId: number): Observable<ApiResponse<RefundResponse>> {
    return this.http.get<ApiResponse<RefundResponse>>(`${this.refundsUrl}/reservation/${reservationId}`);
  }

  // Admin Transactions
  getTransactions(): Observable<any> {
    return this.http.get<any>(`${this.adminPaymentsUrl}/transactions`);
  }

  getPaymentSummary(): Observable<any> {
    return this.http.get<any>(`${this.adminPaymentsUrl}/summary`);
  }
}
