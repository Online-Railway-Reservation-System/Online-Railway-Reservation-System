import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { TrainService } from '../../core/services/train.service';
import { StationService } from '../../core/services/station.service';
import { CustomerService } from '../../core/services/customer.service';
import { ReservationService } from '../../core/services/reservation.service';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslatePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  private trainService = inject(TrainService);
  private stationService = inject(StationService);
  private customerService = inject(CustomerService);
  private reservationService = inject(ReservationService);
  private cdr = inject(ChangeDetectorRef);

  trainCount = 0;
  stationCount = 0;
  customerCount = 0;
  bookingCount = 0;
  openQueriesCount = 0;


  ngOnInit(): void {
    this.trainService.getAllTrains().subscribe({
      next: (res) => {
        this.trainCount = res.data?.length || 0;
        this.cdr.markForCheck();
      },
      error: () => {
        this.trainCount = 0;
        this.cdr.markForCheck();
      }
    });

    this.stationService.getActiveStations().subscribe({
      next: (stations) => {
        this.stationCount = stations.length;
        this.cdr.markForCheck();
      },
      error: () => {
        this.stationCount = 0;
        this.cdr.markForCheck();
      }
    });

    this.customerService.getAllCustomersAdmin().subscribe({
      next: (res) => {
        this.customerCount = res.data?.length || 0;
        this.cdr.markForCheck();
      },
      error: () => {
        this.customerCount = 0;
        this.cdr.markForCheck();
      }
    });

    this.reservationService.getAllReservationsAdmin().subscribe({
      next: (res) => {
        const data = res.data;
        this.bookingCount = data?.totalElements ?? (Array.isArray(data) ? data.length : (data?.content?.length || 0));
        this.cdr.markForCheck();
      },
      error: () => {
        this.bookingCount = 0;
        this.cdr.markForCheck();
      }
    });

    this.customerService.getAllQueriesAdmin('OPEN').subscribe({
      next: (res) => {
        this.openQueriesCount = res.data?.length || 0;
        this.cdr.markForCheck();
      },
      error: () => {
        this.openQueriesCount = 0;
        this.cdr.markForCheck();
      }
    });
  }
}
