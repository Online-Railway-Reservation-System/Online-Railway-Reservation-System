import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { StationService } from '../../core/services/station.service';
import { TrainService } from '../../core/services/train.service';
import { ToastService } from '../../core/services/toast.service';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { RouteStop, RouteStopRequest, Station } from '../../core/models/station.models';
import { Train } from '../../core/models/train.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-routes-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, ModalComponent, TranslatePipe],
  templateUrl: './routes-admin.component.html',
  styleUrl: './routes-admin.component.css'
})
export class RoutesAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  private stationService = inject(StationService);
  private trainService = inject(TrainService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  submitting = false;
  modalOpen = false;

  trains: Train[] = [];
  stations: Station[] = [];
  selectedTrainId: number | null = null;
  routeStops: RouteStop[] = [];

  stopForm: FormGroup = this.fb.group({
    stationId: [1, Validators.required],
    stopSequence: [1, [Validators.required, Validators.min(1)]],
    distanceFromOriginKm: [0, [Validators.required, Validators.min(0)]],
    haltDurationMinutes: [2, [Validators.min(1)]]
  });

  ngOnInit(): void {
    this.trainService.getAllTrains().subscribe({
      next: (res) => {
        this.trains = res.data || [];
        if (this.trains.length > 0) {
          this.selectedTrainId = this.trains[0].id;
          this.loadRouteStops();
        }
      },
      error: () => {}
    });

    this.stationService.getActiveStations().subscribe({
      next: (sList: Station[]) => {
        this.stations = sList;
        if (sList.length > 0) {
          this.stopForm.patchValue({ stationId: sList[0].id });
        }
      },
      error: () => {}
    });
  }

  loadRouteStops(): void {
    if (!this.selectedTrainId) return;

    this.loading = true;
    this.stationService.getRouteStops(this.selectedTrainId).pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (stops: RouteStop[]) => {
        this.routeStops = (stops || []).sort((a: RouteStop, b: RouteStop) => a.stopSequence - b.stopSequence);
        this.cdr.markForCheck();
      },
      error: () => {
        this.routeStops = [];
        this.cdr.markForCheck();
      }
    });
  }

  editingStopId: number | null = null;

  openAddStopModal(): void {
    this.editingStopId = null;
    const nextSeq = this.routeStops.length + 1;
    this.stopForm.patchValue({
      stationId: this.stations[0]?.id || 1,
      stopSequence: nextSeq,
      distanceFromOriginKm: nextSeq * 100,
      haltDurationMinutes: 2
    });
    this.modalOpen = true;
    this.cdr.markForCheck();
  }

  openEditStopModal(stop: RouteStop): void {
    this.editingStopId = stop.id;
    // Find matching station by code or id
    const matched = this.stations.find(s => s.stationCode === stop.stationCode || s.id === stop.stationId);
    this.stopForm.patchValue({
      stationId: matched ? matched.id : (this.stations[0]?.id || 1),
      stopSequence: stop.stopSequence,
      distanceFromOriginKm: stop.distanceFromOriginKm,
      haltDurationMinutes: stop.haltDurationMinutes || 2
    });
    this.modalOpen = true;
    this.cdr.markForCheck();
  }

  onSubmitStop(): void {
    if (this.stopForm.invalid || !this.selectedTrainId) return;

    this.submitting = true;
    this.cdr.markForCheck();
    const val = this.stopForm.value;
    const selectedStation = this.stations.find(s => s.id === Number(val.stationId));

    const req: RouteStopRequest = {
      trainId: this.selectedTrainId,
      stationId: Number(val.stationId),
      stationCode: selectedStation ? selectedStation.stationCode : '',
      stationName: selectedStation ? selectedStation.stationName : '',
      stopSequence: Number(val.stopSequence),
      distanceFromOriginKm: Number(val.distanceFromOriginKm),
      haltDurationMinutes: Number(val.haltDurationMinutes)
    };

    const action$ = this.editingStopId
      ? this.stationService.updateRouteStop(this.editingStopId, req)
      : this.stationService.addRouteStop(req);

    action$.pipe(
      finalize(() => {
        this.submitting = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.modalOpen = false;
        this.toast.success(this.editingStopId ? 'Route stop updated successfully!' : 'Route stop added successfully!');
        this.editingStopId = null;
        this.loadRouteStops();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to save route stop.');
        this.cdr.markForCheck();
      }
    });
  }

  deleteStop(stop: RouteStop): void {
    if (!confirm(`Remove ${stop.stationName} from route?`)) return;

    this.stationService.deleteRouteStop(stop.id).subscribe({
      next: () => {
        this.toast.success('Route stop removed.');
        this.loadRouteStops();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to remove route stop.');
        this.cdr.markForCheck();
      }
    });
  }
}
