export interface PaymentRequest {
  reservationId: number;
  customerId?: number;
  amount: number;
  paymentMethod: string;
  idempotencyKey?: string;
}

export interface PaymentResponse {
  id: number;
  reservationId: number;
  customerId: number;
  amount: number;
  paymentMethod: string;
  status: 'SUCCESS' | 'FAILED';
  transactionReference: string;
  paidAt: string;
}

export interface RefundCalculateRequest {
  reservationId: number;
  pnr: string;
  journeyDateTime: string;
  bookingAmount: number;
  classType: string;
}

export interface RefundCalculateResponse {
  reservationId: number;
  pnr: string;
  bookingAmount: number;
  cancellationCharge: number;
  refundAmount: number;
  refundPercentage: number;
  hoursBeforeDeparture: number;
  ruleApplied: string;
}

export type RefundCalculationResponse = RefundCalculateResponse;

export interface RefundResponse {
  id: number;
  reservationId: number;
  pnr: string;
  paymentTransactionId: number;
  bookingAmount: number;
  cancellationCharge: number;
  refundAmount: number;
  refundPercentage: number;
  status: 'PROCESSED' | 'PENDING' | 'REJECTED';
  processedAt: string;
}
