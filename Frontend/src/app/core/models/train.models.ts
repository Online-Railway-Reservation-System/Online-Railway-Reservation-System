export interface Train {
  id: number;
  trainNumber: string;
  trainName: string;
  trainType: 'PREMIUM' | 'SUPERFAST' | 'EXPRESS' | 'PASSENGER';
  activeStatus: boolean;
}

export interface TrainRequest {
  trainNumber: string;
  trainName: string;
  trainType: string;
  activeStatus: boolean;
}

export interface JourneyLeg {
  trainId: number;
  trainNumber: string;
  trainName: string;
  trainType: string;
  source: string;
  destination: string;
  departureDate: string;
  departureTime: string;
  arrivalDate: string;
  arrivalTime: string;
  duration: string;
  distanceKm: number;
  classType: string;
  quota: string;
  fare: number;
  availableSeats: number;
  status: string;
  waitingListCode?: string;
  waitingListCount?: number;
  tatkalOpeningTime?: string;
  tatkalOpeningDate?: string;
  tatkalWindowOpen?: boolean;
  runningDays?: string;
  generalAvailableSeats?: number;
  ladiesAvailableSeats?: number;
  disabilityAvailableSeats?: number;
  tatkalAvailableSeats?: number;
  ladiesQuotaConfigured?: boolean;
  disabilityQuotaConfigured?: boolean;
}

export interface ConnectionInfo {
  station: string;
  stationName: string;
  arrivalDate: string;
  arrivalTime: string;
  departureDate: string;
  departureTime: string;
  waitingTimeMinutes: number;
  waitingTime: string;
  transferStationCode: string;
  transferStationName: string;
  waitingTimeFormatted: string;
}

export interface TrainSearchResult {
  trainId: number;
  trainNumber: string;
  trainName: string;
  trainType: string;
  source: string;
  destination: string;
  departure: string;
  arrival: string;
  duration: string;
  distanceKm: number;
  classType: string;
  quota: string;
  fare: number;
  availableSeats: number;
  status: 'AVAILABLE' | 'RAC' | 'WL' | string;
  bookingOpen?: boolean;
  departed?: boolean;
  type?: 'DIRECT' | 'CONNECTING';
  journeyDate?: string;
  arrivalDate?: string;
  runningDays?: string;
  legs?: JourneyLeg[];
  connection?: ConnectionInfo;
  totalJourneyDuration?: string;
  tatkalWindowOpen?: boolean;
  tatkalOpeningTime?: string;
  tatkalOpeningDate?: string;
  tatkalWindowStatus?: string;
  waitingListCode?: string;
  waitingListCount?: number;
  generalAvailableSeats?: number;
  ladiesAvailableSeats?: number;
  disabilityAvailableSeats?: number;
  tatkalAvailableSeats?: number;
  ladiesQuotaConfigured?: boolean;
  disabilityQuotaConfigured?: boolean;
}

export interface TrainReadiness {
  trainId: number;
  trainNumber: string;
  trainName: string;
  trainType: string;
  trainDetailsConfigured: boolean;
  routeConfigured: boolean;
  stopsConfigured: boolean;
  scheduleConfigured: boolean;
  fareConfigured: boolean;
  coachesConfigured: boolean;
  seatsConfigured: boolean;
  configurationStatus: 'READY_FOR_BOOKING' | 'CONFIGURATION_INCOMPLETE' | string;
  bookingStatus: 'ENABLED' | 'DISABLED' | string;
  readyForBooking: boolean;
  activeCoachCount?: number;
  totalAllocatedSeats?: number;
  totalCoaches?: number;
  totalSeats?: number;
  routeStopsCount?: number;
  missingPrerequisites: string[];
}
