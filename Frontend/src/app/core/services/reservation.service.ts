import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import {
  ReservationRequest,
  ReservationResponse,
  TrainDelayNotificationRequest,
  TrainDelayNotificationResponse,
  CancellationRequest,
  CancellationPreviewResponse
} from '../models/reservation.models';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private http = inject(HttpClient);
  private reservationsUrl = `${environment.apiBaseUrl}/api/v1/reservations`;
  private adminReservationsUrl = `${environment.apiBaseUrl}/api/v1/admin/reservations`;

  bookReservation(request: ReservationRequest): Observable<ApiResponse<ReservationResponse>> {
    return this.http.post<ApiResponse<ReservationResponse>>(this.reservationsUrl, request);
  }

  getReservationById(id: number): Observable<ApiResponse<ReservationResponse>> {
    return this.http.get<ApiResponse<ReservationResponse>>(`${this.reservationsUrl}/${id}`);
  }

  getReservationByPnr(pnr: string): Observable<ApiResponse<ReservationResponse>> {
    return this.http.get<ApiResponse<ReservationResponse>>(`${this.reservationsUrl}/pnr/${pnr}`);
  }

  getMyReservations(): Observable<ApiResponse<ReservationResponse[]>> {
    return this.http.get<ApiResponse<ReservationResponse[]>>(`${this.reservationsUrl}/my`);
  }

  cancelReservation(id: number, request?: CancellationRequest | string): Observable<ApiResponse<ReservationResponse>> {
    if (typeof request === 'string') {
      let params = new HttpParams().set('reason', request);
      return this.http.post<ApiResponse<ReservationResponse>>(`${this.reservationsUrl}/${id}/cancel`, { reason: request }, { params });
    }
    const body: CancellationRequest = request || { reason: 'Cancelled by passenger' };
    return this.http.post<ApiResponse<ReservationResponse>>(`${this.reservationsUrl}/${id}/cancel`, body);
  }

  previewCancellation(id: number, passengerIds?: number[]): Observable<ApiResponse<CancellationPreviewResponse>> {
    return this.http.post<ApiResponse<CancellationPreviewResponse>>(`${this.reservationsUrl}/${id}/cancel-preview`, passengerIds || []);
  }

  // Admin Reservations
  getAllReservations(page: number = 0, size: number = 20): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(this.adminReservationsUrl, { params });
  }

  getAllReservationsAdmin(page: number = 0, size: number = 500): Observable<any> {
    return this.getAllReservations(page, size);
  }

  downloadReservationsPdf(trainNumber?: string, journeyDate?: string, status?: string): Observable<Blob> {
    let params = new HttpParams();
    if (trainNumber && trainNumber !== 'ALL') {
      params = params.set('trainNumber', trainNumber);
    }
    if (journeyDate && journeyDate !== 'ALL' && journeyDate.trim() !== '') {
      params = params.set('journeyDate', journeyDate);
    }
    if (status && status !== 'ALL' && status.trim() !== '') {
      params = params.set('status', status);
    }
    return this.http.get(`${this.adminReservationsUrl}/pdf`, { params, responseType: 'blob' });
  }

  broadcastTrainDelay(request: TrainDelayNotificationRequest): Observable<ApiResponse<TrainDelayNotificationResponse>> {
    return this.http.post<ApiResponse<TrainDelayNotificationResponse>>(`${this.adminReservationsUrl}/notify-delay`, request);
  }

  prepareChart(trainId: number, journeyDate: string): Observable<ApiResponse<any>> {
    let params = new HttpParams()
      .set('trainId', trainId.toString())
      .set('journeyDate', journeyDate);
    return this.http.post<ApiResponse<any>>(`${this.adminReservationsUrl}/chart-preparation`, {}, { params });
  }
}
