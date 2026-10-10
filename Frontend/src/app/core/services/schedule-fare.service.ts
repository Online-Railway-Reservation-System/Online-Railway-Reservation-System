import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import {
  TrainSchedule,
  TrainScheduleRequest,
  FareCalculationRequest,
  FareCalculationResponse,
  TatkalConfig,
  FareRule
} from '../models/schedule-fare.models';

@Injectable({
  providedIn: 'root'
})
export class ScheduleFareService {
  private http = inject(HttpClient);
  private schedulesUrl = `${environment.apiBaseUrl}/api/v1/schedules`;
  private faresUrl = `${environment.apiBaseUrl}/api/v1/fares`;
  private adminSchedulesUrl = `${environment.apiBaseUrl}/api/v1/admin/schedules`;
  private adminFaresUrl = `${environment.apiBaseUrl}/api/v1/admin/fares`;

  getSchedules(trainId: number): Observable<ApiResponse<TrainSchedule[]>> {
    return this.http.get<ApiResponse<TrainSchedule[]>>(`${this.schedulesUrl}/train/${trainId}`);
  }

  getSchedule(trainId: number): Observable<ApiResponse<TrainSchedule[]>> {
    return this.getSchedules(trainId);
  }

  calculateFare(request: FareCalculationRequest): Observable<ApiResponse<FareCalculationResponse>> {
    return this.http.post<ApiResponse<FareCalculationResponse>>(`${this.faresUrl}/calculate`, request);
  }

  getTatkalConfig(trainId: number): Observable<ApiResponse<TatkalConfig>> {
    return this.http.get<ApiResponse<TatkalConfig>>(`${this.faresUrl}/tatkal-config/${trainId}`);
  }

  // Admin Schedules
  createSchedule(schedule: TrainScheduleRequest): Observable<ApiResponse<TrainSchedule>> {
    return this.http.post<ApiResponse<TrainSchedule>>(this.adminSchedulesUrl, schedule);
  }

  updateSchedule(scheduleId: number, schedule: TrainScheduleRequest): Observable<ApiResponse<TrainSchedule>> {
    return this.http.put<ApiResponse<TrainSchedule>>(`${this.adminSchedulesUrl}/${scheduleId}`, schedule);
  }

  deleteSchedule(scheduleId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminSchedulesUrl}/${scheduleId}`);
  }

  // Admin Tatkal Config
  saveTatkalConfig(config: TatkalConfig): Observable<ApiResponse<TatkalConfig>> {
    return this.http.post<ApiResponse<TatkalConfig>>(`${this.adminFaresUrl}/tatkal-config`, config);
  }

  // Fare Rules
  getFareRules(trainId: number): Observable<ApiResponse<FareRule[]>> {
    return this.http.get<ApiResponse<FareRule[]>>(`${this.faresUrl}/train/${trainId}`);
  }

  saveFareRule(rule: FareRule): Observable<ApiResponse<FareRule>> {
    return this.http.post<ApiResponse<FareRule>>(this.adminFaresUrl, rule);
  }
}
