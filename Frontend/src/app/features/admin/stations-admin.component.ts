import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { StationService } from '../../core/services/station.service';
import { ToastService } from '../../core/services/toast.service';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { Station, StationRequest } from '../../core/models/station.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-stations-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent, TranslatePipe],
  templateUrl: './stations-admin.component.html',
  styleUrl: './stations-admin.component.css'
})
export class StationsAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  private stationService = inject(StationService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  submitting = false;
  modalOpen = false;
  stations: Station[] = [];
  editingStation: Station | null = null;

  stationForm: FormGroup = this.fb.group({
    stationCode: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(5)]],
    stationName: ['', [Validators.required, Validators.minLength(3)]],
    city: ['', Validators.required],
    state: ['', Validators.required],
    activeStatus: [true]
  });

  ngOnInit(): void {
    this.loadStations();
  }

  loadStations(): void {
    this.loading = true;
    this.stationService.getActiveStations().pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (data) => {
        this.stations = data || [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.error('Failed to load stations.');
        this.cdr.markForCheck();
      }
    });
  }

  openCreateModal(): void {
    this.editingStation = null;
    this.stationForm.reset({
      stationCode: '',
      stationName: '',
      city: '',
      state: '',
      activeStatus: true
    });
    this.modalOpen = true;
  }

  openEditModal(station: Station): void {
    this.editingStation = station;
    this.stationForm.patchValue({
      stationCode: station.stationCode,
      stationName: station.stationName,
      city: station.city,
      state: station.state,
      activeStatus: station.activeStatus
    });
    this.modalOpen = true;
  }

  onSubmit(): void {
    if (this.stationForm.invalid) return;

    this.submitting = true;
    const req: StationRequest = this.stationForm.value;

    if (this.editingStation) {
      this.stationService.updateStation(this.editingStation.id, req).pipe(
        finalize(() => {
          this.submitting = false;
          this.cdr.markForCheck();
        })
      ).subscribe({
        next: () => {
          this.modalOpen = false;
          this.toast.success('Station updated successfully!');
          this.loadStations();
        },
        error: (err) => {
          this.toast.error(err.error?.message || 'Failed to update station.');
          this.cdr.markForCheck();
        }
      });
    } else {
      this.stationService.createStation(req).pipe(
        finalize(() => {
          this.submitting = false;
          this.cdr.markForCheck();
        })
      ).subscribe({
        next: () => {
          this.modalOpen = false;
          this.toast.success('Station registered successfully!');
          this.loadStations();
        },
        error: (err) => {
          this.toast.error(err.error?.message || 'Failed to create station.');
          this.cdr.markForCheck();
        }
      });
    }
  }

  deleteStation(station: Station): void {
    if (!confirm(`Are you sure you want to deactivate station ${station.stationName}?`)) {
      return;
    }

    this.stationService.deleteStation(station.id).subscribe({
      next: () => {
        this.toast.success(`Station ${station.stationCode} deactivated.`);
        this.loadStations();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to deactivate station.');
        this.cdr.markForCheck();
      }
    });
  }
}
