export interface FoodMenuItem {
  id: number;
  itemName: string;
  description: string;
  category: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACKS' | 'BEVERAGES';
  price: number;
  available: boolean;
}

export interface FoodItemSelection {
  foodMenuId: number;
  quantity: number;
  itemName?: string;
  price?: number;
}

export interface FoodOrderItem {
  id: number;
  foodMenuId: number;
  itemName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface FoodOrderResponse {
  id: number;
  reservationId: number;
  pnr: string;
  totalAmount: number;
  status: string;
  items: FoodOrderItem[];
}
