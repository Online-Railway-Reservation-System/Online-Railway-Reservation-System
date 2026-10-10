import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import { TicketResponse } from '../models/reservation.models';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private http = inject(HttpClient);
  private ticketsUrl = `${environment.apiBaseUrl}/api/v1/tickets`;

  getTicketByPnr(pnr: string): Observable<ApiResponse<TicketResponse>> {
    return this.http.get<ApiResponse<TicketResponse>>(`${this.ticketsUrl}/pnr/${pnr}`);
  }

  getTicketById(id: number): Observable<ApiResponse<TicketResponse>> {
    return this.http.get<ApiResponse<TicketResponse>>(`${this.ticketsUrl}/${id}`);
  }

  downloadTicketPdf(pnr: string): Observable<Blob> {
    return this.http.get(`${this.ticketsUrl}/pnr/${pnr}/pdf`, { responseType: 'blob' });
  }
}
