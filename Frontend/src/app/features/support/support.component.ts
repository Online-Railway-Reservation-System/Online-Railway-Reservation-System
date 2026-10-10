import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CustomerService } from '../../core/services/customer.service';
import { ToastService } from '../../core/services/toast.service';
import { SupportQuery } from '../../core/models/customer.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslatePipe],
  templateUrl: './support.component.html',
  styleUrl: './support.component.css'
})
export class SupportComponent implements OnInit {
  private fb = inject(FormBuilder);
  private customerService = inject(CustomerService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  submitting = false;
  queries: SupportQuery[] = [];

  queryForm: FormGroup = this.fb.group({
    category: ['BOOKING', Validators.required],
    subject: ['', [Validators.required, Validators.minLength(5)]],
    pnr: [''],
    description: ['', [Validators.required, Validators.minLength(10)]]
  });

  ngOnInit(): void {
    this.loadQueries();
  }

  loadQueries(): void {
    this.loading = true;
    this.customerService.getMySupportQueries().pipe(
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
        this.cdr.markForCheck();
      }
    });
  }

  onSubmitQuery(): void {
    if (this.queryForm.invalid) return;

    this.submitting = true;
    this.customerService.submitSupportQuery(this.queryForm.value).pipe(
      finalize(() => {
        this.submitting = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.toast.success('Your support ticket has been submitted. Our team will review it shortly.');
        this.queryForm.reset({ category: 'BOOKING' });
        this.loadQueries();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to submit ticket.');
        this.cdr.markForCheck();
      }
    });
  }
}
