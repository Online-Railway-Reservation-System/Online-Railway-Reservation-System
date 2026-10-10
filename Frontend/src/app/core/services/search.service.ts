import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import { TrainSearchResult } from '../models/train.models';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private http = inject(HttpClient);
  private searchUrl = `${environment.apiBaseUrl}/api/v1/search/trains`;

  searchTrains(
    source: string,
    destination: string,
    journeyDate: string,
    classType: string = 'SL',
    quota: string = 'GENERAL'
  ): Observable<ApiResponse<TrainSearchResult[]>> {
    let params = new HttpParams()
      .set('source', source.trim().toUpperCase())
      .set('destination', destination.trim().toUpperCase())
      .set('journeyDate', journeyDate)
      .set('classType', classType)
      .set('quota', quota);

    return this.http.get<ApiResponse<TrainSearchResult[]>>(this.searchUrl, { params });
  }
}
