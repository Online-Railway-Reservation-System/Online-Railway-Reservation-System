import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import { Station, StationRequest, RouteStop, RouteStopRequest } from '../models/station.models';

@Injectable({
  providedIn: 'root'
})
export class StationService {
  private http = inject(HttpClient);
  private stationsUrl = `${environment.apiBaseUrl}/api/v1/stations`;
  private routesUrl = `${environment.apiBaseUrl}/api/v1/routes`;
  private adminStationsUrl = `${environment.apiBaseUrl}/api/v1/admin/stations`;
  private adminRoutesUrl = `${environment.apiBaseUrl}/api/v1/admin/routes`;

  getAllStations(): Observable<ApiResponse<Station[]>> {
    return this.http.get<ApiResponse<Station[]>>(this.stationsUrl);
  }

  getActiveStations(): Observable<Station[]> {
    return this.getAllStations().pipe(map(res => res.data || []));
  }

  getStationById(id: number): Observable<ApiResponse<Station>> {
    return this.http.get<ApiResponse<Station>>(`${this.stationsUrl}/${id}`);
  }

  getStationByCode(code: string): Observable<ApiResponse<Station>> {
    return this.http.get<ApiResponse<Station>>(`${this.stationsUrl}/code/${code}`);
  }

  getRouteStops(trainId: number): Observable<RouteStop[]> {
    return this.http.get<ApiResponse<RouteStop[]>>(`${this.routesUrl}/train/${trainId}`).pipe(
      map(res => res.data || [])
    );
  }

  // Admin Station Management
  createStation(station: StationRequest): Observable<ApiResponse<Station>> {
    return this.http.post<ApiResponse<Station>>(this.adminStationsUrl, station);
  }

  updateStation(stationId: number, station: StationRequest): Observable<ApiResponse<Station>> {
    return this.http.put<ApiResponse<Station>>(`${this.adminStationsUrl}/${stationId}`, station);
  }

  deleteStation(stationId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminStationsUrl}/${stationId}`);
  }

  // Admin Route Management
  createRouteStop(stop: RouteStopRequest): Observable<ApiResponse<RouteStop>> {
    return this.http.post<ApiResponse<RouteStop>>(this.adminRoutesUrl, stop);
  }

  addRouteStop(stop: RouteStopRequest): Observable<ApiResponse<RouteStop>> {
    return this.createRouteStop(stop);
  }

  updateRouteStop(routeId: number, stop: RouteStopRequest): Observable<ApiResponse<RouteStop>> {
    return this.http.put<ApiResponse<RouteStop>>(`${this.adminRoutesUrl}/${routeId}`, stop);
  }

  deleteRouteStop(routeId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.adminRoutesUrl}/${routeId}`);
  }
}
