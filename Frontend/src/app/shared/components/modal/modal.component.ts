import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.css'
})
export class ModalComponent {
  @Input() isOpen = false;
  @Input() title = '';
  @Input() maxWidth = '560px';
  @Input() cancelText = 'Cancel';
  @Input() confirmText = 'Confirm';
  @Input() isDestructive = false;
  @Input() showFooter = true;
  @Input() showConfirm = true;
  @Input() confirmDisabled = false;
  @Input() loading = false;
  @Input() closeOnBackdrop = true;

  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  onClose(): void {
    if (!this.loading) {
      this.close.emit();
    }
  }

  onConfirm(): void {
    if (!this.loading && !this.confirmDisabled) {
      this.confirm.emit();
    }
  }

  onBackdropClick(event: MouseEvent): void {
    if (this.closeOnBackdrop && !this.loading) {
      this.onClose();
    }
  }
}
