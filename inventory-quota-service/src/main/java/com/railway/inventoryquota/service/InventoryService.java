package com.railway.inventoryquota.service;

import com.railway.inventoryquota.dto.*;
import com.railway.inventoryquota.entity.SeatInventory;

import java.time.LocalDate;
import java.util.List;

public interface InventoryService {
    AvailabilityResponse getAvailability(Long trainId, LocalDate journeyDate, String classType, String quota);
    AvailabilityResponse getAvailability(Long trainId, LocalDate journeyDate, String classType, String quota,
                                         String fromStationCode, String toStationCode, Integer fromStopSeq, Integer toStopSeq);
    List<SeatInventory> getSeatsLayout(Long trainId, LocalDate journeyDate, String classType);
    SeatHoldResponse holdSeats(SeatHoldRequest request);
    SeatConfirmResponse confirmSeats(SeatConfirmRequest request);
    SeatReleaseResponse releaseSeats(SeatReleaseRequest request);
    void releaseExpiredHolds();
}