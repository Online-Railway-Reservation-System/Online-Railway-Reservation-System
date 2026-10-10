import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CustomerService } from '../../core/services/customer.service';
import { ToastService } from '../../core/services/toast.service';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { SupportQuery } from '../../core/models/customer.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-support-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, ModalComponent, TranslatePipe],
  templateUrl: './support-admin.component.html',
  styleUrl: './support-admin.component.css'
})
export class SupportAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  private customerService = inject(CustomerService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  submitting = false;
  statusFilter = 'ALL';
  queries: SupportQuery[] = [];

  replyModalOpen = false;
  selectedQuery: SupportQuery | null = null;

  replyForm: FormGroup = this.fb.group({
    replyMessage: ['', [Validators.required, Validators.minLength(5)]]
  });

  ngOnInit(): void {
    this.loadQueries();
  }

  loadQueries(): void {
    this.loading = true;
    this.customerService.getAllQueriesAdmin(this.statusFilter).pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.queries = res.data || [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.queries = [];
        this.cdr.markForCheck();
      }
    });
  }

  openReplyModal(q: SupportQuery): void {
    this.selectedQuery = q;
    this.replyForm.patchValue({
      replyMessage: q.adminReply || q.replyMessage || ''
    });
    this.replyModalOpen = true;
  }

  onSubmitReply(): void {
    if (this.replyForm.invalid || !this.selectedQuery) return;

    this.submitting = true;
    const msg = this.replyForm.value.replyMessage;

    this.customerService.replyToQuery(this.selectedQuery.id, msg).pipe(
      finalize(() => {
        this.submitting = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.replyModalOpen = false;
        this.toast.success('Reply submitted to customer.');
        this.loadQueries();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to submit reply.');
        this.cdr.markForCheck();
      }
    });
  }
}
