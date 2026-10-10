import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import {
  AvailabilityResponse,
  SeatLayoutResponse,
  Coach
} from '../models/inventory.models';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private http = inject(HttpClient);
  private inventoryUrl = `${environment.apiBaseUrl}/api/v1/inventory`;
  private adminCoachesUrl = `${environment.apiBaseUrl}/api/v1/admin/coaches`;
  private adminQuotasUrl = `${environment.apiBaseUrl}/api/v1/admin/quotas`;

  getAvailability(
    trainId: number,
    journeyDate: string,
    classType: string = 'SL',
    quota: string = 'GENERAL',
    fromStation?: string,
    toStation?: string
  ): Observable<ApiResponse<AvailabilityResponse>> {
    let params = new HttpParams()
      .set('trainId', trainId)
      .set('journeyDate', journeyDate)
      .set('classType', classType)
      .set('quota', quota);

    if (fromStation) params = params.set('fromStation', fromStation);
    if (toStation) params = params.set('toStation', toStation);

    return this.http.get<ApiResponse<AvailabilityResponse>>(`${this.inventoryUrl}/availability`, { params });
  }

  getSeatsLayout(
    trainId: number,
    journeyDate: string,
    classType: string = 'SL',
    fromStation?: string,
    toStation?: string
  ): Observable<ApiResponse<any>> {
    let params = new HttpParams()
      .set('journeyDate', journeyDate)
      .set('classType', classType);

    if (fromStation) params = params.set('fromStation', fromStation);
    if (toStation) params = params.set('toStation', toStation);

    return this.http.get<ApiResponse<any>>(`${this.inventoryUrl}/seats/${trainId}`, { params });
  }

  getSeatLayout(
    trainId: number,
    journeyDate: string,
    classType: string = 'SL',
    fromStation?: string,
    toStation?: string
  ): Observable<SeatLayoutResponse> {
    return this.getSeatsLayout(trainId, journeyDate, classType, fromStation, toStation).pipe(
      map(res => {
        const rawData = res.data;
        if (Array.isArray(rawData)) {
          const mappedSeats: any[] = rawData.map((s: any) => ({
            id: s.id,
            seatName: s.seatNumber || `${s.coachNumber}-${s.id}`,
            seatNumber: s.seatNumber || `${s.id}`,
            coach: s.coachNumber || s.coach || 'S1',
            status: s.status || 'AVAILABLE',
            berthType: s.berthType || 'LOWER',
            classType: s.classType || classType,
            quotaType: s.quotaType || 'GENERAL',
            openToAll: !!s.openToAll
          }));
          return {
            trainId,
            trainNumber: rawData[0]?.trainNumber || `TR-${trainId}`,
            journeyDate,
            seats: mappedSeats
          };
        } else if (rawData && Array.isArray(rawData.seats)) {
          return rawData;
        }
        return {
          trainId,
          trainNumber: `TR-${trainId}`,
          journeyDate,
          seats: []
        };
      })
    );
  }

  // Coach Management & Retrieval
  getCoaches(trainId: number): Observable<ApiResponse<Coach[]>> {
    return this.http.get<ApiResponse<Coach[]>>(`${this.inventoryUrl}/coaches/train/${trainId}`);
  }

  getAdminCoaches(trainId: number): Observable<ApiResponse<Coach[]>> {
    return this.http.get<ApiResponse<Coach[]>>(`${this.adminCoachesUrl}/train/${trainId}`);
  }

  createCoach(coach: Coach): Observable<ApiResponse<Coach>> {
    return this.http.post<ApiResponse<Coach>>(this.adminCoachesUrl, coach);
  }

  updateCoach(coachId: number, coach: Partial<Coach>): Observable<ApiResponse<Coach>> {
    return this.http.put<ApiResponse<Coach>>(`${this.adminCoachesUrl}/${coachId}`, coach);
  }

  deleteCoach(coachId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminCoachesUrl}/${coachId}`);
  }
}
