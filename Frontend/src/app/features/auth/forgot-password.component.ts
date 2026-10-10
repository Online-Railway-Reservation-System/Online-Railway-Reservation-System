import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, TranslatePipe],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  step = 1;
  loading = false;
  sentEmail = '';

  requestOtpForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  resetPasswordForm: FormGroup = this.fb.group({
    otp: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(6)]],
    newPassword: ['', [Validators.required, Validators.minLength(6)]]
  });

  onRequestOtp(): void {
    if (this.requestOtpForm.invalid) return;

    this.loading = true;
    this.cdr.markForCheck();
    const email = this.requestOtpForm.value.email.trim().toLowerCase();

    this.authService.forgotPassword({ email })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (res: any) => {
          this.sentEmail = email;
          this.step = 2;
          this.toast.success(res.message || 'OTP dispatched to your email address.');
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.toast.error(err.error?.message || 'Failed to dispatch OTP. Please check email address.');
          this.cdr.markForCheck();
        }
      });
  }

  onResetPassword(): void {
    if (this.resetPasswordForm.invalid) return;

    this.loading = true;
    this.cdr.markForCheck();
    const { otp, newPassword } = this.resetPasswordForm.value;

    this.authService.resetPassword({ email: this.sentEmail, otp: otp.trim(), newPassword })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: (res: any) => {
          this.toast.success(res.message || 'Password reset successfully! You can now log in.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.toast.error(err.error?.message || 'Invalid OTP or failed to update password.');
          this.cdr.markForCheck();
        }
      });
  }
}
