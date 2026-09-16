package com.railway.stationroute.service;

import com.railway.stationroute.dto.StationRequest;
import com.railway.stationroute.dto.StationResponse;

import java.util.List;

public interface StationService {
    List<StationResponse> getAllStations();
    StationResponse getStationById(Long id);
    StationResponse getStationByCode(String code);
    StationResponse createStation(StationRequest request);
    StationResponse updateStation(Long id, StationRequest request);
    void deleteStation(Long id);
}