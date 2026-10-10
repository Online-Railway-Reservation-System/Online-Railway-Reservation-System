import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ScheduleFareService } from '../../core/services/schedule-fare.service';
import { TrainService } from '../../core/services/train.service';
import { StationService } from '../../core/services/station.service';
import { ToastService } from '../../core/services/toast.service';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { TrainSchedule, TrainScheduleRequest, TatkalConfig, FareRule } from '../../core/models/schedule-fare.models';
import { Train } from '../../core/models/train.models';
import { Station } from '../../core/models/station.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-schedules-fares-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, ModalComponent, TranslatePipe],
  templateUrl: './schedules-fares-admin.component.html',
  styleUrl: './schedules-fares-admin.component.css'
})
export class SchedulesFaresAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  private scheduleFareService = inject(ScheduleFareService);
  private trainService = inject(TrainService);
  private stationService = inject(StationService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  trains: Train[] = [];
  stations: Station[] = [];
  selectedTrainId: number | null = null;

  loadingSchedules = false;
  loadingFares = false;
  submitting = false;
  schedules: TrainSchedule[] = [];
  fareRules: FareRule[] = [];
  tatkalConfig: TatkalConfig | null = null;

  scheduleModalOpen = false;
  tatkalModalOpen = false;
  fareModalOpen = false;
  editingSchedule: TrainSchedule | null = null;

  scheduleForm: FormGroup = this.fb.group({
    departureStationCode: ['', Validators.required],
    arrivalStationCode: ['', Validators.required],
    departureTime: ['06:00', Validators.required],
    arrivalTime: ['14:30', Validators.required],
    runningDays: ['MON,TUE,WED,THU,FRI,SAT,SUN', Validators.required],
    durationHours: [8.5, [Validators.required, Validators.min(0.1)]]
  });

  fareForm: FormGroup = this.fb.group({
    classType: ['SL', Validators.required],
    baseFarePerKm: [0.65, [Validators.required, Validators.min(0.01)]],
    minimumFare: [150.0, [Validators.required, Validators.min(0)]],
    reservationFee: [50.0, [Validators.required, Validators.min(0)]],
    superfastCharge: [40.0, [Validators.required, Validators.min(0)]]
  });

  tatkalForm: FormGroup = this.fb.group({
    advanceDays: [1, [Validators.required, Validators.min(1)]],
    acOpeningTime: ['10:00:00', Validators.required],
    nonAcOpeningTime: ['11:00:00', Validators.required],
    surchargePercentage: [30.0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.trainService.getAllTrains().subscribe({
      next: (res) => {
        this.trains = res.data || [];
        if (this.trains.length > 0) {
          this.selectedTrainId = this.trains[0].id;
          this.loadTrainData();
        }
      },
      error: () => {}
    });

    this.stationService.getActiveStations().subscribe({
      next: (sList: Station[]) => {
        this.stations = sList;
        if (sList.length > 1) {
          this.scheduleForm.patchValue({
            departureStationCode: sList[0].stationCode,
            arrivalStationCode: sList[1].stationCode
          });
        }
      },
      error: () => {}
    });
  }

  loadTrainData(): void {
    if (!this.selectedTrainId) return;

    this.loadingSchedules = true;
    this.scheduleFareService.getSchedule(this.selectedTrainId).pipe(
      finalize(() => {
        this.loadingSchedules = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.schedules = res.data || [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.schedules = [];
        this.cdr.markForCheck();
      }
    });

    this.scheduleFareService.getTatkalConfig(this.selectedTrainId).subscribe({
      next: (res) => {
        this.tatkalConfig = res.data;
        if (this.tatkalConfig) {
          this.tatkalForm.patchValue({
            advanceDays: this.tatkalConfig.advanceDays,
            acOpeningTime: this.tatkalConfig.acOpeningTime,
            nonAcOpeningTime: this.tatkalConfig.nonAcOpeningTime,
            surchargePercentage: this.tatkalConfig.surchargePercentage
          });
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.tatkalConfig = {
          trainId: this.selectedTrainId!,
          advanceDays: 1,
          acOpeningTime: '10:00:00',
          nonAcOpeningTime: '11:00:00',
          surchargePercentage: 30.0
        };
        this.cdr.markForCheck();
      }
    });

    this.loadingFares = true;
    this.scheduleFareService.getFareRules(this.selectedTrainId).pipe(
      finalize(() => {
        this.loadingFares = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.fareRules = res.data || [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.fareRules = [];
        this.cdr.markForCheck();
      }
    });
  }

  openScheduleModal(schedule?: TrainSchedule): void {
    this.editingSchedule = schedule || null;
    if (schedule) {
      this.scheduleForm.patchValue({
        departureStationCode: schedule.departureStationCode || this.stations[0]?.stationCode || '',
        arrivalStationCode: schedule.arrivalStationCode || (this.stations[1]?.stationCode || this.stations[0]?.stationCode || ''),
        departureTime: schedule.departureTime || '06:00',
        arrivalTime: schedule.arrivalTime || '14:30',
        runningDays: schedule.runningDays || 'MON,TUE,WED,THU,FRI,SAT,SUN',
        durationHours: schedule.durationHours || 8.5
      });
    } else {
      this.scheduleForm.reset({
        departureStationCode: this.stations[0]?.stationCode || '',
        arrivalStationCode: this.stations[1]?.stationCode || this.stations[0]?.stationCode || '',
        departureTime: '06:00',
        arrivalTime: '14:30',
        runningDays: 'MON,TUE,WED,THU,FRI,SAT,SUN',
        durationHours: 8.5
      });
    }
    this.scheduleModalOpen = true;
    this.cdr.markForCheck();
  }

  openTatkalModal(): void {
    this.tatkalModalOpen = true;
    this.cdr.markForCheck();
  }

  openFareModal(rule?: FareRule): void {
    if (rule) {
      this.fareForm.patchValue({
        classType: rule.classType,
        baseFarePerKm: rule.baseFarePerKm,
        minimumFare: rule.minimumFare,
        reservationFee: rule.reservationFee,
        superfastCharge: rule.superfastCharge
      });
    } else {
      this.fareForm.reset({
        classType: 'SL',
        baseFarePerKm: 0.65,
        minimumFare: 150.0,
        reservationFee: 50.0,
        superfastCharge: 40.0
      });
    }
    this.fareModalOpen = true;
    this.cdr.markForCheck();
  }

  onSubmitFare(): void {
    if (this.fareForm.invalid || !this.selectedTrainId) return;

    this.submitting = true;
    this.cdr.markForCheck();
    const req: FareRule = {
      trainId: this.selectedTrainId,
      ...this.fareForm.value
    };

    this.scheduleFareService.saveFareRule(req).pipe(
      finalize(() => {
        this.submitting = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.fareModalOpen = false;
        this.toast.success('Fare rule configured successfully!');
        this.loadTrainData();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to save fare rule.');
        this.cdr.markForCheck();
      }
    });
  }

  onSubmitSchedule(): void {
    if (this.scheduleForm.invalid || !this.selectedTrainId) return;

    this.submitting = true;
    const formVal = this.scheduleForm.value;
    const req: TrainScheduleRequest = {
      trainId: this.selectedTrainId,
      departureStationCode: formVal.departureStationCode,
      arrivalStationCode: formVal.arrivalStationCode,
      departureTime: formVal.departureTime,
      arrivalTime: formVal.arrivalTime,
      runningDays: formVal.runningDays,
      durationHours: Number(formVal.durationHours) || 8.0,
      activeStatus: true
    };

    const call$ = this.editingSchedule
      ? this.scheduleFareService.updateSchedule(this.editingSchedule.id, req)
      : this.scheduleFareService.createSchedule(req);

    call$.pipe(
      finalize(() => {
        this.submitting = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.scheduleModalOpen = false;
        this.toast.success(this.editingSchedule ? 'Schedule updated successfully!' : 'Schedule created successfully!');
        this.editingSchedule = null;
        this.loadTrainData();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to save train schedule.');
        this.cdr.markForCheck();
      }
    });
  }

  onSubmitTatkal(): void {
    if (this.tatkalForm.invalid || !this.selectedTrainId) return;

    this.submitting = true;
    const req: TatkalConfig = {
      trainId: this.selectedTrainId,
      ...this.tatkalForm.value
    };

    this.scheduleFareService.saveTatkalConfig(req).pipe(
      finalize(() => {
        this.submitting = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.tatkalModalOpen = false;
        this.tatkalConfig = res.data;
        this.toast.success('Tatkal window settings updated successfully!');
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to update Tatkal rules.');
        this.cdr.markForCheck();
      }
    });
  }

  deleteSchedule(s: TrainSchedule): void {
    if (!confirm(`Delete schedule stop for ${s.stationCode || s.departureStationCode}?`)) return;

    this.scheduleFareService.deleteSchedule(s.id).subscribe({
      next: () => {
        this.toast.success('Schedule stop removed.');
        this.schedules = this.schedules.filter(item => item.id !== s.id);
        this.cdr.markForCheck();
        this.loadTrainData();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to remove stop.');
        this.cdr.markForCheck();
      }
    });
  }
}
