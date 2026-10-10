import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import {
  CustomerProfile,
  CustomerProfileRequest,
  ConcessionCard,
  ConcessionRequest,
  SupportQuery,
  CustomerQueryRequest,
  AdminReplyRequest
} from '../models/customer.models';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private http = inject(HttpClient);
  private customersUrl = `${environment.apiBaseUrl}/api/v1/customers`;
  private adminCustomersUrl = `${environment.apiBaseUrl}/api/v1/admin/customers`;
  private adminSupportUrl = `${environment.apiBaseUrl}/api/v1/customers/admin/support/queries`;

  getMyProfile(): Observable<ApiResponse<CustomerProfile>> {
    return this.http.get<ApiResponse<CustomerProfile>>(`${this.customersUrl}/me`);
  }

  getProfile(): Observable<CustomerProfile> {
    return new Observable(observer => {
      this.getMyProfile().subscribe({
        next: (res) => observer.next(res.data),
        error: (err) => observer.error(err),
        complete: () => observer.complete()
      });
    });
  }

  updateMyProfile(request: CustomerProfileRequest): Observable<ApiResponse<CustomerProfile>> {
    return this.http.put<ApiResponse<CustomerProfile>>(`${this.customersUrl}/me`, request);
  }

  updateProfile(request: CustomerProfileRequest): Observable<ApiResponse<CustomerProfile>> {
    return this.updateMyProfile(request);
  }

  getMyConcessions(): Observable<ApiResponse<ConcessionCard[]>> {
    return this.http.get<ApiResponse<ConcessionCard[]>>(`${this.customersUrl}/me/concessions`);
  }

  getConcessions(): Observable<ConcessionCard[]> {
    return new Observable(observer => {
      this.getMyConcessions().subscribe({
        next: (res) => observer.next(res.data || []),
        error: (err) => observer.error(err),
        complete: () => observer.complete()
      });
    });
  }

  addConcession(request: ConcessionRequest): Observable<ApiResponse<ConcessionCard>> {
    return this.http.post<ApiResponse<ConcessionCard>>(`${this.customersUrl}/me/concessions`, request);
  }

  verifyConcession(request: { concessionType: string; concessionNumber: string; customerId?: number }): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.customersUrl}/concessions/verify`, request);
  }

  submitSupportQuery(request: CustomerQueryRequest): Observable<ApiResponse<SupportQuery>> {
    const payload = {
      ...request,
      message: request.message || request.description || '',
      description: request.description || request.message || ''
    };
    return this.http.post<ApiResponse<SupportQuery>>(`${this.customersUrl}/support/queries`, payload);
  }

  getMySupportQueries(): Observable<ApiResponse<SupportQuery[]>> {
    return this.http.get<ApiResponse<SupportQuery[]>>(`${this.customersUrl}/support/my-queries`);
  }

  // Admin Operations
  getAllCustomers(): Observable<ApiResponse<CustomerProfile[]>> {
    return this.http.get<ApiResponse<CustomerProfile[]>>(this.adminCustomersUrl);
  }

  getAllCustomersAdmin(): Observable<ApiResponse<CustomerProfile[]>> {
    return this.getAllCustomers();
  }

  downloadCustomersPdf(status: string = 'ALL'): Observable<Blob> {
    let params = new HttpParams();
    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }
    return this.http.get(`${this.adminCustomersUrl}/pdf`, { params, responseType: 'blob' });
  }

  updateCustomerStatus(id: number, status: 'ACTIVE' | 'SUSPENDED'): Observable<ApiResponse<CustomerProfile>> {
    let params = new HttpParams().set('status', status);
    return this.http.patch<ApiResponse<CustomerProfile>>(`${this.adminCustomersUrl}/${id}/status`, {}, { params });
  }

  getAllSupportQueries(status: string = 'ALL'): Observable<ApiResponse<SupportQuery[]>> {
    let params = new HttpParams().set('status', status);
    return this.http.get<ApiResponse<SupportQuery[]>>(this.adminSupportUrl, { params });
  }

  getAllQueriesAdmin(status: string = 'ALL'): Observable<ApiResponse<SupportQuery[]>> {
    return this.getAllSupportQueries(status);
  }

  replyToSupportQuery(id: number, request: AdminReplyRequest): Observable<ApiResponse<SupportQuery>> {
    const payload = {
      ...request,
      adminReply: request.adminReply || request.replyMessage || '',
      replyMessage: request.replyMessage || request.adminReply || ''
    };
    return this.http.post<ApiResponse<SupportQuery>>(`${this.adminSupportUrl}/${id}/reply`, payload);
  }

  replyToQuery(id: number, replyMessage: string): Observable<ApiResponse<SupportQuery>> {
    return this.replyToSupportQuery(id, { replyMessage, adminReply: replyMessage });
  }
}
