package com.railway.stationroute.service;

import com.railway.stationroute.dto.RouteStationRequest;
import com.railway.stationroute.dto.RouteStationResponse;

import java.util.List;

public interface RouteService {
    List<RouteStationResponse> getRouteByTrainId(Long trainId);
    RouteStationResponse addRouteStop(RouteStationRequest request);
    RouteStationResponse updateRouteStop(Long id, RouteStationRequest request);
    void deleteRouteStop(Long id);
    List<RouteStationResponse> saveAllRouteStops(Long trainId, List<RouteStationRequest> stops);
}