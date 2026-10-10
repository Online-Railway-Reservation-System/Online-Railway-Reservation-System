import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { CustomerService } from '../../core/services/customer.service';
import { ToastService } from '../../core/services/toast.service';
import { CustomerProfile } from '../../core/models/customer.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-customers-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './customers-admin.component.html',
  styleUrl: './customers-admin.component.css'
})
export class CustomersAdminComponent implements OnInit {
  private customerService = inject(CustomerService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  loading = true;
  generatingPdf = false;
  customers: CustomerProfile[] = [];
  selectedStatusFilter: 'ALL' | 'ACTIVE' | 'SUSPENDED' = 'ALL';

  // Pagination state
  currentPage = 1;
  pageSize = 10;
  pageSizeOptions = [5, 10, 20, 50];

  get filteredCustomers(): CustomerProfile[] {
    if (this.selectedStatusFilter === 'ACTIVE') {
      return this.customers.filter(c => (c.status || 'ACTIVE').toUpperCase() === 'ACTIVE');
    }
    if (this.selectedStatusFilter === 'SUSPENDED') {
      return this.customers.filter(c => {
        const s = (c.status || '').toUpperCase();
        return s === 'SUSPENDED' || s === 'BLOCKED';
      });
    }
    return this.customers;
  }

  get totalPages(): number {
    const total = this.filteredCustomers.length;
    const size = Number(this.pageSize) || 10;
    return Math.max(1, Math.ceil(total / size));
  }

  get paginatedCustomers(): CustomerProfile[] {
    const p = Number(this.currentPage) || 1;
    const size = Number(this.pageSize) || 10;
    const startIndex = (p - 1) * size;
    return this.filteredCustomers.slice(startIndex, startIndex + size);
  }

  get paginationStartIndex(): number {
    if (this.filteredCustomers.length === 0) return 0;
    const p = Number(this.currentPage) || 1;
    const size = Number(this.pageSize) || 10;
    return (p - 1) * size + 1;
  }

  get paginationEndIndex(): number {
    const p = Number(this.currentPage) || 1;
    const size = Number(this.pageSize) || 10;
    return Math.min(p * size, this.filteredCustomers.length);
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

  get countAll(): number {
    return this.customers.length;
  }

  get countActive(): number {
    return this.customers.filter(c => (c.status || 'ACTIVE').toUpperCase() === 'ACTIVE').length;
  }

  get countSuspended(): number {
    return this.customers.filter(c => {
      const s = (c.status || '').toUpperCase();
      return s === 'SUSPENDED' || s === 'BLOCKED';
    }).length;
  }

  setStatusFilter(filter: 'ALL' | 'ACTIVE' | 'SUSPENDED'): void {
    this.selectedStatusFilter = filter;
    this.currentPage = 1;
    this.cdr.markForCheck();
  }

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.customerService.getAllCustomersAdmin().pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.customers = res.data || [];
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.error('Failed to load customers.');
        this.cdr.markForCheck();
      }
    });
  }

  viewPdf(): void {
    this.generatingPdf = true;
    this.cdr.markForCheck();

    this.customerService.downloadCustomersPdf(this.selectedStatusFilter).pipe(
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
        this.toast.error('Failed to generate passenger details PDF report.');
      }
    });
  }

  downloadPdf(): void {
    this.generatingPdf = true;
    this.cdr.markForCheck();

    this.customerService.downloadCustomersPdf(this.selectedStatusFilter).pipe(
      finalize(() => {
        this.generatingPdf = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (blob: Blob) => {
        const fileUrl = URL.createObjectURL(blob);
        const a = document.createElement('a');
        const todayStr = new Date().toISOString().slice(0, 10);
        a.href = fileUrl;
        a.download = `Passenger-Details-Report-${this.selectedStatusFilter}-${todayStr}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        setTimeout(() => URL.revokeObjectURL(fileUrl), 10000);
        this.toast.success('Passenger details report downloaded successfully.');
      },
      error: () => {
        this.toast.error('Failed to download passenger details PDF report.');
      }
    });
  }

  toggleStatus(c: CustomerProfile, newStatus: 'ACTIVE' | 'SUSPENDED'): void {
    this.customerService.updateCustomerStatus(c.id, newStatus).subscribe({
      next: () => {
        this.toast.success(`Customer status updated to ${newStatus}`);
        this.loadCustomers();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to update status.');
        this.cdr.markForCheck();
      }
    });
  }
}
