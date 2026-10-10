import { Component, OnInit, inject, ChangeDetectorRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { TrainService } from '../../core/services/train.service';
import { ToastService } from '../../core/services/toast.service';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { Train, TrainRequest, TrainReadiness } from '../../core/models/train.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

import { InventoryService } from '../../core/services/inventory.service';
import { Coach } from '../../core/models/inventory.models';

@Component({
  selector: 'app-trains-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, ModalComponent, TranslatePipe],
  templateUrl: './trains-admin.component.html',
  styleUrl: './trains-admin.component.css'
})
export class TrainsAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  private trainService = inject(TrainService);
  private inventoryService = inject(InventoryService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  submitting = false;
  modalOpen = false;
  trains: Train[] = [];
  editingTrain: Train | null = null;
  searchQuery: string = '';
  statusFilter: 'ALL' | 'ACTIVE' | 'INACTIVE' = 'ALL';
  bookingFilter: 'ALL' | 'ENABLED' | 'DISABLED' = 'ALL';
  configFilter: 'ALL' | 'READY' | 'INCOMPLETE' = 'ALL';
  activeMenuTrainId: number | null = null;
  confirmDeleteModalOpen = false;
  trainToDelete: Train | null = null;
  deletingTrain = false;

  @HostListener('document:click')
  onDocumentClick(): void {
    this.closeMenu();
  }

  // Readiness State
  readinessMap = new Map<number, TrainReadiness>();
  selectedReadinessTrain: TrainReadiness | null = null;
  readinessModalOpen = false;
  loadingReadiness = false;

  get filteredTrains(): Train[] {
    return this.trains.filter(t => {
      // Search query filter
      if (this.searchQuery && this.searchQuery.trim()) {
        const q = this.searchQuery.toLowerCase().trim();
        const matchesName = t.trainName && t.trainName.toLowerCase().includes(q);
        const matchesNum = t.trainNumber && t.trainNumber.toLowerCase().includes(q);
        const matchesType = t.trainType && t.trainType.toLowerCase().includes(q);
        if (!matchesName && !matchesNum && !matchesType) return false;
      }

      // Status filter
      if (this.statusFilter === 'ACTIVE' && !t.activeStatus) return false;
      if (this.statusFilter === 'INACTIVE' && t.activeStatus) return false;

      // Booking & Config filters via readiness
      const r = this.readinessMap.get(t.id);
      if (this.bookingFilter === 'ENABLED' && r?.bookingStatus !== 'ENABLED') return false;
      if (this.bookingFilter === 'DISABLED' && r?.bookingStatus === 'ENABLED') return false;

      if (this.configFilter === 'READY' && !r?.readyForBooking) return false;
      if (this.configFilter === 'INCOMPLETE' && r?.readyForBooking) return false;

      return true;
    });
  }

  toggleMenu(trainId: number, event: MouseEvent): void {
    event.stopPropagation();
    this.activeMenuTrainId = this.activeMenuTrainId === trainId ? null : trainId;
  }

  closeMenu(): void {
    this.activeMenuTrainId = null;
  }

  // Coach Management State
  coachModalOpen = false;
  selectedTrainForCoaches: Train | null = null;
  loadingCoaches = false;
  submittingCoach = false;
  coachesList: Coach[] = [];
  editingCoach: Coach | null = null;

  trainForm: FormGroup = this.fb.group({
    trainNumber: ['', [Validators.required, Validators.minLength(2)]],
    trainName: ['', [Validators.required, Validators.minLength(3)]],
    trainType: ['SUPERFAST', Validators.required],
    activeStatus: [true]
  });

  coachForm: FormGroup = this.fb.group({
    coachNumber: ['', [Validators.required, Validators.minLength(2)]],
    classType: ['SL', Validators.required],
    totalSeats: [72, [Validators.required, Validators.min(0)]],
    activeStatus: [true]
  });

  ngOnInit(): void {
    this.loadTrains();
  }

  loadTrains(): void {
    this.loading = true;
    this.trainService.getAllTrains().pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.trains = res.data || [];
        this.loadReadiness();
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.error('Failed to load trains list.');
        this.cdr.markForCheck();
      }
    });
  }

  loadReadiness(): void {
    this.loadingReadiness = true;
    this.trainService.getAllTrainsReadiness().pipe(
      finalize(() => {
        this.loadingReadiness = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        if (res.data) {
          const map = new Map<number, TrainReadiness>();
          res.data.forEach(r => map.set(r.trainId, r));
          this.readinessMap = map;
          this.cdr.markForCheck();
        }
      },
      error: () => {}
    });
  }

  openReadinessModal(trainId: number): void {
    const cached = this.readinessMap.get(trainId);
    if (cached) {
      this.selectedReadinessTrain = cached;
      this.readinessModalOpen = true;
      this.cdr.markForCheck();
    }
    // Always refresh single train readiness to get latest info
    this.trainService.getTrainReadiness(trainId).subscribe({
      next: (res) => {
        if (res.data) {
          this.selectedReadinessTrain = res.data;
          this.readinessMap.set(trainId, res.data);
          this.readinessModalOpen = true;
          this.cdr.markForCheck();
        }
      }
    });
  }

  openCreateModal(): void {
    this.editingTrain = null;
    this.trainForm.reset({
      trainNumber: '',
      trainName: '',
      trainType: 'SUPERFAST',
      activeStatus: true
    });
    this.modalOpen = true;
  }

  openEditModal(train: Train): void {
    this.editingTrain = train;
    this.trainForm.patchValue({
      trainNumber: train.trainNumber,
      trainName: train.trainName,
      trainType: train.trainType,
      activeStatus: train.activeStatus
    });
    this.modalOpen = true;
  }

  onSubmit(): void {
    if (this.trainForm.invalid) return;

    this.submitting = true;
    const req: TrainRequest = this.trainForm.value;

    if (this.editingTrain) {
      this.trainService.updateTrain(this.editingTrain.id, req).pipe(
        finalize(() => {
          this.submitting = false;
          this.cdr.markForCheck();
        })
      ).subscribe({
        next: () => {
          this.modalOpen = false;
          this.toast.success('Train updated successfully!');
          this.loadTrains();
        },
        error: (err) => {
          this.toast.error(err.error?.message || 'Failed to update train.');
          this.cdr.markForCheck();
        }
      });
    } else {
      this.trainService.createTrain(req).pipe(
        finalize(() => {
          this.submitting = false;
          this.cdr.markForCheck();
        })
      ).subscribe({
        next: () => {
          this.modalOpen = false;
          this.toast.success('Train created successfully!');
          this.loadTrains();
        },
        error: (err) => {
          this.toast.error(err.error?.message || 'Failed to create train.');
          this.cdr.markForCheck();
        }
      });
    }
  }

  deactivateTrain(train: Train): void {
    if (!confirm(`Are you sure you want to deactivate train ${train.trainNumber} - ${train.trainName}?`)) {
      return;
    }

    this.trainService.deactivateTrain(train.id).subscribe({
      next: () => {
        this.toast.success(`Train ${train.trainNumber} deactivated.`);
        this.loadTrains();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to deactivate train.');
        this.cdr.markForCheck();
      }
    });
  }

  deleteTrain(train: Train): void {
    this.deactivateTrain(train);
  }

  promptPermanentDelete(train: Train): void {
    this.trainToDelete = train;
    this.confirmDeleteModalOpen = true;
    this.cdr.markForCheck();
  }

  onCancelPermanentDelete(): void {
    this.confirmDeleteModalOpen = false;
    this.trainToDelete = null;
    this.cdr.markForCheck();
  }

  onConfirmPermanentDelete(): void {
    if (!this.trainToDelete) return;
    this.deletingTrain = true;
    const trainNum = this.trainToDelete.trainNumber;
    this.trainService.permanentDeleteTrain(this.trainToDelete.id).pipe(
      finalize(() => {
        this.deletingTrain = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.confirmDeleteModalOpen = false;
        this.trainToDelete = null;
        this.toast.success(`Train ${trainNum} permanently deleted.`);
        this.loadTrains();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to delete train.');
        this.cdr.markForCheck();
      }
    });
  }

  // Coach Management Handlers
  openCoachModal(train: Train): void {
    this.selectedTrainForCoaches = train;
    this.editingCoach = null;
    this.coachForm.reset({
      coachNumber: '',
      classType: 'SL',
      totalSeats: 72,
      activeStatus: true
    });
    this.coachModalOpen = true;
    this.loadTrainCoaches(train.id);
  }

  loadTrainCoaches(trainId: number): void {
    this.loadingCoaches = true;
    this.inventoryService.getAdminCoaches(trainId).pipe(
      finalize(() => {
        this.loadingCoaches = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.coachesList = res.data || [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.coachesList = [];
        this.cdr.markForCheck();
      }
    });
  }

  onCoachClassChange(classType: string): void {
    let defaultSeats = 72;
    if (classType === '1A') defaultSeats = 24;
    else if (classType === '2A') defaultSeats = 48;
    else if (classType === '3A') defaultSeats = 64;
    else if (classType === 'CC') defaultSeats = 78;
    else if (classType === '2S') defaultSeats = 90;
    this.coachForm.patchValue({ totalSeats: defaultSeats });
  }

  openEditCoach(coach: Coach): void {
    this.editingCoach = coach;
    this.coachForm.patchValue({
      coachNumber: coach.coachNumber,
      classType: coach.classType,
      totalSeats: coach.totalSeats,
      activeStatus: coach.activeStatus
    });
  }

  cancelEditCoach(): void {
    this.editingCoach = null;
    this.coachForm.reset({
      coachNumber: '',
      classType: 'SL',
      totalSeats: 72,
      activeStatus: true
    });
  }

  get calculatedQuotaPreview(): { general: number; tatkal: number; ladies: number; disability: number } {
    const total = Number(this.coachForm.get('totalSeats')?.value) || 0;
    if (total <= 0) return { general: 0, tatkal: 0, ladies: 0, disability: 0 };
    if (total === 1) return { general: 1, tatkal: 0, ladies: 0, disability: 0 };
    if (total === 2) return { general: 2, tatkal: 0, ladies: 0, disability: 0 };

    let disability = Math.round(total * 0.10);
    if (disability === 0 && total >= 4) disability = 1;

    let ladies = Math.round(total * 0.15);
    if (ladies === 0 && total >= 3) ladies = 1;

    let tatkal = Math.round(total * 0.15);
    if (tatkal === 0 && total >= 5) tatkal = 1;

    let general = total - (disability + ladies + tatkal);
    if (general < 1) {
      general = 1;
      while ((general + disability + ladies + tatkal) > total) {
        if (tatkal > 0) tatkal--;
        else if (ladies > 0) ladies--;
        else if (disability > 0) disability--;
        else break;
      }
    }

    return { general, tatkal, ladies, disability };
  }

  onSubmitCoach(): void {
    if (this.coachForm.invalid || !this.selectedTrainForCoaches) return;

    const formVal = this.coachForm.value;
    const totalSeats = Number(formVal.totalSeats) || 0;
    const activeStatus = formVal.activeStatus !== false;

    if (activeStatus && totalSeats < 10) {
      this.toast.error('Coach cannot be enabled. At least 10 seats must be configured.');
      return;
    }

    this.submittingCoach = true;
    const req: Coach = {
      trainId: this.selectedTrainForCoaches.id,
      coachNumber: formVal.coachNumber.trim().toUpperCase(),
      classType: formVal.classType,
      totalSeats: totalSeats,
      activeStatus: activeStatus && totalSeats >= 10
    };

    const call$ = this.editingCoach?.id
      ? this.inventoryService.updateCoach(this.editingCoach.id, req)
      : this.inventoryService.createCoach(req);

    call$.pipe(
      finalize(() => {
        this.submittingCoach = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.toast.success(this.editingCoach ? 'Coach updated successfully!' : 'Coach added successfully!');
        this.cancelEditCoach();
        this.loadTrainCoaches(this.selectedTrainForCoaches!.id);
        this.loadReadiness();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to save coach.');
        this.cdr.markForCheck();
      }
    });
  }

  toggleCoachStatus(coach: Coach): void {
    if (!coach.id || !this.selectedTrainForCoaches) return;
    const newStatus = !coach.activeStatus;
    if (newStatus && (coach.totalSeats == null || coach.totalSeats < 10)) {
      this.toast.error('Coach cannot be enabled. At least 10 seats must be configured.');
      return;
    }

    const updated: Partial<Coach> = {
      ...coach,
      activeStatus: newStatus
    };

    this.inventoryService.updateCoach(coach.id, updated).subscribe({
      next: () => {
        this.toast.success(`Coach ${coach.coachNumber} ${newStatus ? 'enabled' : 'disabled'} successfully!`);
        this.loadTrainCoaches(this.selectedTrainForCoaches!.id);
        this.loadReadiness();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to update coach status.');
      }
    });
  }

  canEnableCoach(coach: Coach): boolean {
    return !!(coach && coach.totalSeats !== undefined && coach.totalSeats !== null && coach.totalSeats >= 10);
  }

  deleteCoach(coach: Coach): void {
    if (!coach.id || !confirm(`Deactivate coach ${coach.coachNumber} (${coach.classType})?`)) return;

    this.inventoryService.deleteCoach(coach.id).subscribe({
      next: () => {
        this.toast.success(`Coach ${coach.coachNumber} deactivated.`);
        this.loadTrainCoaches(this.selectedTrainForCoaches!.id);
        this.loadReadiness();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to deactivate coach.');
        this.cdr.markForCheck();
      }
    });
  }
}
