import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { TicketService } from '../../core/services/ticket.service';
import { ReservationService } from '../../core/services/reservation.service';
import { ToastService } from '../../core/services/toast.service';
import { TicketResponse, PassengerResponse, CancellationPreviewResponse } from '../../core/models/reservation.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
import { PkrCurrencyPipe } from '../../shared/pipes/currency.pipe';

export interface SelectablePassenger {
  passenger: PassengerResponse;
  isSelected: boolean;
}

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, TranslatePipe, PkrCurrencyPipe],
  templateUrl: './ticket-detail.component.html',
  styleUrl: './ticket-detail.component.css'
})
export class TicketDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private ticketService = inject(TicketService);
  private reservationService = inject(ReservationService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  pnr = '';
  loading = true;
  ticket: TicketResponse | null = null;

  cancelModalOpen = false;
  isConfirmStep = false;
  cancelling = false;
  loadingPreview = false;
  cancelReason = 'Change of travel plans';
  selectablePassengers: SelectablePassenger[] = [];
  selectAllPassengers = true;
  cancellationPreview: CancellationPreviewResponse | null = null;

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.pnr = params.get('pnr') || '';
      if (this.pnr) {
        this.loadTicket();
      }
    });
  }

  loadTicket(): void {
    this.loading = true;
    this.ticketService.getTicketByPnr(this.pnr).pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.ticket = res.data;
        this.initSelectablePassengers();
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.error(`Unable to load ticket details for PNR: ${this.pnr}`);
        this.cdr.markForCheck();
      }
    });
  }

  initSelectablePassengers(): void {
    if (!this.ticket || !this.ticket.passengers) {
      this.selectablePassengers = [];
      return;
    }
    const active = this.ticket.passengers.filter(p => p.status !== 'CANCELLED' && p.status !== 'EXPIRED');
    this.selectablePassengers = active.map(p => ({
      passenger: p,
      isSelected: true
    }));
    this.selectAllPassengers = true;
  }

  get activePassengers(): PassengerResponse[] {
    if (!this.ticket || !this.ticket.passengers) return [];
    return this.ticket.passengers.filter(p => p.status !== 'CANCELLED' && p.status !== 'EXPIRED');
  }

  getCoach(seatNumber?: string): string {
    if (!seatNumber) return 'S1';
    const parts = seatNumber.split('-');
    return parts.length > 1 ? parts[0] : 'S1';
  }

  isJourneyPast(journeyDate?: string, departureTime?: string): boolean {
    if (!journeyDate) return false;
    const time = departureTime || '23:59:59';
    const journeyDateTime = new Date(`${journeyDate}T${time}`);
    return journeyDateTime.getTime() < Date.now();
  }

  canCancel(): boolean {
    if (!this.ticket) return false;
    const status = this.ticket.status;
    if (status !== 'CONFIRMED' && status !== 'WAITING_LIST' && status !== 'PARTIALLY_CONFIRMED') return false;
    const hasActivePax = this.ticket.passengers && this.ticket.passengers.some(p => p.status !== 'CANCELLED' && p.status !== 'EXPIRED');
    if (!hasActivePax && this.ticket.passengers && this.ticket.passengers.length > 0) return false;
    return !this.isJourneyPast(this.ticket.journeyDate, this.ticket.departureTime);
  }

  get hasActivePassengers(): boolean {
    if (!this.ticket || !this.ticket.passengers) return false;
    return this.ticket.passengers.some(p => p.status !== 'CANCELLED' && p.status !== 'EXPIRED');
  }

  get selectedPassengers(): PassengerResponse[] {
    return this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger);
  }

  get selectedPassengersCount(): number {
    return this.selectablePassengers.filter(p => p.isSelected).length;
  }

  printTicket(): void {
    window.print();
  }

  downloadPdf(): void {
    if (!this.pnr) return;
    this.ticketService.downloadTicketPdf(this.pnr).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `E-Ticket-${this.pnr}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.toast.error('Unable to download PDF ticket. Please try again.');
      }
    });
  }

  openCancelModal(): void {
    if (!this.ticket) return;
    this.initSelectablePassengers();
    if (this.selectablePassengers.length === 0) {
      this.toast.info('All passengers on this ticket are already cancelled.');
      return;
    }
    this.isConfirmStep = false;
    this.cancelReason = 'Change of travel plans';
    this.cancelModalOpen = true;
    this.fetchCancellationPreview();
  }

  cancelSelectedPassengers(): void {
    if (this.selectedPassengersCount === 0) {
      this.toast.warning('Please select at least one passenger to cancel.');
      return;
    }
    this.isConfirmStep = true;
    this.cancelModalOpen = true;
    this.fetchCancellationPreview();
  }

  cancelEntirePnr(): void {
    if (!this.ticket) return;
    this.initSelectablePassengers();
    this.selectablePassengers.forEach(p => p.isSelected = true);
    this.selectAllPassengers = true;
    this.isConfirmStep = true;
    this.cancelModalOpen = true;
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
    if (!this.ticket) return;
    const selectedIds = this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger.id);
    if (selectedIds.length === 0) {
      this.cancellationPreview = null;
      this.cdr.markForCheck();
      return;
    }

    this.loadingPreview = true;
    this.reservationService.previewCancellation(this.ticket.reservationId, selectedIds).pipe(
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
        const fare = this.ticket!.totalAmount || 891;
        const count = selectedIds.length;
        const totalPax = this.selectablePassengers.length || 1;
        const perPax = fare / totalPax;
        const refundAmt = Math.max(0, perPax * count * 0.9);
        this.cancellationPreview = {
          pnr: this.ticket!.pnr,
          reservationId: this.ticket!.reservationId,
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
    if (!this.ticket) return;
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

    this.reservationService.cancelReservation(this.ticket.reservationId, request).pipe(
      finalize(() => {
        this.cancelling = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.cancelModalOpen = false;
        this.isConfirmStep = false;
        const isFull = this.selectablePassengers.length === selectedIds.length;
        if (isFull) {
          this.toast.success(`PNR ${this.ticket?.pnr} successfully cancelled in full. Refund processed.`);
        } else {
          this.toast.success(`Cancelled ${selectedIds.length} passenger(s) on PNR ${this.ticket?.pnr}. Remaining passengers stay confirmed.`);
        }

        try {
          if (this.ticket?.passengers) {
            const cancelledSeats = this.selectablePassengers.filter(p => p.isSelected).map(p => p.passenger.seatNumber).filter(Boolean);
            const currentBooked: string[] = JSON.parse(localStorage.getItem('user_booked_seat_numbers') || '[]');
            const updated = currentBooked.filter(s => !cancelledSeats.includes(s));
            localStorage.setItem('user_booked_seat_numbers', JSON.stringify(updated));
          }
        } catch (e) {}
        this.loadTicket();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to cancel reservation.');
        this.cdr.markForCheck();
      }
    });
  }
}
