import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { finalize, combineLatest, Subscription, timer } from 'rxjs';
import { InventoryService } from '../../core/services/inventory.service';
import { BookingStateService } from '../../core/services/booking-state.service';
import { ToastService } from '../../core/services/toast.service';
import { SeatLayout, SeatMapItem, Coach } from '../../core/models/inventory.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { PkrCurrencyPipe } from '../../shared/pipes/currency.pipe';

import { AuthService } from '../../core/services/auth.service';
import { CustomerService } from '../../core/services/customer.service';

@Component({
  selector: 'app-seats',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslatePipe, PkrCurrencyPipe],
  templateUrl: './seats.component.html',
  styleUrl: './seats.component.css'
})
export class SeatsComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private inventoryService = inject(InventoryService);
  private bookingState = inject(BookingStateService);
  private authService = inject(AuthService);
  private customerService = inject(CustomerService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  userGender = 'MALE';

  trainId = 1;
  journeyDate = '';
  classType = 'SL';
  quota = 'GENERAL';
  fromStation = '';
  toStation = '';

  loading = true;
  loadingCoaches = false;
  trainCoaches: Coach[] = [];
  layout: SeatLayout | null = null;
  coaches: string[] = [];
  activeCoach = '';
  private livePollingSub?: Subscription;

  ngOnDestroy(): void {
    this.stopDynamicSeatSync();
  }

  selectedSeats: SeatMapItem[] = [];
  unitFare = 891.0;

  ngOnInit(): void {
    combineLatest([this.route.paramMap, this.route.queryParams]).subscribe(([pMap, qParams]) => {
      const pTrainId = Number(pMap.get('trainId'));
      if (pTrainId) {
        this.trainId = pTrainId;
      }
      this.journeyDate = qParams['journeyDate'] || new Date().toISOString().split('T')[0];
      this.classType = qParams['classType'] || 'SL';
      this.quota = qParams['quota'] || 'GENERAL';
      this.fromStation = qParams['source'] || qParams['fromStation'] || qParams['sourceStationCode'] || '';
      this.toStation = qParams['destination'] || qParams['toStation'] || qParams['destinationStationCode'] || '';

      const ctx = this.bookingState.journeyContext();
      if (!this.fromStation && ctx?.sourceStationCode) this.fromStation = ctx.sourceStationCode;
      if (!this.toStation && ctx?.destinationStationCode) this.toStation = ctx.destinationStationCode;

      this.selectedSeats = [];
      this.loadTrainCoaches();
    });

    const ctx = this.bookingState.journeyContext();
    if (ctx?.baseFare) {
      this.unitFare = ctx.baseFare;
    }

    if (this.authService.isAuthenticated()) {
      this.customerService.getProfile().subscribe({
        next: (profile) => {
          if (profile && profile.gender) {
            this.userGender = profile.gender.toUpperCase().trim();
          }
        },
        error: () => {}
      });
    }
  }

  onJourneyDateChange(newDate: string): void {
    if (!newDate || newDate === this.journeyDate) return;
    this.journeyDate = newDate;
    this.selectedSeats = [];
    this.layout = null;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { journeyDate: this.journeyDate },
      queryParamsHandling: 'merge'
    });
    this.loadSeatLayout();
    this.cdr.markForCheck();
  }

  onCoachDropdownChange(coachNum: string): void {
    const coach = this.trainCoaches.find(c => c.coachNumber === coachNum);
    if (coach) {
      this.selectCoachOption(coach);
    }
  }

  loadTrainCoaches(): void {
    this.loadingCoaches = true;
    this.loading = true;
    this.cdr.markForCheck();
    this.inventoryService.getCoaches(this.trainId).subscribe({
      next: (res) => {
        this.loadingCoaches = false;
        const raw = (res && res.data) ? res.data.filter(c => c.activeStatus !== false && c.totalSeats && c.totalSeats > 0) : [];
        const map = new Map<string, Coach>();
        raw.forEach(c => {
          const key = (c.coachNumber || '').trim().toUpperCase();
          if (key && !map.has(key)) {
            map.set(key, c);
          }
        });
        this.trainCoaches = Array.from(map.values()).sort((a, b) =>
          (a.coachNumber || '').localeCompare(b.coachNumber || '', undefined, { numeric: true })
        );

        if (this.trainCoaches.length > 0) {
          let matched = this.trainCoaches.find(c => (c.coachNumber || '').toUpperCase() === (this.activeCoach || '').toUpperCase());
          if (!matched && this.classType) {
            matched = this.trainCoaches.find(c => (c.classType || '').toUpperCase() === this.classType.toUpperCase());
          }
          const selected = matched || this.trainCoaches[0];
          this.activeCoach = selected.coachNumber;
          if (selected.classType && selected.classType !== 'ALL') {
            this.classType = selected.classType;
          }
          this.loadSeatLayout();
        } else {
          this.trainCoaches = [];
          this.coaches = [];
          this.layout = null;
          this.loading = false;
          this.cdr.markForCheck();
        }
      },
      error: () => {
        this.loadingCoaches = false;
        this.trainCoaches = [];
        this.coaches = [];
        this.layout = null;
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadSeatLayout(): void {
    if (!this.trainCoaches || this.trainCoaches.length === 0) {
      this.layout = null;
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }
    this.loading = true;
    this.cdr.markForCheck();

    this.inventoryService.getSeatLayout(this.trainId, this.journeyDate, this.classType, this.fromStation, this.toStation)
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (layout) => {
          this.layout = layout;
          if (layout && layout.seats && layout.seats.length > 0) {
            const coachSet = new Set<string>();
            layout.seats.forEach(s => {
              if (s.coach) coachSet.add(s.coach);
            });
            this.coaches = Array.from(coachSet).sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));
            if (this.coaches.length > 0 && !this.coaches.includes(this.activeCoach)) {
              this.activeCoach = this.coaches[0];
            }
            this.autoPrioritizeQuotaSeats();
          } else {
            this.layout = {
              trainId: this.trainId,
              trainNumber: 'TR-' + this.trainId,
              journeyDate: this.journeyDate,
              seats: []
            };
            this.coaches = [];
          }
          this.startDynamicSeatSync();
          this.cdr.markForCheck();
        },
        error: () => {
          this.layout = null;
          this.coaches = [];
          this.startDynamicSeatSync();
          this.cdr.markForCheck();
        }
      });
  }

  selectCoachOption(coach: Coach): void {
    if (this.activeCoach === coach.coachNumber && this.classType === coach.classType) return;
    this.activeCoach = coach.coachNumber;
    if (coach.classType && coach.classType !== 'ALL') {
      this.classType = coach.classType;
    }
    this.selectedSeats = [];
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { classType: this.classType },
      queryParamsHandling: 'merge'
    });
    this.loadSeatLayout();
    this.cdr.markForCheck();
  }

  startDynamicSeatSync(): void {
    this.stopDynamicSeatSync();
    // Dynamically poll inventory every 8 seconds to lock newly booked seats in real time
    this.livePollingSub = timer(8000, 8000).subscribe(() => {
      if (!this.loading && this.layout && this.layout.seats) {
        this.inventoryService.getSeatLayout(this.trainId, this.journeyDate, this.classType, this.fromStation, this.toStation)
          .subscribe({
            next: (freshLayout) => {
              if (freshLayout && freshLayout.seats && this.layout && this.layout.seats) {
                let updatedAny = false;
                freshLayout.seats.forEach(freshSeat => {
                  const existing = this.layout!.seats.find(s => s.seatNumber === freshSeat.seatNumber && s.coach === freshSeat.coach);
                  if (existing && existing.status !== freshSeat.status) {
                    existing.status = freshSeat.status;
                    updatedAny = true;
                    if (freshSeat.status !== 'AVAILABLE') {
                      const idx = this.selectedSeats.findIndex(s => s.seatNumber === freshSeat.seatNumber && s.coach === freshSeat.coach);
                      if (idx >= 0) {
                        this.selectedSeats.splice(idx, 1);
                        this.toast.warning(`Seat ${freshSeat.seatNumber} was booked by another passenger and is now dynamically locked in red.`);
                      }
                    }
                  }
                });
                if (updatedAny) {
                  this.cdr.markForCheck();
                }
              }
            },
            error: () => {}
          });
      }
    });
  }

  stopDynamicSeatSync(): void {
    if (this.livePollingSub) {
      this.livePollingSub.unsubscribe();
      this.livePollingSub = undefined;
    }
  }

  scrollCarriage(delta: number): void {
    const el = document.getElementById('seatsCarriageScroll');
    if (el) {
      el.scrollBy({ left: delta, behavior: 'smooth' });
    }
  }

  selectCoach(coach: string): void {
    this.activeCoach = coach;
    this.cdr.markForCheck();
  }

  get isChair(): boolean {
    return this.classType === 'CC' || this.classType === '2S';
  }

  get is1A(): boolean {
    return this.classType === '1A';
  }

  get activeCoachStats() {
    const seats = this.activeCoachSeats;
    const total = seats.length;
    const booked = seats.filter(s => s.status === 'BOOKED' || s.status === 'CONFIRMED').length;
    const available = seats.filter(s => s.status === 'AVAILABLE').length;
    const selected = this.selectedSeats.filter(s => s.coach === this.activeCoach).length;
    return { total, booked, available, selected };
  }

  autoPrioritizeQuotaSeats(): void {
    if (!this.layout || !this.layout.seats) return;
    if (this.selectedSeats.length > 0) return;

    if (this.quota === 'LADIES') {
      const preferredSeat = this.layout.seats.find(s => s.status === 'AVAILABLE' && s.quotaType === 'LADIES');
      if (preferredSeat) {
        this.selectedSeats = [preferredSeat];
        this.activeCoach = preferredSeat.coach;
      }
    } else if (this.quota === 'DISABILITY' || this.quota === 'SENIOR_CITIZEN') {
      const preferredSeat = this.layout.seats.find(s => s.status === 'AVAILABLE' && (s.quotaType === 'DISABILITY' || s.berthType === 'LOWER'));
      if (preferredSeat) {
        this.selectedSeats = [preferredSeat];
        this.activeCoach = preferredSeat.coach;
      }
    }
  }

  isTatkalOpen(): boolean {
    if (!this.journeyDate) return false;
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const parts = this.journeyDate.split('-');
    if (parts.length !== 3) return false;
    const jDate = new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
    const diffMs = jDate.getTime() - today.getTime();
    const diffDays = Math.round(diffMs / (1000 * 60 * 60 * 24));
    if (diffDays <= 0) return true;
    if (diffDays === 1) return now.getHours() >= 10;
    return false;
  }

  get activeCoachSeats(): SeatMapItem[] {
    if (!this.layout || !this.layout.seats) return [];
    return this.layout.seats
      .filter(s => s.coach === this.activeCoach)
      .sort((a, b) => this.extractSeatNumber(a.seatNumber || a.seatName) - this.extractSeatNumber(b.seatNumber || b.seatName));
  }

  private extractSeatNumber(seatNum: string | undefined): number {
    if (!seatNum) return 0;
    const match = seatNum.match(/(\d+)$/);
    return match ? parseInt(match[1], 10) : 0;
  }

  get activeCoachBays(): { bayNumber: number; mainBay: SeatMapItem[]; sideBay: SeatMapItem[] }[] {
    const seats = this.activeCoachSeats;
    if (seats.length === 0) return [];

    const is2A = this.classType === '2A';
    const is1A = this.classType === '1A';
    const isChair = this.classType === 'CC' || this.classType === '2S';
    const baySize = is1A ? 4 : (is2A ? 6 : (isChair ? 5 : 8));

    const bays: { bayNumber: number; mainBay: SeatMapItem[]; sideBay: SeatMapItem[] }[] = [];
    for (let i = 0; i < seats.length; i += baySize) {
      const slice = seats.slice(i, i + baySize);
      let sideBay: SeatMapItem[] = [];
      let mainBay: SeatMapItem[] = [];

      if (isChair) {
        mainBay = slice.slice(0, Math.min(3, slice.length));
        sideBay = slice.slice(Math.min(3, slice.length));
      } else if (is1A) {
        mainBay = slice;
        sideBay = [];
      } else if (is2A) {
        const main = slice.filter(s => s.berthType !== 'SIDE_LOWER' && s.berthType !== 'SIDE_UPPER');
        sideBay = slice.filter(s => s.berthType === 'SIDE_LOWER' || s.berthType === 'SIDE_UPPER');
        if (main.length >= 4) {
          mainBay = [main[0], main[2], main[1], main[3]];
        } else {
          mainBay = main;
        }
      } else {
        // Sleeper / 3A (8 berths per bay: 6 in main cabin, 2 on side)
        const main = slice.filter(s => s.berthType !== 'SIDE_LOWER' && s.berthType !== 'SIDE_UPPER');
        sideBay = slice.filter(s => s.berthType === 'SIDE_LOWER' || s.berthType === 'SIDE_UPPER');
        if (main.length >= 6) {
          mainBay = [main[0], main[3], main[1], main[4], main[2], main[5]];
        } else {
          mainBay = main;
        }
      }

      bays.push({
        bayNumber: Math.floor(i / baySize) + 1,
        mainBay: mainBay.length > 0 ? mainBay : slice.slice(0, Math.ceil(slice.length * 0.75)),
        sideBay: sideBay.length > 0 ? sideBay : slice.slice(Math.ceil(slice.length * 0.75))
      });
    }
    return bays;
  }

  isWindowSeat(seat: SeatMapItem): boolean {
    return seat.berthType === 'LOWER' || seat.berthType === 'SIDE_LOWER' || seat.seatName.endsWith('1') || seat.seatName.endsWith('7');
  }

  isSeatSelected(seat: SeatMapItem): boolean {
    return this.selectedSeats.some(s => s.seatName === seat.seatName);
  }

  toggleSeat(seat: SeatMapItem): void {
    const idx = this.selectedSeats.findIndex(s => s.seatName === seat.seatName);
    if (idx >= 0) {
      this.selectedSeats.splice(idx, 1);
      this.cdr.markForCheck();
      return;
    }

    if (seat.status !== 'AVAILABLE') {
      this.toast.error(`Seat ${seat.seatNumber} is booked and locked.`);
      return;
    }

    const tatkalOrOpen = this.isTatkalOpen() || !!seat.openToAll;

    // Quota access rules: if Tatkal is not open, reserve special quota seats for matching passengers
    if (!tatkalOrOpen && seat.quotaType && seat.quotaType !== 'GENERAL') {
      if (seat.quotaType === 'TATKAL') {
        this.toast.warning('Tatkal Quota is locked for normal booking. Tatkal booking opens 24 hours prior to train departure.');
        return;
      }
      if (seat.quotaType === 'LADIES') {
        const isFemale = (this.userGender === 'FEMALE') || (this.quota === 'LADIES');
        if (!isFemale) {
          this.toast.error('This seat is reserved for Ladies quota and cannot be booked by a male passenger before Tatkal opens.');
          return;
        }
      }
      if (seat.quotaType === 'DISABILITY' && this.quota !== 'DISABILITY' && this.quota !== 'SENIOR_CITIZEN') {
        this.toast.info('Selected Disability Quota seat (Lower Berth).');
      }
    }

    if (this.selectedSeats.length >= 6) {
      this.toast.warning('Maximum 6 passengers allowed per reservation.');
      return;
    }
    this.selectedSeats.push(seat);
    this.cdr.markForCheck();
  }

  get seatCount(): number {
    return this.selectedSeats.length || 1;
  }

  get baseTrainCost(): number {
    return Math.round(this.seatCount * this.unitFare);
  }

  get reservationCharges(): number {
    return Math.round(this.seatCount * 50);
  }

  get gstTax(): number {
    return Math.round(this.baseTrainCost * 0.05);
  }

  get totalEstimatedFare(): number {
    return this.baseTrainCost + this.reservationCharges + this.gstTax;
  }

  proceedToPassengerDetails(): void {
    this.bookingState.setSelectedSeats(this.selectedSeats);
    this.router.navigate(['/booking'], {
      queryParams: {
        trainId: this.trainId,
        journeyDate: this.journeyDate,
        classType: this.classType,
        quota: this.quota,
        source: this.fromStation,
        destination: this.toStation
      }
    });
  }

  proceedWithoutSpecificSeat(): void {
    this.bookingState.setSelectedSeats([]);
    this.router.navigate(['/booking'], {
      queryParams: {
        trainId: this.trainId,
        journeyDate: this.journeyDate,
        classType: this.classType,
        quota: this.quota,
        source: this.fromStation,
        destination: this.toStation
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/search'], {
      queryParams: {
        journeyDate: this.journeyDate,
        classType: this.classType,
        quota: this.quota
      }
    });
  }
}
