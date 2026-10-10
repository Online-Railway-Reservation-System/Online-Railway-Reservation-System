import { FoodItemSelection, FoodOrderResponse } from './food.models';

export interface PassengerRequest {
  name: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  age: number;
  address?: string;
  seatPreference?: 'LOWER' | 'MIDDLE' | 'UPPER' | 'SIDE_LOWER' | 'SIDE_UPPER' | 'NO_PREFERENCE';
  concessionType?: 'DISABILITY' | 'GOVERNMENT_STAFF' | 'SENIOR_CITIZEN' | null;
  concessionNumber?: string | null;
  seatNumber?: string;
  coachNumber?: string;
  ladiesQuotaPreference?: boolean;
  allocatedQuota?: string;
}

export interface ReservationRequest {
  customerId?: number;
  customerEmail?: string;
  trainId: number;
  sourceStationCode: string;
  destinationStationCode: string;
  journeyDate: string; // YYYY-MM-DD
  classType: string;
  quota?: string;
  passengers: PassengerRequest[];
  foodItems?: FoodItemSelection[];
  paymentMethod?: 'CREDIT_CARD' | 'DEBIT_CARD' | 'UPI' | 'NET_BANKING' | string;
}

export interface PassengerResponse {
  id: number;
  name: string;
  gender: string;
  age: number;
  seatPreference: string;
  seatNumber: string;
  berthType: string;
  status: string;
  concessionType?: string;
  waitingListCode?: string;
  waitingListNumber?: number;
  allocatedQuota?: string;
  ladiesQuotaPreference?: boolean;
}

export interface ReservationResponse {
  id: number;
  pnr: string;
  customerId: number;
  customerEmail: string;
  trainId: number;
  trainNumber: string;
  trainName: string;
  sourceStationCode: string;
  destinationStationCode: string;
  journeyDate: string;
  classType: string;
  quota: string;
  passengerCount: number;
  baseFare: number;
  concessionDiscount: number;
  reservationFee: number;
  gstTax?: number;
  ticketFare: number;
  foodTotal: number;
  finalAmount: number;
  status: 'CONFIRMED' | 'PARTIALLY_CONFIRMED' | 'CANCELLED' | 'PENDING' | 'FAILED' | 'WAITING_LIST' | 'WAITING_LIST_EXPIRED' | string;
  waitingListCode?: string;
  waitingListNumber?: number;
  paymentId?: number;
  passengers: PassengerResponse[];
  foodOrder?: FoodOrderResponse;
  createdAt: string;
  totalFare: number;
}

export interface TicketResponse {
  id: number;
  reservationId: number;
  pnr: string;
  trainNumber: string;
  trainName: string;
  journeyDate: string;
  sourceStation: string;
  destinationStation: string;
  departureTime: string;
  arrivalTime: string;
  baseFare: number;
  concessionDiscount: number;
  reservationFee: number;
  gstTax?: number;
  foodTotal: number;
  totalAmount: number;
  status: 'CONFIRMED' | 'PARTIALLY_CONFIRMED' | 'CANCELLED' | 'WAITING_LIST' | 'WAITING_LIST_EXPIRED' | string;
  waitingListCode?: string;
  waitingListNumber?: number;
  passengers: PassengerResponse[];
  bookedAt: string;
}

export interface TrainDelayNotificationRequest {
  trainNumber: string;
  journeyDate: string; // YYYY-MM-DD
  delayTime: string;   // e.g. "1 hour 30 mins", "45 mins"
  reason?: string;
}

export interface TrainDelayNotificationResponse {
  trainNumber: string;
  journeyDate: string;
  delayTime: string;
  passengersNotified: number;
  notifiedPnrs: string[];
  message?: string;
}

export interface CancellationRequest {
  passengerIds?: number[];
  reason?: string;
}

export interface CancellationPreviewResponse {
  pnr: string;
  reservationId: number;
  originalTotalAmount: number;
  totalPassengers: number;
  selectedPassengerCount: number;
  perPassengerFare: number;
  cancellationChargePerPassenger: number;
  refundPerPassenger: number;
  totalCancellationCharge: number;
  totalRefundAmount: number;
  refundPercentage: number;
  deductionPolicyNote: string;
  fullCancellation: boolean;
  passengerDetails: PassengerResponse[];
}
