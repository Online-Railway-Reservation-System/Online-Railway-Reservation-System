import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  title?: string;
  message: string;
  durationMs?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toasts = signal<ToastMessage[]>([]);

  show(type: 'success' | 'error' | 'info' | 'warning', message: string, title?: string, durationMs: number = 4000) {
    const id = Math.random().toString(36).substring(2, 9);
    const toast: ToastMessage = { id, type, title, message, durationMs };

    this.toasts.update(current => [...current, toast]);

    if (durationMs > 0) {
      setTimeout(() => {
        this.remove(id);
      }, durationMs);
    }
  }

  success(message: string, title: string = 'Success') {
    this.show('success', message, title);
  }

  error(message: string, title: string = 'Error') {
    this.show('error', message, title, 6000);
  }

  info(message: string, title: string = 'Notice') {
    this.show('info', message, title);
  }

  warning(message: string, title: string = 'Warning') {
    this.show('warning', message, title, 5000);
  }

  remove(id: string) {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }
}
