import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { SearchService } from '../../core/services/search.service';
import { StationService } from '../../core/services/station.service';
import { BookingStateService } from '../../core/services/booking-state.service';
import { InventoryService } from '../../core/services/inventory.service';
import { ToastService } from '../../core/services/toast.service';
import { TrainSearchResult } from '../../core/models/train.models';
import { Station } from '../../core/models/station.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { PkrCurrencyPipe } from '../../shared/pipes/currency.pipe';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslatePipe, PkrCurrencyPipe],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private searchService = inject(SearchService);
  private stationService = inject(StationService);
  private bookingState = inject(BookingStateService);
  private inventoryService = inject(InventoryService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  stations: Station[] = [];
  source = 'KHI';
  destination = 'LHE';
  journeyDate = '';
  minDate = '';
  classType = 'ALL';
  quota = 'GENERAL';

  loading = false;
  results: TrainSearchResult[] = [];
  activeTab: 'ALL' | 'DIRECT' | 'CONNECTING' = 'ALL';

  selectedTrainId: number | null = null;
  selectedClassType = 'SL';
  availableClasses: string[] = ['1A', '2A', '3A', 'SL', 'CC', '2S'];

  calculateBaseFare(distanceKm: number, classType: string): number {
    const rateMap: { [key: string]: number } = {
      '1A': 2.40,
      '2A': 1.50,
      '3A': 1.05,
      'CC': 0.85,
      '2S': 0.30,
      'SL': 0.45
    };
    const rate = rateMap[classType ? classType.toUpperCase() : 'SL'] || 0.45;
    return Math.round(Math.max(120.0, (distanceKm || 50) * rate) * 100) / 100;
  }

  selectConnectingClass(train: TrainSearchResult, cls: string): void {
    train.classType = cls;
    if (train.legs && train.legs.length >= 2 && train.legs[0] && train.legs[1]) {
      train.legs[0].classType = cls;
      train.legs[0].fare = this.calculateBaseFare(train.legs[0].distanceKm, cls);
      train.legs[1].classType = cls;
      train.legs[1].fare = this.calculateBaseFare(train.legs[1].distanceKm, cls);
      train.fare = Math.round((train.legs[0].fare + train.legs[1].fare) * 100) / 100;

      // Query live inventory for each leg
      train.legs.forEach((leg) => {
        const legDate = leg.departureDate || train.journeyDate || this.journeyDate;
        this.inventoryService.getAvailability(leg.trainId, legDate, cls, this.quota, leg.source, leg.destination).subscribe({
          next: (res) => {
            if (res.data) {
              leg.availableSeats = res.data.availableSeats;
              leg.status = res.data.status;
              leg.generalAvailableSeats = res.data.generalAvailableSeats !== undefined ? res.data.generalAvailableSeats : res.data.availableSeats;
              leg.ladiesAvailableSeats = res.data.ladiesAvailableSeats;
              leg.disabilityAvailableSeats = res.data.disabilityAvailableSeats;
              leg.tatkalAvailableSeats = res.data.tatkalAvailableSeats;
              leg.waitingListCount = res.data.waitingListCount;
              leg.waitingListCode = res.data.waitingListCode;
              this.cdr.markForCheck();
            }
          },
          error: () => {}
        });
      });
    }
    this.cdr.markForCheck();
  }

  getLegBookBtnText(train: TrainSearchResult, legIndex: number): string {
    const leg = train.legs?.[legIndex];
    if (!leg) return 'Book Train';
    const trainNum = leg.trainNumber;
    const genSeats = this.getGeneralSeats(leg);
    if (genSeats > 0 || (leg.availableSeats && leg.availableSeats > 0)) {
      return `🎟️ ${legIndex === 0 ? 'Book Train 1' : 'Book Train 2'} (${trainNum})`;
    }
    if (leg.status === 'NOT_AVAILABLE') {
      return `🚫 Unavailable (${trainNum})`;
    }
    return `⏳ ${legIndex === 0 ? 'Book Train 1 (Waitlist)' : 'Book Train 2 (Waitlist)'} (${trainNum})`;
  }

  isLegBookable(train: TrainSearchResult, legIndex: number): boolean {
    const leg = train.legs?.[legIndex];
    if (!leg) return false;
    if (leg.status === 'NOT_AVAILABLE') return false;
    return true;
  }

  getCombinedClassFare(train: TrainSearchResult, cls: string): number {
    if (!train.legs || train.legs.length < 2 || !train.legs[0] || !train.legs[1]) return train.fare || 0;
    const d1 = train.legs[0].distanceKm || 100;
    const d2 = train.legs[1].distanceKm || 100;
    return Math.round((this.calculateBaseFare(d1, cls) + this.calculateBaseFare(d2, cls)) * 100) / 100;
  }

  getLegFare(train: TrainSearchResult, legIndex: number): number {
    if (!train.legs || train.legs.length <= legIndex || !train.legs[legIndex]) return 0;
    return train.legs[legIndex].fare || 0;
  }

  ngOnInit(): void {
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
    this.journeyDate = this.minDate;

    // Restore last search criteria if present
    const saved = sessionStorage.getItem('last_search_state');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (parsed.source) this.source = parsed.source;
        if (parsed.destination) this.destination = parsed.destination;
        if (parsed.journeyDate && parsed.journeyDate >= this.minDate) this.journeyDate = parsed.journeyDate;
        if (parsed.classType) this.classType = parsed.classType;
        if (parsed.quota) this.quota = parsed.quota;
      } catch (e) {}
    }

    // Load stations for the strip
    this.stationService.getActiveStations().subscribe({
      next: (st) => {
        this.stations = st;
        this.cdr.markForCheck();
      },
      error: () => {}
    });

    // Read route query parameters & sync with session storage
    this.route.queryParams.subscribe((params) => {
      let hasParams = false;
      if (params['source']) { this.source = params['source']; hasParams = true; }
      if (params['destination']) { this.destination = params['destination']; hasParams = true; }
      if (params['journeyDate']) { this.journeyDate = params['journeyDate']; hasParams = true; }
      if (params['classType']) { this.classType = params['classType']; hasParams = true; }
      if (params['quota']) { this.quota = params['quota']; hasParams = true; }

      if (hasParams) {
        sessionStorage.setItem('last_search_state', JSON.stringify({
          source: this.source,
          destination: this.destination,
          journeyDate: this.journeyDate,
          classType: this.classType,
          quota: this.quota
        }));
      }

      if (this.source && this.destination) {
        this.executeSearch(false);
      }
    });
  }

  executeSearch(updateUrl = true): void {
    if (this.source === this.destination) {
      this.toast.warning('Origin and destination stations must be different.');
      return;
    }

    if (updateUrl) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: {
          source: this.source,
          destination: this.destination,
          journeyDate: this.journeyDate,
          classType: this.classType,
          quota: this.quota
        },
        queryParamsHandling: 'merge'
      });
    }

    sessionStorage.setItem('last_search_state', JSON.stringify({
      source: this.source,
      destination: this.destination,
      journeyDate: this.journeyDate,
      classType: this.classType,
      quota: this.quota
    }));

    this.loading = true;
    this.cdr.markForCheck();

    this.searchService.searchTrains(this.source, this.destination, this.journeyDate, this.classType, this.quota)
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (res) => {
          this.results = res.data || [];
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.toast.error(err.error?.message || 'Error searching trains. Please retry.');
          this.results = [];
          this.cdr.markForCheck();
        }
      });
  }

  trainNameFilter: string = '';

  get filteredResults(): TrainSearchResult[] {
    let list = this.results;
    if (this.activeTab === 'DIRECT') {
      list = list.filter(r => r.type !== 'CONNECTING');
    } else if (this.activeTab === 'CONNECTING') {
      list = list.filter(r => r.type === 'CONNECTING');
    }
    if (this.trainNameFilter && this.trainNameFilter.trim()) {
      const q = this.trainNameFilter.toLowerCase().trim();
      list = list.filter(r =>
        (r.trainName && r.trainName.toLowerCase().includes(q)) ||
        (r.trainNumber && r.trainNumber.toLowerCase().includes(q))
      );
    }
    return list;
  }

  get directCount(): number {
    return this.results.filter(r => r.type !== 'CONNECTING').length;
  }

  get connectingCount(): number {
    return this.results.filter(r => r.type === 'CONNECTING').length;
  }

  selectClass(train: TrainSearchResult, cls: string): void {
    this.selectedTrainId = train.trainId;
    this.selectedClassType = cls;
  }

  openSeatMap(train: TrainSearchResult): void {
    const bookDate = train.journeyDate || this.journeyDate;
    this.bookingState.setSelectedSeats([]);
    this.saveBookingContext(train, bookDate);
    this.router.navigate(['/booking'], {
      queryParams: {
        trainId: train.trainId,
        journeyDate: bookDate,
        classType: this.selectedClassType || train.classType || 'SL',
        quota: this.quota,
        source: this.source,
        destination: this.destination,
        selectSeats: 'true'
      }
    });
  }

  proceedToBook(train: TrainSearchResult): void {
    const bookDate = train.journeyDate || this.journeyDate;
    const concreteClass = (this.selectedClassType && this.selectedClassType !== 'ALL')
      ? this.selectedClassType
      : (train.classType && train.classType !== 'ALL' ? train.classType : 'SL');
    this.bookingState.setSelectedSeats([]);
    this.saveBookingContext(train, bookDate);
    this.router.navigate(['/booking'], {
      queryParams: {
        trainId: train.trainId,
        journeyDate: bookDate,
        classType: concreteClass,
        quota: this.quota,
        source: this.source,
        destination: this.destination
      }
    });
  }

  bookConnectingLeg(train: TrainSearchResult, legIndex: number): void {
    if (!train.legs || !train.legs[legIndex]) return;
    const leg = train.legs[legIndex];
    const bookDate = leg.departureDate || train.journeyDate || this.journeyDate;
    const legClass = train.classType || 'SL';
    const legFare = this.getLegFare(train, legIndex);

    const legTrain: TrainSearchResult = {
      trainId: leg.trainId,
      trainNumber: leg.trainNumber,
      trainName: leg.trainName,
      trainType: leg.trainType,
      source: leg.source,
      destination: leg.destination,
      departure: leg.departureTime,
      arrival: leg.arrivalTime,
      duration: leg.duration,
      distanceKm: leg.distanceKm,
      classType: legClass,
      quota: this.quota,
      fare: legFare,
      availableSeats: leg.availableSeats,
      status: leg.status,
      type: 'DIRECT',
      journeyDate: bookDate
    };

    this.bookingState.setSelectedSeats([]);
    this.bookingState.setSelectedTrain(legTrain);
    this.bookingState.setJourneyContext({
      sourceStationCode: leg.source,
      destinationStationCode: leg.destination,
      journeyDate: bookDate,
      classType: legClass,
      quota: this.quota,
      distanceKm: leg.distanceKm,
      baseFare: legFare
    });

    this.router.navigate(['/booking'], {
      queryParams: {
        trainId: leg.trainId,
        journeyDate: bookDate,
        classType: legClass,
        quota: this.quota,
        source: leg.source,
        destination: leg.destination
      }
    });
  }

  viewConnectingLegSeats(train: TrainSearchResult, legIndex: number): void {
    if (!train.legs || !train.legs[legIndex]) return;
    const leg = train.legs[legIndex];
    const bookDate = leg.departureDate || train.journeyDate || this.journeyDate;
    const legClass = train.classType || 'SL';
    const legFare = this.getLegFare(train, legIndex);

    const legTrain: TrainSearchResult = {
      trainId: leg.trainId,
      trainNumber: leg.trainNumber,
      trainName: leg.trainName,
      trainType: leg.trainType,
      source: leg.source,
      destination: leg.destination,
      departure: leg.departureTime,
      arrival: leg.arrivalTime,
      duration: leg.duration,
      distanceKm: leg.distanceKm,
      classType: legClass,
      quota: this.quota,
      fare: legFare,
      availableSeats: leg.availableSeats,
      status: leg.status,
      type: 'DIRECT',
      journeyDate: bookDate
    };

    this.bookingState.setSelectedSeats([]);
    this.bookingState.setSelectedTrain(legTrain);
    this.bookingState.setJourneyContext({
      sourceStationCode: leg.source,
      destinationStationCode: leg.destination,
      journeyDate: bookDate,
      classType: legClass,
      quota: this.quota,
      distanceKm: leg.distanceKm,
      baseFare: legFare
    });

    this.router.navigate(['/booking'], {
      queryParams: {
        trainId: leg.trainId,
        journeyDate: bookDate,
        classType: legClass,
        quota: this.quota,
        source: leg.source,
        destination: leg.destination,
        selectSeats: 'true'
      }
    });
  }


  private saveBookingContext(train: TrainSearchResult, effectiveDate?: string): void {
    const bookDate = effectiveDate || train.journeyDate || this.journeyDate;
    const concreteClass = (this.selectedClassType && this.selectedClassType !== 'ALL')
      ? this.selectedClassType
      : (train.classType && train.classType !== 'ALL' ? train.classType : 'SL');
    this.bookingState.setSelectedTrain(train);
    this.bookingState.setJourneyContext({
      sourceStationCode: this.source,
      destinationStationCode: this.destination,
      journeyDate: bookDate,
      classType: concreteClass,
      quota: this.quota,
      distanceKm: train.distanceKm || 178.0,
      baseFare: train.fare || 891.0
    });
  }

  getTatkalOpeningDate(journeyDateStr?: string): string {
    return this.getTatkalOpeningTimeFull(journeyDateStr);
  }

  getTatkalOpeningDateOnly(journeyDateStr?: string): string {
    const dateStr = journeyDateStr || this.journeyDate;
    if (!dateStr) return '';
    try {
      const parts = dateStr.split('-');
      if (parts.length === 3) {
        const year = parseInt(parts[0], 10);
        const month = parseInt(parts[1], 10) - 1;
        const day = parseInt(parts[2], 10);
        const d = new Date(year, month, day);
        d.setDate(d.getDate() - 1);
        const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
        return d.toLocaleDateString('en-GB', options);
      }
      const d = new Date(dateStr + 'T00:00:00');
      d.setDate(d.getDate() - 1);
      const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
      return d.toLocaleDateString('en-GB', options);
    } catch (e) {
      return '';
    }
  }

  getTatkalOpeningTimeFull(journeyDateStr?: string): string {
    const dateStr = journeyDateStr || this.journeyDate;
    if (!dateStr) return '';
    try {
      const dateOnly = this.getTatkalOpeningDateOnly(dateStr);
      return dateOnly ? `${dateOnly} at 10:00 AM` : '';
    } catch (e) {
      return '';
    }
  }

  formatDisplayDate(dateStr?: string): string {
    const dStr = dateStr || this.journeyDate;
    if (!dStr) return '';
    try {
      const parts = dStr.split('-');
      if (parts.length === 3) {
        const year = parseInt(parts[0], 10);
        const month = parseInt(parts[1], 10) - 1;
        const day = parseInt(parts[2], 10);
        const d = new Date(year, month, day);
        const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
        return d.toLocaleDateString('en-GB', options);
      }
      const d = new Date(dStr + 'T00:00:00');
      const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
      return d.toLocaleDateString('en-GB', options);
    } catch (e) {
      return dStr;
    }
  }

  getArrivalDisplayDate(train: any): string {
    if (train.arrivalDate) {
      return this.formatDisplayDate(train.arrivalDate);
    }
    const depDate = train.journeyDate || this.journeyDate;
    if (!depDate) return '';
    if (train.departure && train.arrival) {
      const [dh, dm] = train.departure.split(':').map((x: string) => parseInt(x, 10));
      const [ah, am] = train.arrival.split(':').map((x: string) => parseInt(x, 10));
      const depM = (dh || 0) * 60 + (dm || 0);
      const arrM = (ah || 0) * 60 + (am || 0);
      if (arrM < depM) {
        const parts = depDate.split('-');
        if (parts.length === 3) {
          const d = new Date(parseInt(parts[0], 10), parseInt(parts[1], 10) - 1, parseInt(parts[2], 10));
          d.setDate(d.getDate() + 1);
          const options: Intl.DateTimeFormatOptions = { day: '2-digit', month: 'short', year: 'numeric' };
          return d.toLocaleDateString('en-GB', options);
        }
      }
    }
    return this.formatDisplayDate(depDate);
  }

  isTatkalOpen(journeyDateStr?: string): boolean {
    const dateStr = journeyDateStr || this.journeyDate;
    if (!dateStr) return false;
    try {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const target = new Date(dateStr + 'T00:00:00');
      const diffMs = target.getTime() - today.getTime();
      const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));
      if (diffDays <= 0) return true;
      if (diffDays === 1) {
        const now = new Date();
        return now.getHours() >= 10;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  getAvailabilityBadgeText(availableSeats: number, waitingListCount?: number, waitingListCode?: string, status?: string): string {
    if (availableSeats && availableSeats > 0) {
      return `AVAILABLE (${availableSeats} seats)`;
    }
    if (status === 'NOT_AVAILABLE') {
      return 'NOT AVAILABLE';
    }
    if (waitingListCode && waitingListCode.trim()) {
      const code = waitingListCode.trim();
      return code.startsWith('WAITLIST') ? code : `WAITLIST (${code})`;
    }
    if (waitingListCount && waitingListCount > 0) {
      return `WAITLIST (GNWL${waitingListCount})`;
    }
    return 'WAITLIST AVAILABLE';
  }

  getWlBadgeText(train: any): string {
    if (!train) return 'WAITLIST AVAILABLE';
    if (train.waitingListCode && train.waitingListCode.trim()) {
      const code = train.waitingListCode.trim();
      return code.startsWith('WAITLIST') ? code : `WAITLIST (${code})`;
    }
    if (train.waitingListCount && train.waitingListCount > 0) {
      return `WAITLIST (GNWL${train.waitingListCount})`;
    }
    return 'WAITLIST AVAILABLE';
  }

  getAvailabilityBadgeClass(availableSeats: number, status?: string): string {
    if (availableSeats && availableSeats > 0) {
      return 'status-avail';
    }
    if (status === 'RAC') {
      return 'status-rac';
    }
    return 'status-wl';
  }

  getGeneralSeats(train: any): number {
    if (!train) return 0;
    if (train.generalAvailableSeats !== undefined && train.generalAvailableSeats !== null) {
      return train.generalAvailableSeats;
    }
    return train.availableSeats || 0;
  }

  getLadiesSeats(train: any): number {
    if (!train) return 0;
    return train.ladiesAvailableSeats || 0;
  }

  getDisabilitySeats(train: any): number {
    if (!train) return 0;
    return train.disabilityAvailableSeats || 0;
  }

  getTatkalSeats(train: any): number {
    if (!train) return 0;
    return train.tatkalAvailableSeats || 0;
  }

  isLadiesConfigured(train: any): boolean {
    if (!train) return false;
    return train.ladiesQuotaConfigured !== false;
  }

  isDisabilityConfigured(train: any): boolean {
    if (!train) return false;
    return train.disabilityQuotaConfigured !== false;
  }

  resetDate(): void {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    this.journeyDate = d.toISOString().split('T')[0];
    this.executeSearch();
  }
}
