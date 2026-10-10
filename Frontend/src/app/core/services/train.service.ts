import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import { Train, TrainRequest, TrainReadiness } from '../models/train.models';

@Injectable({
  providedIn: 'root'
})
export class TrainService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}/api/v1/trains`;
  private adminApiUrl = `${environment.apiBaseUrl}/api/v1/admin/trains`;

  getAllTrains(): Observable<ApiResponse<Train[]>> {
    return this.http.get<ApiResponse<Train[]>>(this.apiUrl);
  }

  getTrainById(trainId: number): Observable<ApiResponse<Train>> {
    return this.http.get<ApiResponse<Train>>(`${this.apiUrl}/${trainId}`);
  }

  getTrainByNumber(trainNumber: string): Observable<ApiResponse<Train>> {
    return this.http.get<ApiResponse<Train>>(`${this.apiUrl}/number/${trainNumber}`);
  }

  createTrain(train: TrainRequest): Observable<ApiResponse<Train>> {
    return this.http.post<ApiResponse<Train>>(this.adminApiUrl, train);
  }

  updateTrain(trainId: number, train: TrainRequest): Observable<ApiResponse<Train>> {
    return this.http.put<ApiResponse<Train>>(`${this.adminApiUrl}/${trainId}`, train);
  }

  deleteTrain(trainId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminApiUrl}/${trainId}`);
  }

  deactivateTrain(trainId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminApiUrl}/${trainId}`);
  }

  permanentDeleteTrain(trainId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminApiUrl}/${trainId}/permanent`);
  }

  getAllTrainsReadiness(): Observable<ApiResponse<TrainReadiness[]>> {
    return this.http.get<ApiResponse<TrainReadiness[]>>(`${this.adminApiUrl}/readiness`);
  }

  getTrainReadiness(trainId: number): Observable<ApiResponse<TrainReadiness>> {
    return this.http.get<ApiResponse<TrainReadiness>>(`${this.adminApiUrl}/${trainId}/readiness`);
  }
}
