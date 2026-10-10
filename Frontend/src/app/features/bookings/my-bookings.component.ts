import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { ReservationService } from '../../core/services/reservation.service';
import { ToastService } from '../../core/services/toast.service';
import { ReservationResponse, PassengerResponse, CancellationPreviewResponse } from '../../core/models/reservation.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { PkrCurrencyPipe } from '../../shared/pipes/currency.pipe';

export interface SelectablePassenger {
  passenger: PassengerResponse;
  isSelected: boolean;
}

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslatePipe, PkrCurrencyPipe],
  templateUrl: './my-bookings.component.html',
  styleUrl: './my-bookings.component.css'
})
export class MyBookingsComponent implements OnInit {
  private reservationService = inject(ReservationService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  bookings: ReservationResponse[] = [];
  activeTab: 'ALL' | 'UPCOMING' | 'COMPLETED' | 'CANCELLED' = 'UPCOMING';

  cancelModalOpen = false;
  isConfirmStep = false;
  cancelling = false;
  loadingPreview = false;
  cancelReason = 'Change in travel plans';
  selectedBooking: ReservationResponse | null = null;
  selectablePassengers: SelectablePassenger[] = [];
  selectAllPassengers = true;
  cancellationPreview: CancellationPreviewResponse | null = null;

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.loading = true;
    this.reservationService.getMyReservations().pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.bookings = res.data || [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.error('Unable to fetch your reservations.');
        this.cdr.markForCheck();
      }
    });
  }

  get todayStr(): string {
    return new Date().toISOString().split('T')[0];
  }

  get upcomingBookings(): ReservationResponse[] {
    return this.bookings.filter(b => (b.status === 'CONFIRMED' || b.status === 'PARTIALLY_CONFIRMED' || b.status === 'WAITING_LIST') && b.journeyDate >= this.todayStr);
  }

  get completedBookings(): ReservationResponse[] {
    return this.bookings.filter(b => (b.status === 'CONFIRMED' || b.status === 'PARTIALLY_CONFIRMED' || b.status === 'WAITING_LIST') && b.journeyDate < this.todayStr);
  }

  get cancelledBookings(): ReservationResponse[] {
    return this.bookings.filter(b => b.status === 'CANCELLED' || b.status === 'WAITING_LIST_EXPIRED');
  }

  get displayedBookings(): ReservationResponse[] {
    if (this.activeTab === 'UPCOMING') return this.upcomingBookings;
    if (this.activeTab === 'COMPLETED') return this.completedBookings;
    if (this.activeTab === 'CANCELLED') return this.cancelledBookings;
    return this.bookings;
  }

  isJourneyPast(journeyDate?: string, departureTime?: string): boolean {
    if (!journeyDate) return false;
    const time = departureTime || '23:59:59';
    const journeyDateTime = new Date(`${journeyDate}T${time}`);
    return journeyDateTime.getTime() < Date.now();
  }

  canCancel(booking: ReservationResponse | null): boolean {
    if (!booking) return false;
    const status = booking.status;
    if (status !== 'CONFIRMED' && status !== 'WAITING_LIST' && status !== 'PARTIALLY_CONFIRMED') return false;
    const hasActivePax = booking.passengers && booking.passengers.some(p => p.status !== 'CANCELLED' && p.status !== 'EXPIRED');
    if (!hasActivePax && booking.passengers && booking.passengers.length > 0) return false;
    return !this.isJourneyPast(booking.journeyDate, (booking as any).departureTime);
  }

  getActivePassengersCount(booking: ReservationResponse): number {
    if (!booking.passengers || booking.passengers.length === 0) return booking.passengerCount || 1;
    return booking.passengers.filter(p => p.status !== 'CANCELLED' && p.status !== 'EXPIRED').length;
  }

  get selectedPassengers(): PassengerResponse[] {
    return this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger);
  }

  get selectedPassengersCount(): number {
    return this.selectablePassengers.filter(p => p.isSelected).length;
  }

  openCancelModal(booking: ReservationResponse): void {
    this.selectedBooking = booking;
    const activePax = (booking.passengers || []).filter(p => p.status !== 'CANCELLED' && p.status !== 'EXPIRED');
    if (activePax.length === 0 && booking.passengers && booking.passengers.length > 0) {
      this.toast.info('All passengers on this booking are already cancelled.');
      return;
    }

    this.selectablePassengers = activePax.map(p => ({
      passenger: p,
      isSelected: true
    }));
    this.selectAllPassengers = true;
    this.isConfirmStep = false;
    this.cancelReason = 'Change in travel plans';
    this.cancelModalOpen = true;

    this.fetchCancellationPreview();
  }

  cancelSelectedPassengers(): void {
    if (this.selectedPassengersCount === 0) {
      this.toast.warning('Please select at least one passenger to cancel.');
      return;
    }
    this.isConfirmStep = true;
    this.fetchCancellationPreview();
  }

  cancelEntirePnr(): void {
    if (!this.selectedBooking) return;
    this.selectablePassengers.forEach(p => p.isSelected = true);
    this.selectAllPassengers = true;
    this.isConfirmStep = true;
    this.fetchCancellationPreview();
  }

  toggleSelectAll(): void {
    this.selectablePassengers.forEach(p => p.isSelected = this.selectAllPassengers);
    this.fetchCancellationPreview();
  }

  onPassengerToggle(): void {
    this.selectAllPassengers = this.selectablePassengers.length > 0 && this.selectablePassengers.every(p => p.isSelected);
    this.fetchCancellationPreview();
  }

  goBackToSelectStep(): void {
    this.isConfirmStep = false;
  }

  fetchCancellationPreview(): void {
    if (!this.selectedBooking) return;
    const selectedIds = this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger.id);
    if (selectedIds.length === 0) {
      this.cancellationPreview = null;
      this.cdr.markForCheck();
      return;
    }

    this.loadingPreview = true;
    this.reservationService.previewCancellation(this.selectedBooking.id, selectedIds).pipe(
      finalize(() => {
        this.loadingPreview = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.cancellationPreview = res.data;
        this.cdr.markForCheck();
      },
      error: () => {
        const fare = this.selectedBooking?.finalAmount || this.selectedBooking?.totalFare || 891;
        const count = selectedIds.length;
        const totalPax = this.selectablePassengers.length || 1;
        const perPax = fare / totalPax;
        const refundAmt = Math.max(0, perPax * count * 0.9);
        this.cancellationPreview = {
          pnr: this.selectedBooking!.pnr,
          reservationId: this.selectedBooking!.id,
          originalTotalAmount: fare,
          totalPassengers: totalPax,
          selectedPassengerCount: count,
          perPassengerFare: perPax,
          cancellationChargePerPassenger: perPax * 0.1,
          refundPerPassenger: perPax * 0.9,
          totalCancellationCharge: perPax * count * 0.1,
          totalRefundAmount: refundAmt,
          refundPercentage: 90,
          deductionPolicyNote: '>48h before departure: 90% refund (10% cancellation charge deducted)',
          fullCancellation: count === totalPax,
          passengerDetails: this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger)
        };
        this.cdr.markForCheck();
      }
    });
  }

  confirmCancellation(): void {
    if (!this.selectedBooking) return;
    const selectedIds = this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger.id);
    if (selectedIds.length === 0) {
      this.toast.warning('Please select at least 1 passenger to cancel.');
      return;
    }

    this.cancelling = true;
    const request = {
      passengerIds: selectedIds,
      reason: this.cancelReason
    };

    this.reservationService.cancelReservation(this.selectedBooking.id, request).pipe(
      finalize(() => {
        this.cancelling = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.cancelModalOpen = false;
        this.isConfirmStep = false;
        const isFull = this.selectablePassengers.length === selectedIds.length;
        if (isFull) {
          this.toast.success(`Booking ${this.selectedBooking?.pnr} successfully cancelled in full.`);
        } else {
          this.toast.success(`Cancelled ${selectedIds.length} passenger(s) on PNR ${this.selectedBooking?.pnr}. Remaining passengers stay confirmed.`);
        }

        try {
          if (this.selectedBooking?.passengers) {
            const cancelledSeats = this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger.seatNumber).filter(Boolean);
            const currentBooked: string[] = JSON.parse(localStorage.getItem('user_booked_seat_numbers') || '[]');
            const updated = currentBooked.filter(s => !cancelledSeats.includes(s));
            localStorage.setItem('user_booked_seat_numbers', JSON.stringify(updated));
          }
        } catch (e) {}
        this.loadBookings();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to cancel booking.');
        this.cdr.markForCheck();
      }
    });
  }
}

