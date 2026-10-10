import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { finalize, Subscription, timer } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { BookingStateService } from '../../core/services/booking-state.service';
import { CustomerService } from '../../core/services/customer.service';
import { FoodService } from '../../core/services/food.service';
import { ScheduleFareService } from '../../core/services/schedule-fare.service';
import { ReservationService } from '../../core/services/reservation.service';
import { InventoryService } from '../../core/services/inventory.service';
import { ToastService } from '../../core/services/toast.service';
import { FoodMenuItem } from '../../core/models/food.models';
import { FareCalculationResponse } from '../../core/models/schedule-fare.models';
import { SeatLayout, SeatMapItem, Coach } from '../../core/models/inventory.models';
import { ReservationRequest } from '../../core/models/reservation.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { PkrCurrencyPipe } from '../../shared/pipes/currency.pipe';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, TranslatePipe, PkrCurrencyPipe],
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.css'
})
export class BookingComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private bookingState = inject(BookingStateService);
  private customerService = inject(CustomerService);
  private foodService = inject(FoodService);
  private scheduleFareService = inject(ScheduleFareService);
  private reservationService = inject(ReservationService);
  private inventoryService = inject(InventoryService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  step = 1;
  trainId = 1;
  sourceStationCode = 'KHI';
  destinationStationCode = 'LHE';
  journeyDate = '';
  classType = 'SL';
  quota = 'GENERAL';
  distanceKm = 1286.0;

  // Seat Selection State (Step 2)
  layout: SeatLayout | null = null;
  loadingSeats = false;
  loadingCoaches = false;
  trainCoaches: Coach[] = [];
  coaches: string[] = [];
  activeCoach = 'S1';
  activePassengerIndex = 0;
  maleLadiesSeatWarning: string | null = null;
  private livePollingSub?: Subscription;

  ngOnDestroy(): void {
    this.stopDynamicSeatSync();
  }

  menuItems: FoodMenuItem[] = [];
  selectedMealQuantities: { [menuId: number]: number } = {};

  fareBreakdown: FareCalculationResponse = {
    trainId: 1,
    classType: 'SL',
    quota: 'GENERAL',
    distanceKm: 1286.0,
    passengerCount: 1,
    tatkalSurcharge: 0,
    baseFare: 891.0,
    concessionDiscount: 0.0,
    reservationFee: 50.0,
    gstTax: 45.0,
    finalPayableFare: 986.0
  };

  selectedPaymentMethod = 'CREDIT_CARD';
  bookingInProgress = false;

  concessionVerificationState: {
    [index: number]: {
      loading: boolean;
      verified: boolean;
      eligible: boolean;
      message: string;
      discountAmount?: number;
    }
  } = {};

  passengersForm: FormGroup = this.fb.group({
    passengers: this.fb.array([])
  });

  get passengersArray(): FormArray {
    return this.passengersForm.get('passengers') as FormArray;
  }

  ngOnInit(): void {
    const ctx = this.bookingState.journeyContext();
    if (ctx) {
      this.sourceStationCode = ctx.sourceStationCode;
      this.destinationStationCode = ctx.destinationStationCode;
      this.journeyDate = ctx.journeyDate;
      this.classType = ctx.classType;
      this.quota = ctx.quota;
      this.distanceKm = ctx.distanceKm || 1286.0;
    }

    this.route.queryParams.subscribe((params) => {
      if (params['trainId']) this.trainId = Number(params['trainId']);
      if (params['journeyDate']) this.journeyDate = params['journeyDate'];
      if (params['classType']) this.classType = params['classType'];
      if (params['quota']) this.quota = params['quota'];
      if (params['source']) this.sourceStationCode = params['source'];
      if (params['destination']) this.destinationStationCode = params['destination'];
      if (params['selectSeats'] === 'true') {
        this.step = 2;
      }
    });

    if (!this.classType || this.classType === 'ALL') {
      this.classType = 'SL';
    }

    if (!this.journeyDate) {
      this.journeyDate = new Date().toISOString().split('T')[0];
    }

    // Initialize passengers from selected seats if available
    const selectedSeats = this.bookingState.selectedSeats();
    if (selectedSeats && selectedSeats.length > 0) {
      selectedSeats.forEach(seat => {
        this.passengersArray.push(this.createPassengerGroup('', seat.berthType as any, seat.seatNumber, seat.coach));
      });
    } else {
      this.passengersArray.push(this.createPassengerGroup());
    }

    // Auto-fill first passenger from logged-in customer profile
    if (this.authService.isAuthenticated()) {
      this.customerService.getProfile().subscribe({
        next: (profile) => {
          if (profile && this.passengersArray.length > 0) {
            const first = this.passengersArray.at(0);
            if (!first.get('name')?.value) {
              first.patchValue({
                name: profile.fullName,
                gender: profile.gender || 'MALE',
                address: profile.address || ''
              });
              this.cdr.markForCheck();
            }
          }
        },
        error: () => {}
      });
    }

    // Load Food Menu
    this.foodService.getFoodMenu().subscribe({
      next: (menu) => {
        this.menuItems = menu;
        this.cdr.markForCheck();
      },
      error: () => {
        this.menuItems = [
          { id: 1, itemName: 'Special Chicken Biryani Box', description: 'Fragrant Basmati Rice, Chicken, Raita, Salad', category: 'DINNER', price: 250, available: true },
          { id: 2, itemName: 'Continental Breakfast Set', description: 'Boiled Eggs, 2 Toast, Butter, Jam, Tea', category: 'BREAKFAST', price: 180, available: true },
          { id: 3, itemName: 'Traditional Dal Chawal Box', description: 'Basmati Rice, Moong/Masoor Dal, Pickle, Salad', category: 'LUNCH', price: 160, available: true },
          { id: 4, itemName: 'Karak Doodh Patti Chai', description: 'Freshly brewed hot railway tea (200ml)', category: 'BEVERAGES', price: 40, available: true }
        ];
        this.cdr.markForCheck();
      }
    });

    this.recalculateFare();
    this.loadTrainCoaches();
  }

  createPassengerGroup(name = '', berthPref = 'NO_PREFERENCE', seatNumber = '', coach = '', gender = 'MALE', age = 25): FormGroup {
    return this.fb.group({
      name: [name, [Validators.required, Validators.minLength(2)]],
      age: [age, [Validators.required, Validators.min(1), Validators.max(120)]],
      gender: [gender, Validators.required],
      address: ['Shahrah-e-Quaid-e-Azam, Lahore'],
      seatPreference: [berthPref],
      seatNumber: [seatNumber],
      coachNumber: [coach],
      concessionType: [null],
      concessionNumber: [''],
      ladiesQuotaPreference: [true]
    });
  }

  addPassenger(name = '', berthPref = 'NO_PREFERENCE', seatNumber = '', coach = '', gender = 'MALE', age = 25): void {
    if (this.passengersArray.length < 6) {
      this.passengersArray.push(this.createPassengerGroup(name, berthPref, seatNumber, coach, gender, age));
      this.activePassengerIndex = this.passengersArray.length - 1;
      this.recalculateFare();
      this.toast.info(`Passenger #${this.passengersArray.length} added. Total passengers: ${this.passengersArray.length}`);
      this.cdr.markForCheck();
    } else {
      this.toast.warning('Maximum 6 passengers allowed per booking.');
    }
  }

  removePassenger(index: number): void {
    if (this.passengersArray.length > 1) {
      this.passengersArray.removeAt(index);
      delete this.concessionVerificationState[index];
      if (this.activePassengerIndex >= this.passengersArray.length) {
        this.activePassengerIndex = this.passengersArray.length - 1;
      }
      this.syncSelectedSeatsToState();
      this.validatePassengerSeatQuotas();
      this.recalculateFare();
      this.toast.info(`Passenger #${index + 1} removed.`);
      this.cdr.markForCheck();
    } else {
      this.toast.warning('At least 1 passenger is required.');
    }
  }

  togglePassengerGender(index: number, event?: MouseEvent): void {
    if (event) event.stopPropagation();
    if (index >= 0 && index < this.passengersArray.length) {
      const p = this.passengersArray.at(index);
      const current = p.get('gender')?.value || 'MALE';
      const next = current === 'MALE' ? 'FEMALE' : 'MALE';
      p.patchValue({ gender: next });
      this.validatePassengerSeatQuotas();
      this.toast.info(`Passenger #${index + 1} gender set to ${next}.`);
      this.cdr.markForCheck();
    }
  }

  onConcessionTypeChange(index: number): void {
    const pGroup = this.passengersArray.at(index);
    if (!pGroup.get('concessionType')?.value) {
      pGroup.patchValue({ concessionNumber: '' });
      delete this.concessionVerificationState[index];
      this.recalculateFare();
    }
  }

  verifyConcession(index: number): void {
    const pGroup = this.passengersArray.at(index);
    const type = pGroup.get('concessionType')?.value;
    const num = (pGroup.get('concessionNumber')?.value || '').trim();

    if (!type || !num) {
      delete this.concessionVerificationState[index];
      this.recalculateFare();
      return;
    }

    this.concessionVerificationState[index] = {
      loading: true,
      verified: false,
      eligible: false,
      message: 'Verifying concession ID with railway database...'
    };
    this.cdr.markForCheck();

    this.customerService.verifyConcession({
      concessionType: type,
      concessionNumber: num
    }).subscribe({
      next: (res) => {
        const data = res.data;
        if (data && data.eligible) {
          this.concessionVerificationState[index] = {
            loading: false,
            verified: true,
            eligible: true,
            message: data.statusMessage || 'Concession verified! Discount applied.'
          };
          this.toast.success(`Concession Verified for Passenger ${index + 1}!`);
        } else {
          this.concessionVerificationState[index] = {
            loading: false,
            verified: true,
            eligible: false,
            message: data?.statusMessage || 'Invalid Concession ID! Full charge applies.'
          };
          this.toast.warning(`Passenger ${index + 1}: Invalid Concession ID. Full fare applies.`);
        }
        this.recalculateFare();
        this.cdr.markForCheck();
      },
      error: () => {
        this.concessionVerificationState[index] = {
          loading: false,
          verified: true,
          eligible: false,
          message: 'Invalid Concession ID! Full charge applies.'
        };
        this.toast.warning(`Passenger ${index + 1}: Concession ID not found. Full fare applies.`);
        this.recalculateFare();
        this.cdr.markForCheck();
      }
    });
  }

  recalculateFare(): void {
    this.scheduleFareService.calculateFare({
      trainId: this.trainId,
      sourceStationCode: this.sourceStationCode,
      destinationStationCode: this.destinationStationCode,
      classType: this.classType,
      quota: this.quota,
      passengerCount: this.passengersArray.length,
      distanceKm: this.distanceKm
    }).subscribe({
      next: (res) => {
        this.fareBreakdown = { ...res.data };
        this.applyVerifiedConcessions();
        this.cdr.markForCheck();
      },
      error: () => {
        const paxCount = this.passengersArray.length;
        const base = 891.0 * paxCount;
        const resFee = 50.0 * paxCount;
        const gst = Math.round(base * 0.05);
        this.fareBreakdown = {
          trainId: this.trainId,
          classType: this.classType,
          quota: this.quota,
          distanceKm: this.distanceKm,
          passengerCount: paxCount,
          tatkalSurcharge: 0,
          baseFare: base,
          concessionDiscount: 0,
          reservationFee: resFee,
          gstTax: gst,
          finalPayableFare: base + resFee + gst
        };
        this.applyVerifiedConcessions();
        this.cdr.markForCheck();
      }
    });
  }

  private applyVerifiedConcessions(): void {
    if (!this.fareBreakdown) return;
    const paxCount = this.passengersArray.length || 1;
    const singleBase = (this.fareBreakdown as any).singleBaseFare || ((this.fareBreakdown.baseFare || 0) / paxCount);
    let totalDiscount = 0;

    for (let i = 0; i < this.passengersArray.length; i++) {
      const state = this.concessionVerificationState[i];
      if (state && state.eligible) {
        // 40% discount on that passenger's base fare
        const passengerDiscount = Math.round(singleBase * 0.40);
        state.discountAmount = passengerDiscount;
        totalDiscount += passengerDiscount;
      }
    }

    this.fareBreakdown.concessionDiscount = totalDiscount;
    this.fareBreakdown.reservationFee = 50.0 * paxCount;
    const taxableBase = Math.max(0, (this.fareBreakdown.baseFare || 0) - totalDiscount);
    const gst = Math.round(taxableBase * 0.05);
    this.fareBreakdown.gstTax = gst;
    const gross = taxableBase + (this.fareBreakdown.reservationFee || 0) + (this.fareBreakdown.tatkalSurcharge || 0) + gst;
    this.fareBreakdown.finalPayableFare = gross;
  }

  getMealQty(menuId: number): number {
    return this.selectedMealQuantities[menuId] || 0;
  }

  updateMealQty(item: FoodMenuItem, delta: number): void {
    const current = this.getMealQty(item.id);
    const updated = Math.max(0, current + delta);
    if (updated === 0) {
      delete this.selectedMealQuantities[item.id];
    } else {
      this.selectedMealQuantities[item.id] = updated;
    }
    this.cdr.markForCheck();
  }

  get selectedMealsCount(): number {
    return Object.values(this.selectedMealQuantities).reduce((acc, q) => acc + q, 0);
  }

  get mealsTotal(): number {
    let total = 0;
    for (let idStr in this.selectedMealQuantities) {
      const id = Number(idStr);
      const qty = this.selectedMealQuantities[id];
      const item = this.menuItems.find(m => m.id === id);
      if (item) total += item.price * qty;
    }
    return total;
  }

  get totalPayable(): number {
    return (this.fareBreakdown.finalPayableFare || 0) + this.mealsTotal;
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

  // ----------------------------------------------------
  // SEAT SELECTION & COACH CARRIAGE METHODS (STEP 2)
  // ----------------------------------------------------

  getCoachListForClass(classType: string): string[] {
    switch (classType) {
      case '1A': return ['H1', 'H2'];
      case '2A': return ['A1', 'A2', 'A3'];
      case '3A': return ['B1', 'B2', 'B3', 'B4'];
      case 'CC': return ['C1', 'C2', 'C3'];
      case '2S': return ['D1', 'D2', 'D3', 'D4'];
      default:   return ['S1', 'S2', 'S3', 'S4', 'S5', 'S6'];
    }
  }

  scrollCarriage(delta: number): void {
    const el = document.getElementById('carriageScrollContainer');
    if (el) {
      el.scrollBy({ left: delta, behavior: 'smooth' });
    }
  }

  onCoachDropdownChange(coachNum: string): void {
    const coach = this.trainCoaches.find(c => c.coachNumber === coachNum);
    if (coach) {
      this.selectCoachOption(coach);
    }
  }

  loadTrainCoaches(): void {
    this.loadingCoaches = true;
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
          this.recalculateFare();
          this.loadSeatLayout();
        } else {
          this.trainCoaches = [];
          this.coaches = [];
          this.layout = null;
          this.cdr.markForCheck();
        }
      },
      error: () => {
        this.loadingCoaches = false;
        this.trainCoaches = [];
        this.coaches = [];
        this.layout = null;
        this.cdr.markForCheck();
      }
    });
  }

  loadSeatLayout(): void {
    if (!this.trainCoaches || this.trainCoaches.length === 0) {
      this.layout = null;
      this.loadingSeats = false;
      this.cdr.markForCheck();
      return;
    }
    this.loadingSeats = true;
    this.cdr.markForCheck();

    this.inventoryService.getSeatLayout(
      this.trainId,
      this.journeyDate,
      this.classType,
      this.sourceStationCode,
      this.destinationStationCode
    ).pipe(
      finalize(() => {
        this.loadingSeats = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (layout) => {
        if (layout && layout.seats && layout.seats.length > 0) {
          this.layout = layout;
          const coachSet = new Set<string>();
          layout.seats.forEach(s => {
            if (s.coach) coachSet.add(s.coach);
          });
          this.coaches = Array.from(coachSet).sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));
          if (this.coaches.length > 0 && !this.coaches.includes(this.activeCoach)) {
            this.activeCoach = this.coaches[0];
          }
        } else {
          this.layout = {
            trainId: this.trainId,
            trainNumber: 'TR-' + this.trainId,
            journeyDate: this.journeyDate,
            seats: []
          };
          this.coaches = [];
        }
        this.validatePassengerSeatQuotas();
        this.cdr.markForCheck();
      },
      error: () => {
        this.layout = null;
        this.coaches = [];
        this.validatePassengerSeatQuotas();
        this.cdr.markForCheck();
      }
    });
  }

  selectCoach(coach: string): void {
    this.activeCoach = coach;
    this.cdr.markForCheck();
  }

  selectCoachOption(coach: Coach): void {
    if (this.activeCoach === coach.coachNumber && this.classType === coach.classType) return;
    this.activeCoach = coach.coachNumber;
    if (coach.classType && coach.classType !== 'ALL') {
      this.classType = coach.classType;
    }
    for (let i = 0; i < this.passengersArray.length; i++) {
      this.passengersArray.at(i).patchValue({ seatNumber: '', coachNumber: '' });
    }
    this.recalculateFare();
    this.loadSeatLayout();
    this.cdr.markForCheck();
  }

  selectClassType(newClass: string): void {
    if (this.classType === newClass) return;
    this.classType = newClass;
    for (let i = 0; i < this.passengersArray.length; i++) {
      this.passengersArray.at(i).patchValue({ seatNumber: '', coachNumber: '' });
    }
    this.recalculateFare();
    this.loadSeatLayout();
  }

  get isChair(): boolean {
    return this.classType === 'CC' || this.classType === '2S';
  }

  get is1A(): boolean {
    return this.classType === '1A';
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
    const isChair = this.isChair;
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
        mainBay = slice.filter(s => s.berthType !== 'SIDE_LOWER' && s.berthType !== 'SIDE_UPPER');
        sideBay = slice.filter(s => s.berthType === 'SIDE_LOWER' || s.berthType === 'SIDE_UPPER');
      } else {
        mainBay = slice.filter(s => s.berthType !== 'SIDE_LOWER' && s.berthType !== 'SIDE_UPPER');
        sideBay = slice.filter(s => s.berthType === 'SIDE_LOWER' || s.berthType === 'SIDE_UPPER');
      }

      bays.push({
        bayNumber: Math.floor(i / baySize) + 1,
        mainBay: mainBay.length > 0 ? mainBay : slice.slice(0, Math.ceil(slice.length * 0.75)),
        sideBay: sideBay
      });
    }
    return bays;
  }

  get activeCoachStats(): { available: number; booked: number; selected: number } {
    const seats = this.activeCoachSeats;
    let avail = 0;
    let booked = 0;
    let selected = 0;

    seats.forEach(s => {
      const isAssigned = this.isSeatAssignedToAnyPassenger(s).assigned;
      if (isAssigned) {
        selected++;
      } else if (s.status === 'AVAILABLE') {
        avail++;
      } else {
        booked++;
      }
    });

    return { available: avail, booked: booked, selected: selected };
  }

  isWindowSeat(seat: SeatMapItem): boolean {
    return seat.berthType === 'LOWER' || seat.berthType === 'SIDE_LOWER' || seat.seatName.endsWith('1') || seat.seatName.endsWith('7');
  }

  setActivePassenger(idx: number): void {
    if (idx >= 0 && idx < this.passengersArray.length) {
      this.activePassengerIndex = idx;
      this.cdr.markForCheck();
    }
  }

  isSeatAssignedToCurrentPassenger(seat: SeatMapItem): boolean {
    if (this.activePassengerIndex < 0 || this.activePassengerIndex >= this.passengersArray.length) return false;
    const pax = this.passengersArray.at(this.activePassengerIndex).value;
    return pax.seatNumber === seat.seatNumber && pax.coachNumber === seat.coach;
  }

  isSeatAssignedToAnyPassenger(seat: SeatMapItem): { assigned: boolean; passengerIndex?: number; passengerName?: string; isMale?: boolean } {
    for (let i = 0; i < this.passengersArray.length; i++) {
      const p = this.passengersArray.at(i).value;
      if (p.seatNumber === seat.seatNumber && (!p.coachNumber || p.coachNumber === seat.coach)) {
        return {
          assigned: true,
          passengerIndex: i,
          passengerName: p.name || `Passenger #${i + 1}`,
          isMale: p.gender === 'MALE'
        };
      }
    }
    return { assigned: false };
  }

  assignSeatToActivePassenger(seat: SeatMapItem): void {
    if (seat.status !== 'AVAILABLE') {
      this.toast.error(`Seat ${seat.seatNumber} is booked and locked. Please select an available seat.`);
      return;
    }

    const tatkalOpen = this.isTatkalOpen() || !!seat.openToAll;

    // Check if the seat is already assigned to any passenger in this booking
    const currentAssignment = this.isSeatAssignedToAnyPassenger(seat);
    if (currentAssignment.assigned && currentAssignment.passengerIndex !== undefined) {
      const assignedIndex = currentAssignment.passengerIndex;
      // If clicking on the seat already assigned to active passenger, unassign it
      if (assignedIndex === this.activePassengerIndex) {
        this.passengersArray.at(assignedIndex).patchValue({ seatNumber: '', coachNumber: '' });
        this.toast.info(`Seat ${seat.seatNumber} unassigned.`);
        this.syncSelectedSeatsToState();
        this.validatePassengerSeatQuotas();
        this.cdr.markForCheck();
        return;
      } else {
        this.toast.warning(`Seat ${seat.seatNumber} is already selected for Passenger #${assignedIndex + 1}.`);
        return;
      }
    }

    // Determine target passenger:
    // 1. If active passenger has no seat, use activePassengerIndex.
    // 2. Else if any other existing passenger has no seat, use that passenger's index.
    // 3. Else if all existing passengers have seats and passengersArray.length < 6, automatically add a new passenger!
    // 4. Else if passengersArray.length >= 6 and active passenger already has a seat, reassign active passenger.
    let targetIndex = this.activePassengerIndex;
    const activePax = this.passengersArray.at(this.activePassengerIndex)?.value;

    if (activePax && activePax.seatNumber) {
      // Find unassigned passenger
      const unassignedIdx = this.passengersArray.controls.findIndex(c => !c.value.seatNumber);
      if (unassignedIdx !== -1) {
        targetIndex = unassignedIdx;
        this.activePassengerIndex = unassignedIdx;
      } else if (this.passengersArray.length < 6) {
        // Auto-add next passenger
        this.addPassenger();
        targetIndex = this.passengersArray.length - 1;
        this.activePassengerIndex = targetIndex;
      }
    }

    if (targetIndex < 0 || targetIndex >= this.passengersArray.length) {
      targetIndex = 0;
      this.activePassengerIndex = 0;
    }

    const paxControl = this.passengersArray.at(targetIndex);
    const pax = paxControl.value;
    const paxName = pax.name || `Passenger #${targetIndex + 1}`;
    const gender = (pax.gender || 'MALE').toUpperCase().trim();
    const isMale = gender === 'MALE';

    // 1. Quota Check: Tatkal locked during normal hours
    if (!tatkalOpen && seat.quotaType === 'TATKAL') {
      this.toast.warning('Tatkal Quota is locked for normal booking. Tatkal opens 24 hours prior to departure.');
      return;
    }

    // 2. Quota Check: Women Quota Seat Rules
    if (!tatkalOpen && seat.quotaType === 'LADIES') {
      if (isMale) {
        this.maleLadiesSeatWarning = 'This seat is reserved for Ladies quota and cannot be booked by a male passenger before Tatkal opens.';
        this.toast.error(this.maleLadiesSeatWarning);
        return;
      }
    }

    // Assign seat to target passenger
    paxControl.patchValue({
      seatNumber: seat.seatNumber,
      coachNumber: seat.coach,
      seatPreference: seat.berthType
    });

    this.toast.success(`Seat ${seat.seatNumber} (${seat.berthType}) assigned to ${paxName}.`);
    this.maleLadiesSeatWarning = null;

    // Advance activePassengerIndex to next unassigned passenger if any
    const nextUnassigned = this.passengersArray.controls.findIndex(c => !c.value.seatNumber);
    if (nextUnassigned !== -1) {
      this.activePassengerIndex = nextUnassigned;
    }

    this.syncSelectedSeatsToState();
    this.validatePassengerSeatQuotas();
    this.recalculateFare();
    this.cdr.markForCheck();
  }

  removeAssignedSeat(index: number): void {
    if (index >= 0 && index < this.passengersArray.length) {
      this.passengersArray.at(index).patchValue({ seatNumber: '', coachNumber: '' });
      this.syncSelectedSeatsToState();
      this.validatePassengerSeatQuotas();
      this.cdr.markForCheck();
    }
  }

  private syncSelectedSeatsToState(): void {
    const list: any[] = [];
    for (let i = 0; i < this.passengersArray.length; i++) {
      const p = this.passengersArray.at(i).value;
      if (p.seatNumber) {
        list.push({
          seatNumber: p.seatNumber,
          coach: p.coachNumber || this.activeCoach,
          berthType: p.seatPreference || 'LOWER',
          classType: this.classType
        });
      }
    }
    this.bookingState.setSelectedSeats(list);
  }

  validatePassengerSeatQuotas(): boolean {
    const tatkalOpen = this.isTatkalOpen();
    const passengers = this.passengersArray.value;
    this.maleLadiesSeatWarning = null;

    for (let i = 0; i < passengers.length; i++) {
      const p = passengers[i];
      if (p.seatNumber) {
        const seatItem = this.layout?.seats?.find(s => s.seatNumber === p.seatNumber && (!p.coachNumber || s.coach === p.coachNumber));
        if (seatItem && seatItem.quotaType === 'LADIES' && !seatItem.openToAll && !tatkalOpen) {
          if (p.gender === 'MALE') {
            this.maleLadiesSeatWarning = 'This seat is reserved for Ladies quota and cannot be booked by a male passenger before Tatkal opens.';
            return false;
          }
        }
      }
    }
    return true;
  }

  onJourneyDateChange(newDate: string): void {
    if (!newDate || newDate === this.journeyDate) return;
    this.journeyDate = newDate;
    for (let i = 0; i < this.passengersArray.length; i++) {
      this.passengersArray.at(i).patchValue({ seatNumber: '', coachNumber: '' });
    }
    this.bookingState.setSelectedSeats([]);
    this.maleLadiesSeatWarning = null;
    this.recalculateFare();
    if (this.step === 2) {
      this.loadSeatLayout();
    }
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { journeyDate: this.journeyDate },
      queryParamsHandling: 'merge'
    });
    this.cdr.markForCheck();
  }

  validateGenderForLadiesQuota(): boolean {
    return this.validatePassengerSeatQuotas();
  }

  goToStep2(): void {
    if (this.passengersForm.invalid) {
      this.passengersForm.markAllAsTouched();
      this.toast.warning('Please enter valid passenger details for all travelers before proceeding to seat selection.');
      return;
    }
    this.step = 2;
    this.activePassengerIndex = 0;
    if (!this.trainCoaches || this.trainCoaches.length === 0) {
      this.loadTrainCoaches();
    } else {
      this.loadSeatLayout();
    }
    this.startDynamicSeatSync();
    this.cdr.markForCheck();
  }

  startDynamicSeatSync(): void {
    this.stopDynamicSeatSync();
    // Dynamically poll inventory every 8 seconds to lock newly booked seats in real time
    this.livePollingSub = timer(8000, 8000).subscribe(() => {
      if (this.step === 2 && !this.loadingSeats) {
        this.inventoryService.getSeatLayout(
          this.trainId,
          this.journeyDate,
          this.classType,
          this.sourceStationCode,
          this.destinationStationCode
        ).subscribe({
          next: (freshLayout) => {
            if (freshLayout && freshLayout.seats && this.layout && this.layout.seats) {
              let updatedAny = false;
              freshLayout.seats.forEach(freshSeat => {
                const existing = this.layout!.seats.find(s => s.seatNumber === freshSeat.seatNumber && s.coach === freshSeat.coach);
                if (existing && existing.status !== freshSeat.status) {
                  existing.status = freshSeat.status;
                  updatedAny = true;
                  if (freshSeat.status !== 'AVAILABLE') {
                    for (let i = 0; i < this.passengersArray.length; i++) {
                      const p = this.passengersArray.at(i);
                      if (p.get('seatNumber')?.value === freshSeat.seatNumber && (p.get('coachNumber')?.value || this.activeCoach) === freshSeat.coach) {
                        p.patchValue({ seatNumber: '', coachNumber: '' });
                        this.toast.warning(`Seat ${freshSeat.seatNumber} was booked by another passenger and is now dynamically locked in red.`);
                      }
                    }
                  }
                }
              });
              if (updatedAny) {
                this.syncSelectedSeatsToState();
                this.validatePassengerSeatQuotas();
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

  goToStep3(): void {
    this.stopDynamicSeatSync();
    if (!this.validatePassengerSeatQuotas()) {
      if (this.maleLadiesSeatWarning) {
        this.toast.error(this.maleLadiesSeatWarning);
      }
      return;
    }
    if (this.passengersForm.invalid) {
      this.step = 1;
      this.passengersForm.markAllAsTouched();
      this.toast.warning('Please enter passenger names and details before proceeding.');
      this.cdr.markForCheck();
      return;
    }
    this.step = 3;
    this.cdr.markForCheck();
  }

  skipSeatSelection(): void {
    this.stopDynamicSeatSync();
    for (let i = 0; i < this.passengersArray.length; i++) {
      this.passengersArray.at(i).patchValue({ seatNumber: '', coachNumber: '' });
    }
    this.bookingState.setSelectedSeats([]);
    this.maleLadiesSeatWarning = null;
    if (this.passengersForm.invalid) {
      this.step = 1;
      this.passengersForm.markAllAsTouched();
      this.toast.warning('Please enter passenger names and details before proceeding.');
      this.cdr.markForCheck();
      return;
    }
    this.step = 3;
    this.cdr.markForCheck();
  }

  goToStep4(): void {
    this.step = 4;
    this.cdr.markForCheck();
  }

  goBack(): void {
    this.stopDynamicSeatSync();
    if (this.step > 1) {
      this.step--;
      this.cdr.markForCheck();
      return;
    }
    this.router.navigate(['/search'], {
      queryParams: {
        journeyDate: this.journeyDate,
        classType: this.classType,
        quota: this.quota
      }
    });
  }

  confirmBooking(): void {
    if (!this.validatePassengerSeatQuotas()) {
      this.step = 2; // Jump back to seat selection
      if (this.maleLadiesSeatWarning) {
        this.toast.error(this.maleLadiesSeatWarning);
      }
      return;
    }

    if (this.passengersForm.invalid) {
      this.step = 1;
      this.passengersForm.markAllAsTouched();
      this.toast.warning('Please enter passenger names and details before confirming.');
      return;
    }

    this.bookingInProgress = true;
    this.cdr.markForCheck();

    const foodItems = Object.keys(this.selectedMealQuantities).map(idStr => ({
      foodMenuId: Number(idStr),
      quantity: this.selectedMealQuantities[Number(idStr)]
    }));

    // Normalize quota: If Tatkal is not open, default to GENERAL.
    // If quota was LADIES but there are male passengers or mixed seats, generalize to GENERAL.
    let effectiveQuota = this.quota || 'GENERAL';
    if (!this.isTatkalOpen() && effectiveQuota.toUpperCase() === 'TATKAL') {
      effectiveQuota = 'GENERAL';
    }
    const hasMalePax = this.passengersArray.value.some((p: any) => (p.gender || '').toUpperCase() === 'MALE');
    if (hasMalePax && effectiveQuota.toUpperCase() === 'LADIES') {
      effectiveQuota = 'GENERAL';
    }

    const request: ReservationRequest = {
      trainId: this.trainId,
      sourceStationCode: this.sourceStationCode,
      destinationStationCode: this.destinationStationCode,
      journeyDate: this.journeyDate,
      classType: this.classType,
      quota: effectiveQuota,
      passengers: this.passengersArray.value,
      foodItems: foodItems,
      paymentMethod: this.selectedPaymentMethod
    };

    this.reservationService.bookReservation(request)
      .pipe(
        finalize(() => {
          this.bookingInProgress = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (res) => {
          const reservation = res.data;
          this.toast.success(`Booking Confirmed! PNR: ${reservation.pnr}`);
          // Persist user-booked seats scoped per Train + Journey Date
          try {
            const key = `user_booked_${this.trainId}_${this.journeyDate}`;
            const currentBooked: string[] = JSON.parse(localStorage.getItem(key) || '[]');
            const newBooked = this.passengersArray.value.map((p: any) => p.seatNumber).filter(Boolean);
            const combined = Array.from(new Set([...currentBooked, ...newBooked]));
            localStorage.setItem(key, JSON.stringify(combined));
            localStorage.removeItem('user_booked_seat_numbers'); // Clean up old unisolated key
          } catch (e) {}
          this.bookingState.clearBookingContext();
          this.router.navigate(['/ticket', reservation.pnr]);
        },
        error: (err) => {
          const msg = err.error?.message || 'Booking failed. Please verify seat availability and passenger details.';
          this.toast.error(msg);
        }
      });
  }

  private generateDemoSeats(coaches: string[] = ['S1']): SeatMapItem[] {
    const list: SeatMapItem[] = [];
    const is1A = this.classType === '1A';
    const is2A = this.classType === '2A';
    const isCC = this.classType === 'CC' || this.classType === '2S';
    const seatsPerCoach = is1A ? 18 : (is2A ? 18 : (isCC ? 18 : 18));
    const berths = ['LOWER', 'MIDDLE', 'UPPER', 'LOWER', 'MIDDLE', 'UPPER', 'SIDE_LOWER', 'SIDE_UPPER'] as const;

    let userBookedSeats: string[] = [];
    try {
      const key = `user_booked_${this.trainId}_${this.journeyDate}`;
      userBookedSeats = JSON.parse(localStorage.getItem(key) || '[]');
    } catch (e) {}

    for (const c of coaches) {
      for (let i = 1; i <= seatsPerCoach; i++) {
        let bType: 'LOWER' | 'MIDDLE' | 'UPPER' | 'SIDE_LOWER' | 'SIDE_UPPER' = berths[(i - 1) % berths.length];
        if (is1A) {
          bType = i % 2 === 1 ? 'LOWER' : 'UPPER';
        } else if (is2A) {
          const mod = (i - 1) % 6;
          bType = mod === 0 || mod === 2 ? 'LOWER' : (mod === 1 || mod === 3 ? 'UPPER' : (mod === 4 ? 'SIDE_LOWER' : 'SIDE_UPPER'));
        }

        let qType = 'GENERAL';
        if (i <= 10) {
          qType = 'GENERAL';
        } else if (i <= 13) {
          qType = 'TATKAL';
        } else if (i <= 16) {
          qType = 'LADIES';
        } else {
          qType = 'DISABILITY';
          bType = 'LOWER';
        }

        const seatNum = `${c}-${i}`;
        const status = userBookedSeats.includes(seatNum) ? 'CONFIRMED' : 'AVAILABLE';

        const tatkalOpen = this.isTatkalOpen();
        const openToAll = tatkalOpen || qType === 'GENERAL';

        list.push({
          id: i,
          seatName: seatNum,
          seatNumber: seatNum,
          coach: c,
          status: status,
          berthType: bType,
          classType: this.classType || 'SL',
          quotaType: qType,
          openToAll: openToAll
        });
      }
    }
    return list;
  }
}
