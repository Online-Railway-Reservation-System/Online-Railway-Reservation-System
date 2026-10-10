import { Component, OnInit, inject, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FoodService } from '../../core/services/food.service';
import { FoodMenuItem } from '../../core/models/food.models';
import { PkrCurrencyPipe } from '../../shared/pipes/currency.pipe';
import { TranslatePipe } from '../../shared/pipes/translate.pipe';
@Component({
  selector: 'app-food-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, PkrCurrencyPipe, TranslatePipe],
  templateUrl: './food-admin.component.html',
  styleUrls: ['./food-admin.component.css']
})
export class FoodAdminComponent implements OnInit {
  private foodService = inject(FoodService);
  private cdr = inject(ChangeDetectorRef);

  menuItems = signal<FoodMenuItem[]>([]);
  filteredItems = signal<FoodMenuItem[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Filters
  searchTerm = '';
  selectedCategory = 'ALL';
  categories = ['ALL', 'BREAKFAST', 'LUNCH', 'DINNER', 'SNACKS', 'BEVERAGES'];

  // Modal State
  showModal = false;
  isEditing = false;
  currentItem: Partial<FoodMenuItem> = {
    itemName: '',
    description: '',
    category: 'LUNCH',
    price: 0,
    available: true
  };

  ngOnInit(): void {
    this.loadMenuItems();
  }

  loadMenuItems(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.foodService.getAllMenuItems().subscribe({
      next: (res) => {
        const items = res.data || [];
        this.menuItems.set(items);
        this.applyFilter();
        this.loading.set(false);
        this.cdr.markForCheck();
      },
      error: () => {
        // Fallback to customer menu if admin endpoint encounters any issue
        this.foodService.getAvailableMenu().subscribe({
          next: (res) => {
            const items = res.data || [];
            this.menuItems.set(items);
            this.applyFilter();
            this.loading.set(false);
            this.cdr.markForCheck();
          },
          error: (err) => {
            this.errorMessage.set('Failed to load catering menu items: ' + (err.error?.message || err.message));
            this.loading.set(false);
            this.cdr.markForCheck();
          }
        });
      }
    });
  }

  applyFilter(): void {
    let list = this.menuItems();

    if (this.selectedCategory !== 'ALL') {
      list = list.filter(item => item.category === this.selectedCategory);
    }

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      list = list.filter(item =>
        item.itemName.toLowerCase().includes(term) ||
        (item.description && item.description.toLowerCase().includes(term))
      );
    }

    this.filteredItems.set(list);
    this.cdr.markForCheck();
  }

  openAddModal(): void {
    this.isEditing = false;
    this.currentItem = {
      itemName: '',
      description: '',
      category: 'LUNCH',
      price: 150,
      available: true
    };
    this.showModal = true;
  }

  openEditModal(item: FoodMenuItem): void {
    this.isEditing = true;
    this.currentItem = { ...item };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveMenuItem(): void {
    if (!this.currentItem.itemName?.trim() || !this.currentItem.price) {
      this.errorMessage.set('Please enter a valid item name and price.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    if (this.isEditing && this.currentItem.id) {
      this.foodService.updateMenuItem(this.currentItem.id, this.currentItem).subscribe({
        next: () => {
          this.showSuccess('Catering menu item updated successfully!');
          this.closeModal();
          this.loadMenuItems();
        },
        error: (err) => {
          this.errorMessage.set('Failed to update menu item: ' + (err.error?.message || err.message));
          this.loading.set(false);
          this.cdr.markForCheck();
        }
      });
    } else {
      this.foodService.createMenuItem(this.currentItem).subscribe({
        next: () => {
          this.showSuccess('New catering menu item added successfully!');
          this.closeModal();
          this.loadMenuItems();
        },
        error: (err) => {
          this.errorMessage.set('Failed to create menu item: ' + (err.error?.message || err.message));
          this.loading.set(false);
          this.cdr.markForCheck();
        }
      });
    }
  }

  toggleAvailability(item: FoodMenuItem): void {
    const newStatus = !item.available;
    this.foodService.updateAvailability(item.id, newStatus).subscribe({
      next: () => {
        item.available = newStatus;
        this.showSuccess(`"${item.itemName}" is now ${newStatus ? 'AVAILABLE' : 'UNAVAILABLE'}.`);
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage.set('Failed to toggle availability: ' + (err.error?.message || err.message));
        this.cdr.markForCheck();
      }
    });
  }

  deleteItem(item: FoodMenuItem): void {
    if (!confirm(`Are you sure you want to remove "${item.itemName}" from the menu?`)) {
      return;
    }

    this.foodService.deleteMenuItem(item.id).subscribe({
      next: () => {
        this.showSuccess(`"${item.itemName}" removed from catering menu.`);
        this.menuItems.set(this.menuItems().filter(i => i.id !== item.id));
        this.cdr.markForCheck();
        this.loadMenuItems();
      },
      error: (err) => {
        this.errorMessage.set('Failed to delete menu item: ' + (err.error?.message || err.message));
        this.cdr.markForCheck();
      }
    });
  }

  private showSuccess(msg: string): void {
    this.successMessage.set(msg);
    setTimeout(() => {
      this.successMessage.set(null);
      this.cdr.markForCheck();
    }, 4000);
  }
}
