export interface Station {
  id: number;
  stationCode: string;
  stationName: string;
  city: string;
  state: string;
  activeStatus: boolean;
}

export interface StationRequest {
  stationCode: string;
  stationName: string;
  city: string;
  state: string;
  activeStatus: boolean;
}

export interface RouteStop {
  id: number;
  trainId: number;
  stationId: number;
  stationCode: string;
  stationName: string;
  stopSequence: number;
  distanceFromOriginKm: number;
  haltDurationMinutes?: number;
}

export interface RouteStopRequest {
  trainId: number;
  stationId?: number;
  stationCode: string;
  stationName: string;
  stopSequence: number;
  distanceFromOriginKm: number;
  haltDurationMinutes?: number;
  sourceStation?: boolean;
  destinationStation?: boolean;
}
