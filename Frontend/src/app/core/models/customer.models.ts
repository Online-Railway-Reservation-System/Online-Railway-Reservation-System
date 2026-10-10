export interface CustomerProfile {
  id: number;
  userId: number;
  fullName: string;
  email: string;
  mobile: string | null;
  address: string | null;
  gender: string | null;
  dateOfBirth: string | null;
  status: 'ACTIVE' | 'SUSPENDED' | 'BLOCKED' | string;
}

export interface CustomerProfileRequest {
  fullName: string;
  mobile?: string;
  address?: string;
  gender?: string;
  dateOfBirth?: string;
}

export interface ConcessionCard {
  id: number;
  customerId: number;
  concessionType: 'DISABILITY' | 'GOVERNMENT_STAFF' | 'SENIOR_CITIZEN';
  cardNumber: string;
  documentUrl?: string;
  verified: boolean;
  validUntil?: string;
}

export type Concession = ConcessionCard;

export interface ConcessionRequest {
  concessionType: string;
  cardNumber: string;
  documentUrl?: string;
}

export interface SupportQuery {
  id: number;
  customerId: number;
  customerEmail: string;
  subject: string;
  category: string;
  description?: string;
  message?: string;
  pnr?: string;
  status: 'OPEN' | 'RESOLVED' | 'IN_PROGRESS';
  adminReply?: string;
  replyMessage?: string;
  repliedBy?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface CustomerQueryRequest {
  subject: string;
  category: string;
  description?: string;
  message?: string;
  pnr?: string;
}

export interface AdminReplyRequest {
  replyMessage?: string;
  adminReply?: string;
}
