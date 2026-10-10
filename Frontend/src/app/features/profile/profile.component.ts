import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { CustomerService } from '../../core/services/customer.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { CustomerProfile } from '../../core/models/customer.models';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslatePipe],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  protected authService = inject(AuthService);
  private customerService = inject(CustomerService);
  private toast = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  activeTab: 'DETAILS' | 'SECURITY' = 'DETAILS';
  profile: CustomerProfile | null = null;

  savingProfile = false;
  savingPassword = false;

  profileForm: FormGroup = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(2)]],
    mobile: ['', [Validators.pattern('^([0-9+\\- ]{7,15})?$')]],
    gender: ['MALE'],
    dateOfBirth: ['1995-06-20'],
    address: ['']
  });

  passwordForm: FormGroup = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', Validators.required]
  });

  resetMode: 'CHANGE' | 'OTP' = 'CHANGE';
  sendingOtp = false;
  resettingOtp = false;
  otpSent = false;

  otpForm: FormGroup = this.fb.group({
    otp: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(8)]],
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', Validators.required]
  });

  ngOnInit(): void {
    this.loadProfile();
  }

  get initials(): string {
    const name = this.profile?.fullName || this.authService.currentUser()?.fullName || 'User';
    return name.split(' ').filter(n => n.length > 0).map(n => n[0]).join('').toUpperCase().substring(0, 2) || 'U';
  }

  loadProfile(): void {
    const currUser = this.authService.currentUser();
    this.customerService.getProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        if (!this.profile.email && currUser?.email) {
          this.profile.email = currUser.email;
        }
        this.profileForm.patchValue({
          fullName: profile.fullName || currUser?.fullName || '',
          mobile: profile.mobile || '',
          gender: profile.gender || 'MALE',
          dateOfBirth: profile.dateOfBirth || '1995-06-20',
          address: profile.address || ''
        });
        this.cdr.markForCheck();
      },
      error: () => {
        if (currUser) {
          this.profile = {
            id: 0,
            userId: currUser.id,
            fullName: currUser.fullName,
            email: currUser.email,
            mobile: null,
            address: null,
            gender: 'MALE',
            dateOfBirth: '1995-06-20',
            status: 'ACTIVE'
          };
          this.profileForm.patchValue({
            fullName: currUser.fullName || '',
            mobile: '',
            gender: 'MALE',
            dateOfBirth: '1995-06-20',
            address: ''
          });
        }
        this.cdr.markForCheck();
      }
    });
  }

  onUpdateProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      const nameCtrl = this.profileForm.get('fullName');
      const mobileCtrl = this.profileForm.get('mobile');
      if (nameCtrl?.invalid) {
        this.toast.warning('Please enter a valid full name (at least 2 characters).');
      } else if (mobileCtrl?.invalid) {
        this.toast.warning('Please enter a valid mobile number (7 to 15 digits).');
      } else {
        this.toast.warning('Please check all required profile fields.');
      }
      return;
    }

    this.savingProfile = true;
    const formVal = this.profileForm.value;
    const payload = {
      fullName: formVal.fullName ? formVal.fullName.trim() : '',
      mobile: formVal.mobile ? formVal.mobile.trim() : '',
      gender: formVal.gender || 'MALE',
      dateOfBirth: formVal.dateOfBirth || null,
      address: formVal.address ? formVal.address.trim() : ''
    };

    this.customerService.updateProfile(payload).pipe(
      finalize(() => {
        this.savingProfile = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (res) => {
        this.profile = res.data;
        const currentUser = this.authService.currentUser();
        if (currentUser && res.data) {
          const updatedUser = {
            ...currentUser,
            fullName: res.data.fullName
          };
          this.authService.updateCurrentUser(updatedUser);
        }
        this.toast.success('Profile updated successfully!');
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to update profile.');
        this.cdr.markForCheck();
      }
    });
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) return;

    const { currentPassword, newPassword, confirmPassword } = this.passwordForm.value;
    if (newPassword !== confirmPassword) {
      this.toast.warning('New password and confirmation do not match.');
      return;
    }

    this.savingPassword = true;
    this.authService.changePassword({ currentPassword, newPassword }).pipe(
      finalize(() => {
        this.savingPassword = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.toast.success('Password updated successfully!');
        this.passwordForm.reset();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Incorrect current password.');
      }
    });
  }

  sendResetOtp(): void {
    const email = this.profile?.email || this.authService.currentUser()?.email;
    if (!email) {
      this.toast.error('No email found for account.');
      return;
    }
    this.sendingOtp = true;
    this.authService.forgotPassword({ email }).pipe(
      finalize(() => {
        this.sendingOtp = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.otpSent = true;
        this.toast.success('Reset OTP has been sent to your registered email!');
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to send reset OTP. Please try again.');
      }
    });
  }

  onResetWithOtp(): void {
    if (this.otpForm.invalid) return;
    const email = this.profile?.email || this.authService.currentUser()?.email;
    if (!email) {
      this.toast.error('No email found for account.');
      return;
    }
    const { otp, newPassword, confirmPassword } = this.otpForm.value;
    if (newPassword !== confirmPassword) {
      this.toast.warning('New password and confirmation do not match.');
      return;
    }

    this.resettingOtp = true;
    this.authService.resetPassword({ email, otp, newPassword }).pipe(
      finalize(() => {
        this.resettingOtp = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.toast.success('Password reset successfully with OTP! You can now use your new password.');
        this.otpForm.reset();
        this.otpSent = false;
        this.resetMode = 'CHANGE';
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Invalid or expired OTP. Please verify and retry.');
      }
    });
  }
}

