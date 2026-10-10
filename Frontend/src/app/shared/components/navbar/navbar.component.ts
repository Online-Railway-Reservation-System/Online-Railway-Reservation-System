import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { LanguageService } from '../../../core/services/language.service';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, TranslatePipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  authService = inject(AuthService);
  langService = inject(LanguageService);
  private router = inject(Router);

  pnrInput: string = '';
  mobileMenuOpen: boolean = false;

  get userInitials(): string {
    const name = this.authService.currentUser()?.fullName;
    if (!name) return 'PR';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  }

  checkPnr() {
    if (this.pnrInput && this.pnrInput.trim().length >= 5) {
      const pnr = this.pnrInput.trim();
      this.pnrInput = '';
      this.mobileMenuOpen = false;
      this.router.navigate(['/tickets', pnr]);
    }
  }

  logout() {
    this.mobileMenuOpen = false;
    this.authService.logout();
  }
}
