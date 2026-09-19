package com.railway.stationroute.service;

import com.railway.stationroute.dto.RouteStationRequest;
import com.railway.stationroute.dto.RouteStationResponse;
import com.railway.stationroute.entity.RouteStation;
import com.railway.stationroute.exception.ResourceNotFoundException;
import com.railway.stationroute.repository.RouteStationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteStationRepository routeStationRepository;

    public RouteServiceImpl(RouteStationRepository routeStationRepository) {
        this.routeStationRepository = routeStationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteStationResponse> getRouteByTrainId(Long trainId) {
        return routeStationRepository.findByTrainIdOrderByStopSequenceAsc(trainId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RouteStationResponse addRouteStop(RouteStationRequest request) {
        RouteStation stop = new RouteStation();
        stop.setTrainId(request.getTrainId());
        stop.setStationCode(request.getStationCode().trim().toUpperCase());
        stop.setStationName(request.getStationName().trim());
        stop.setStopSequence(request.getStopSequence());
        stop.setDistanceFromOriginKm(request.getDistanceFromOriginKm());
        stop.setSourceStation(request.isSourceStation());
        stop.setDestinationStation(request.isDestinationStation());

        RouteStation saved = routeStationRepository.save(stop);
        return mapToDto(saved);
    }

    @Override
    public RouteStationResponse updateRouteStop(Long id, RouteStationRequest request) {
        RouteStation stop = routeStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route stop not found with id: " + id));

        stop.setTrainId(request.getTrainId());
        stop.setStationCode(request.getStationCode().trim().toUpperCase());
        stop.setStationName(request.getStationName().trim());
        stop.setStopSequence(request.getStopSequence());
        stop.setDistanceFromOriginKm(request.getDistanceFromOriginKm());
        stop.setSourceStation(request.isSourceStation());
        stop.setDestinationStation(request.isDestinationStation());

        RouteStation saved = routeStationRepository.save(stop);
        return mapToDto(saved);
    }

    @Override
    public void deleteRouteStop(Long id) {
        RouteStation stop = routeStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route stop not found with id: " + id));
        routeStationRepository.delete(stop);
    }

    @Override
    public List<RouteStationResponse> saveAllRouteStops(Long trainId, List<RouteStationRequest> stops) {
        routeStationRepository.deleteByTrainId(trainId);
        if (stops == null || stops.isEmpty()) {
            return List.of();
        }

        List<RouteStation> entities = new java.util.ArrayList<>();
        for (int i = 0; i < stops.size(); i++) {
            RouteStationRequest req = stops.get(i);
            RouteStation stop = new RouteStation();
            stop.setTrainId(trainId);
            stop.setStationCode(req.getStationCode().trim().toUpperCase());
            stop.setStationName(req.getStationName() != null ? req.getStationName().trim() : req.getStationCode());
            stop.setStopSequence(i + 1);
            stop.setDistanceFromOriginKm(req.getDistanceFromOriginKm() != null ? req.getDistanceFromOriginKm() : 0.0);
            stop.setSourceStation(i == 0);
            stop.setDestinationStation(i == stops.size() - 1);
            entities.add(stop);
        }

        List<RouteStation> saved = routeStationRepository.saveAll(entities);
        return saved.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private RouteStationResponse mapToDto(RouteStation r) {
        return new RouteStationResponse(
                r.getId(),
                r.getTrainId(),
                r.getStationCode(),
                r.getStationName(),
                r.getStopSequence(),
                r.getDistanceFromOriginKm(),
                r.isSourceStation(),
                r.isDestinationStation()
        );
    }
}