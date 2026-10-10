import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { ReservationService } from '../../core/services/reservation.service';
import { TrainService } from '../../core/services/train.service';
import { ToastService } from '../../core/services/toast.service';
import { ReservationResponse, TrainDelayNotificationRequest } from '../../core/models/reservation.models';
import { Train } from '../../core/models/train.models';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { PkrCurrencyPipe } from '../../shared/pipes/currency.pipe';

@Component({
  selector: 'app-reservations-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule, ModalComponent, TranslatePipe, PkrCurrencyPipe],
  templateUrl: './reservations-admin.component.html',
  styleUrl: './reservations-admin.component.css'
})
export class ReservationsAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  private reservationService = inject(ReservationService);
  private trainService = inject(TrainService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  generatingPdf = false;
  submittingDelay = false;
  delayModalOpen = false;
  reservations: ReservationResponse[] = [];
  trains: Train[] = [];

  selectedTrainNumber: string = 'ALL';
  selectedJourneyDate: string = '';
  selectedStatusFilter: 'ALL' | 'CONFIRMED' | 'PARTIALLY_CONFIRMED' | 'WAITING_LIST' | 'CANCELLED' | 'WAITING_LIST_EXPIRED' | string = 'ALL';

  currentPage: number = 1;
  pageSize: number = 10;
  pageSizeOptions: number[] = [10, 25, 50];

  isPartiallyConfirmed(r: ReservationResponse): boolean {
    const s = (r.status || '').toUpperCase();
    if (s === 'PARTIALLY_CONFIRMED') return true;
    if (s === 'CANCELLED' || s === 'WAITING_LIST_EXPIRED' || s === 'FAILED' || s === 'PENDING') return false;
    if (r.passengers && r.passengers.length > 1) {
      const activePax = r.passengers.filter(p => (p.status || '').toUpperCase() !== 'CANCELLED');
      const hasCnf = activePax.some(p => (p.status || '').toUpperCase() === 'CNF' || (p.status || '').toUpperCase() === 'CONFIRMED' || !!p.seatNumber);
      const hasWl = activePax.some(p => (p.status || '').toUpperCase() === 'WL' || (p.status || '').toUpperCase() === 'WAITING_LIST' || (p.status || '').toUpperCase() === 'WAITLISTED' || (!p.seatNumber && !!p.waitingListCode));
      return hasCnf && hasWl;
    }
    return false;
  }

  isConfirmed(r: ReservationResponse): boolean {
    if (this.isPartiallyConfirmed(r)) return false;
    const s = (r.status || '').toUpperCase();
    return s === 'CONFIRMED';
  }

  isWaitingList(r: ReservationResponse): boolean {
    if (this.isPartiallyConfirmed(r)) return false;
    const s = (r.status || '').toUpperCase();
    return s === 'WAITING_LIST' || s === 'WAITLISTED';
  }

  get filteredReservations(): ReservationResponse[] {
    return this.reservations.filter(r => {
      if (this.selectedTrainNumber && this.selectedTrainNumber !== 'ALL') {
        const filter = this.selectedTrainNumber.trim().toUpperCase();
        const tNum = (r.trainNumber || '').toUpperCase();
        const tName = (r.trainName || '').toUpperCase();
        if (tNum !== filter && !tName.includes(filter)) {
          return false;
        }
      }
      if (this.selectedJourneyDate && this.selectedJourneyDate.trim() !== '') {
        const jDate = r.journeyDate ? r.journeyDate.toString() : '';
        if (jDate !== this.selectedJourneyDate.trim()) {
          return false;
        }
      }
      if (this.selectedStatusFilter && this.selectedStatusFilter !== 'ALL') {
        if (this.selectedStatusFilter === 'PARTIALLY_CONFIRMED') {
          if (!this.isPartiallyConfirmed(r)) return false;
        } else if (this.selectedStatusFilter === 'CONFIRMED') {
          if (!this.isConfirmed(r)) return false;
        } else if (this.selectedStatusFilter === 'WAITING_LIST') {
          if (!this.isWaitingList(r)) return false;
        } else {
          const status = (r.status || 'CONFIRMED').toUpperCase();
          if (status !== this.selectedStatusFilter.toUpperCase()) {
            return false;
          }
        }
      }
      return true;
    }).sort((a, b) => {
      const dateA = a.journeyDate ? a.journeyDate.toString() : '';
      const dateB = b.journeyDate ? b.journeyDate.toString() : '';
      if (dateA !== dateB) {
        return dateB.localeCompare(dateA);
      }
      const trainA = a.trainNumber || '';
      const trainB = b.trainNumber || '';
      if (trainA !== trainB) {
        return trainA.localeCompare(trainB);
      }
      return (b.id || 0) - (a.id || 0);
    });
  }

  get totalPages(): number {
    const total = this.filteredReservations.length;
    const size = Number(this.pageSize) || 10;
    return Math.max(1, Math.ceil(total / size));
  }

  get paginatedReservations(): ReservationResponse[] {
    const p = Number(this.currentPage) || 1;
    const size = Number(this.pageSize) || 10;
    const startIndex = (p - 1) * size;
    return this.filteredReservations.slice(startIndex, startIndex + size);
  }

  get paginationStartIndex(): number {
    if (this.filteredReservations.length === 0) return 0;
    const p = Number(this.currentPage) || 1;
    const size = Number(this.pageSize) || 10;
    return (p - 1) * size + 1;
  }

  get paginationEndIndex(): number {
    const p = Number(this.currentPage) || 1;
    const size = Number(this.pageSize) || 10;
    return Math.min(p * size, this.filteredReservations.length);
  }

  get pageNumbers(): number[] {
    const total = this.totalPages;
    const current = Number(this.currentPage) || 1;
    const pages: number[] = [];
    const maxVisible = 5;

    let start = Math.max(1, current - 2);
    let end = Math.min(total, start + maxVisible - 1);
    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  goToPage(page: number | string): void {
    const p = Number(page);
    if (!isNaN(p) && p >= 1 && p <= this.totalPages) {
      this.currentPage = p;
      this.cdr.detectChanges();
    }
  }

  nextPage(): void {
    const next = (Number(this.currentPage) || 1) + 1;
    if (next <= this.totalPages) {
      this.goToPage(next);
    }
  }

  prevPage(): void {
    const prev = (Number(this.currentPage) || 1) - 1;
    if (prev >= 1) {
      this.goToPage(prev);
    }
  }

  firstPage(): void {
    this.goToPage(1);
  }

  lastPage(): void {
    this.goToPage(this.totalPages);
  }

  setPageSize(size: number | string): void {
    this.pageSize = Number(size) || 10;
    this.currentPage = 1;
    this.cdr.detectChanges();
  }

  getPassengerCount(r: ReservationResponse): number {
    if (r.passengers && r.passengers.length > 0) {
      return r.passengers.length;
    }
    return r.passengerCount || 1;
  }

  getConfirmedPaxCount(r: ReservationResponse): number {
    if (!r.passengers || r.passengers.length === 0) {
      return this.isConfirmed(r) ? 1 : 0;
    }
    return r.passengers.filter(p => (p.status || '').toUpperCase() === 'CNF' || (p.status || '').toUpperCase() === 'CONFIRMED' || !!p.seatNumber).length;
  }

  getWaitlistPaxCount(r: ReservationResponse): number {
    if (!r.passengers || r.passengers.length === 0) {
      return this.isWaitingList(r) ? 1 : 0;
    }
    return r.passengers.filter(p => (p.status || '').toUpperCase() === 'WL' || (p.status || '').toUpperCase() === 'WAITING_LIST' || (p.status || '').toUpperCase() === 'WAITLISTED' || (!p.seatNumber && !!p.waitingListCode)).length;
  }

  get isFilterActive(): boolean {
    return this.selectedTrainNumber !== 'ALL' ||
           (!!this.selectedJourneyDate && this.selectedJourneyDate.trim() !== '') ||
           (this.selectedStatusFilter !== 'ALL');
  }

  get countAll(): number {
    return this.reservations.length;
  }

  get countConfirmed(): number {
    return this.reservations.filter(r => this.isConfirmed(r)).length;
  }

  get countPartiallyConfirmed(): number {
    return this.reservations.filter(r => this.isPartiallyConfirmed(r)).length;
  }

  get countWaitingList(): number {
    return this.reservations.filter(r => this.isWaitingList(r)).length;
  }

  get countCancelled(): number {
    return this.reservations.filter(r => (r.status || '').toUpperCase() === 'CANCELLED').length;
  }

  get countExpired(): number {
    return this.reservations.filter(r => (r.status || '').toUpperCase() === 'WAITING_LIST_EXPIRED').length;
  }

  preparingChart = false;

  prepareChart(): void {
    if (this.selectedTrainNumber === 'ALL') {
      this.toast.warning('Please select a specific train to prepare chart.');
      return;
    }
    const train = this.trains.find(t => t.trainNumber === this.selectedTrainNumber);
    if (!train) {
      this.toast.warning('Selected train not found.');
      return;
    }
    const date = this.selectedJourneyDate || new Date().toISOString().split('T')[0];

    this.preparingChart = true;
    this.cdr.markForCheck();

    this.reservationService.prepareChart(train.id!, date).pipe(
      finalize(() => {
        this.preparingChart = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        const data = res.data;
        this.toast.success(res.message || `Chart prepared! Promoted: ${data?.promotedCount || 0}, Expired: ${data?.expiredCount || 0}`);
        this.loadReservations();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to prepare chart.');
      }
    });
  }

  setTrainFilter(trainNumber: string): void {
    this.selectedTrainNumber = trainNumber;
    this.currentPage = 1;
    this.cdr.markForCheck();
  }

  setJourneyDateFilter(date: string): void {
    this.selectedJourneyDate = date;
    this.currentPage = 1;
    this.cdr.markForCheck();
  }

  setStatusFilter(status: string): void {
    this.selectedStatusFilter = status;
    this.currentPage = 1;
    this.cdr.markForCheck();
  }

  clearFilters(): void {
    this.selectedTrainNumber = 'ALL';
    this.selectedJourneyDate = '';
    this.selectedStatusFilter = 'ALL';
    this.currentPage = 1;
    this.cdr.markForCheck();
  }

  clearDateFilter(): void {
    this.selectedJourneyDate = '';
    this.currentPage = 1;
    this.cdr.markForCheck();
  }

  viewPdf(): void {
    this.generatingPdf = true;
    this.cdr.markForCheck();

    this.reservationService.downloadReservationsPdf(this.selectedTrainNumber, this.selectedJourneyDate, this.selectedStatusFilter).pipe(
      finalize(() => {
        this.generatingPdf = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (blob: Blob) => {
        const fileUrl = URL.createObjectURL(blob);
        window.open(fileUrl, '_blank');
        setTimeout(() => URL.revokeObjectURL(fileUrl), 60000);
      },
      error: () => {
        this.toast.error('Failed to generate reservations PDF report.');
      }
    });
  }

  downloadPdf(): void {
    this.generatingPdf = true;
    this.cdr.markForCheck();

    this.reservationService.downloadReservationsPdf(this.selectedTrainNumber, this.selectedJourneyDate, this.selectedStatusFilter).pipe(
      finalize(() => {
        this.generatingPdf = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (blob: Blob) => {
        const fileUrl = URL.createObjectURL(blob);
        const a = document.createElement('a');
        const todayStr = new Date().toISOString().slice(0, 10);
        const trainPart = this.selectedTrainNumber !== 'ALL' ? `-${this.selectedTrainNumber}` : '';
        const datePart = this.selectedJourneyDate ? `-${this.selectedJourneyDate}` : '';
        const statusPart = this.selectedStatusFilter !== 'ALL' ? `-${this.selectedStatusFilter}` : '';
        a.href = fileUrl;
        a.download = `Reservations-Report${trainPart}${datePart}${statusPart}-${todayStr}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        setTimeout(() => URL.revokeObjectURL(fileUrl), 10000);
        this.toast.success('Reservations PDF report downloaded successfully.');
      },
      error: () => {
        this.toast.error('Failed to download reservations PDF report.');
      }
    });
  }

  delayForm: FormGroup = this.fb.group({
    trainNumber: ['', Validators.required],
    journeyDate: [new Date().toISOString().split('T')[0], Validators.required],
    delayTime: ['1 hour 15 mins', Validators.required],
    reason: ['Operational track signal maintenance', Validators.required]
  });

  ngOnInit(): void {
    this.loadReservations();
    this.loadTrains();
  }

  loadTrains(): void {
    this.trainService.getAllTrains().subscribe({
      next: (res) => {
        this.trains = res.data || [];
        if (this.trains.length > 0 && !this.delayForm.value.trainNumber) {
          this.delayForm.patchValue({ trainNumber: this.trains[0].trainNumber });
        }
        this.cdr.markForCheck();
      },
      error: () => {}
    });
  }

  loadReservations(): void {
    this.loading = true;
    this.reservationService.getAllReservationsAdmin().pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        const data = res.data;
        this.reservations = Array.isArray(data) ? data : (data?.content || []);
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.error('Failed to load reservations.');
        this.cdr.markForCheck();
      }
    });
  }

  openDelayModal(r?: ReservationResponse): void {
    if (r) {
      this.delayForm.patchValue({
        trainNumber: r.trainNumber,
        journeyDate: r.journeyDate,
        delayTime: '1 hour 15 mins',
        reason: 'Operational track signal maintenance'
      });
    } else if (this.trains.length > 0) {
      this.delayForm.patchValue({
        trainNumber: this.trains[0].trainNumber,
        journeyDate: new Date().toISOString().split('T')[0]
      });
    }
    this.delayModalOpen = true;
    this.cdr.markForCheck();
  }

  onSubmitDelay(): void {
    if (this.delayForm.invalid) return;

    this.submittingDelay = true;
    const req: TrainDelayNotificationRequest = {
      trainNumber: this.delayForm.value.trainNumber.trim().toUpperCase(),
      journeyDate: this.delayForm.value.journeyDate,
      delayTime: this.delayForm.value.delayTime.trim(),
      reason: this.delayForm.value.reason.trim()
    };

    this.reservationService.broadcastTrainDelay(req).pipe(
      finalize(() => {
        this.submittingDelay = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.delayModalOpen = false;
        const count = res.data?.passengersNotified || 0;
        if (count > 0) {
          this.toast.success(`📢 Broadcast sent! ${count} passengers notified via email for Train ${req.trainNumber}.`);
        } else {
          this.toast.info(`Delay alert processed. No active bookings found for Train ${req.trainNumber} on ${req.journeyDate}.`);
        }
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to broadcast train delay.');
        this.cdr.markForCheck();
      }
    });
  }
}
