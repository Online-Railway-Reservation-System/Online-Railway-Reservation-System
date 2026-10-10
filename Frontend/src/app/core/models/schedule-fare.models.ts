export interface TrainSchedule {
  id: number;
  trainId: number;
  departureStationCode: string;
  arrivalStationCode: string;
  departureTime: string;
  arrivalTime: string;
  runningDays: string;
  durationHours: number;
  validFrom?: string;
  validUpto?: string;
  activeStatus?: boolean;
  stationCode?: string;
  dayNumber?: number;
}

export interface TrainScheduleRequest {
  trainId: number;
  departureStationCode: string;
  arrivalStationCode: string;
  departureTime: string;
  arrivalTime: string;
  runningDays: string;
  durationHours: number;
  validFrom?: string;
  validUpto?: string;
  activeStatus?: boolean;
}

export interface FareCalculationRequest {
  trainId: number;
  sourceStationCode: string;
  destinationStationCode: string;
  classType: string;
  quota?: string;
  passengerCount?: number;
  distanceKm?: number;
}

export interface FareCalculationResponse {
  trainId: number;
  classType: string;
  quota: string;
  distanceKm: number;
  passengerCount: number;
  baseFare: number;
  concessionDiscount: number;
  reservationFee: number;
  gstTax?: number;
  tatkalSurcharge: number;
  finalPayableFare: number;
}

export interface TatkalConfig {
  id?: number;
  trainId: number;
  advanceDays: number;
  acOpeningTime: string;
  nonAcOpeningTime: string;
  surchargePercentage: number;
  maxSurchargeAmount?: number;
}

export interface FareRule {
  id?: number;
  trainId?: number;
  classType: string;
  baseFarePerKm: number;
  minimumFare: number;
  reservationFee: number;
  superfastCharge: number;
}
