export interface SeatInventory {
  id?: number;
  seatName: string;
  seatNumber: string;
  coach: string;
  status: 'AVAILABLE' | 'HELD' | 'BOOKED' | 'CONFIRMED';
  berthType: 'LOWER' | 'MIDDLE' | 'UPPER' | 'SIDE_LOWER' | 'SIDE_UPPER';
  classType: string;
  quotaType?: string;
  openToAll?: boolean;
}

export interface SeatLayoutResponse {
  trainId: number;
  trainNumber: string;
  journeyDate: string;
  coach?: string;
  seats: SeatInventory[];
}

export type SeatLayout = SeatLayoutResponse;
export type SeatMapItem = SeatInventory;

export interface AvailabilityResponse {
  trainId: number;
  journeyDate: string;
  classType: string;
  quota: string;
  availableSeats: number;
  heldSeats: number;
  confirmedSeats: number;
  totalSeats: number;
  status: string;
  waitingListCount?: number;
  waitingListCode?: string;
  tatkalOpen?: boolean;
  generalAvailableSeats?: number;
  ladiesAvailableSeats?: number;
  disabilityAvailableSeats?: number;
  tatkalAvailableSeats?: number;
}

export interface Coach {
  id?: number;
  trainId: number;
  coachNumber: string;
  classType: string;
  totalSeats: number;
  activeStatus: boolean;
}
