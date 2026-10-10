import { Injectable, signal, computed } from '@angular/core';
import { TrainSearchResult } from '../models/train.models';
import { SeatInventory } from '../models/inventory.models';
import { PassengerRequest } from '../models/reservation.models';
import { FoodItemSelection } from '../models/food.models';
import { FareCalculationResponse } from '../models/schedule-fare.models';

export interface JourneyContext {
  sourceStationCode: string;
  destinationStationCode: string;
  journeyDate: string;
  classType: string;
  quota: string;
  distanceKm?: number;
  baseFare?: number;
}

export interface BookingState {
  train: TrainSearchResult | null;
  trainId: number | null;
  trainNumber: string | null;
  trainName: string | null;
  sourceStationCode: string;
  destinationStationCode: string;
  journeyDate: string;
  classType: string;
  quota: string;
  distanceKm?: number;
  baseFare?: number;
  selectedSeats: SeatInventory[];
  passengers: PassengerRequest[];
  selectedFood: FoodItemSelection[];
  fareDetails: FareCalculationResponse | null;
  paymentMethod: string;
}

const initialState: BookingState = {
  train: null,
  trainId: null,
  trainNumber: null,
  trainName: null,
  sourceStationCode: '',
  destinationStationCode: '',
  journeyDate: '',
  classType: 'SL',
  quota: 'GENERAL',
  distanceKm: 178.0,
  baseFare: 891.0,
  selectedSeats: [],
  passengers: [],
  selectedFood: [],
  fareDetails: null,
  paymentMethod: 'CREDIT_CARD'
};

@Injectable({
  providedIn: 'root'
})
export class BookingStateService {
  private state = signal<BookingState>(initialState);

  readonly current = this.state.asReadonly();

  readonly hasTrainSelected = computed(() => !!this.state().trainId);
  readonly passengerCount = computed(() => this.state().passengers.length);
  readonly selectedSeats = computed(() => this.state().selectedSeats);
  readonly journeyContext = computed<JourneyContext>(() => ({
    sourceStationCode: this.state().sourceStationCode,
    destinationStationCode: this.state().destinationStationCode,
    journeyDate: this.state().journeyDate,
    classType: this.state().classType,
    quota: this.state().quota,
    distanceKm: this.state().distanceKm,
    baseFare: this.state().baseFare
  }));

  readonly foodTotal = computed(() => {
    return this.state().selectedFood.reduce((sum, item) => sum + ((item.price || 0) * (item.quantity || 1)), 0);
  });

  readonly grandTotal = computed(() => {
    const fare = this.state().fareDetails?.finalPayableFare || (this.state().train?.fare || 0) * Math.max(1, this.state().passengers.length);
    return fare + this.foodTotal();
  });

  setSelectedTrain(train: TrainSearchResult) {
    this.state.update(s => ({
      ...s,
      train,
      trainId: train.trainId,
      trainNumber: train.trainNumber,
      trainName: train.trainName,
      baseFare: train.fare
    }));
  }

  setJourneyContext(ctx: JourneyContext) {
    this.state.update(s => ({
      ...s,
      ...ctx
    }));
  }

  initBookingFromSearch(
    train: TrainSearchResult,
    source: string,
    destination: string,
    journeyDate: string,
    classType: string,
    quota: string
  ) {
    this.state.update(s => ({
      ...s,
      train,
      trainId: train.trainId,
      trainNumber: train.trainNumber,
      trainName: train.trainName,
      sourceStationCode: source,
      destinationStationCode: destination,
      journeyDate,
      classType,
      quota,
      selectedSeats: [],
      passengers: [],
      selectedFood: [],
      fareDetails: null
    }));
  }

  setSelectedSeats(seats: SeatInventory[]) {
    this.state.update(s => ({ ...s, selectedSeats: seats }));
  }

  setPassengers(passengers: PassengerRequest[]) {
    this.state.update(s => ({ ...s, passengers }));
  }

  setSelectedFood(food: FoodItemSelection[]) {
    this.state.update(s => ({ ...s, selectedFood: food }));
  }

  setFareDetails(fareDetails: FareCalculationResponse) {
    this.state.update(s => ({ ...s, fareDetails }));
  }

  setPaymentMethod(method: string) {
    this.state.update(s => ({ ...s, paymentMethod: method }));
  }

  clearBookingContext() {
    this.state.set(initialState);
  }

  resetBooking() {
    this.clearBookingContext();
  }
}
