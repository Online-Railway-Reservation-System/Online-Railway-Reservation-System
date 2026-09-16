package com.railway.stationroute.service;

import com.railway.stationroute.dto.StationRequest;
import com.railway.stationroute.dto.StationResponse;
import com.railway.stationroute.entity.Station;
import com.railway.stationroute.exception.DuplicateResourceException;
import com.railway.stationroute.exception.ResourceNotFoundException;
import com.railway.stationroute.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StationResponse> getAllStations() {
        return stationRepository.findByActiveStatusTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StationResponse getStationById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));
        return mapToDto(station);
    }

    @Override
    @Transactional(readOnly = true)
    public StationResponse getStationByCode(String code) {
        Station station = stationRepository.findByStationCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with code: " + code));
        return mapToDto(station);
    }

    @Override
    public StationResponse createStation(StationRequest request) {
        String code = request.getStationCode().trim().toUpperCase();
        if (stationRepository.existsByStationCode(code)) {
            throw new DuplicateResourceException("Station with code " + code + " already exists");
        }

        Station station = new Station();
        station.setStationCode(code);
        station.setStationName(request.getStationName().trim());
        station.setCity(request.getCity().trim());
        station.setState(request.getState().trim());
        station.setActiveStatus(request.isActiveStatus());

        Station saved = stationRepository.save(station);
        return mapToDto(saved);
    }

    @Override
    public StationResponse updateStation(Long id, StationRequest request) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));

        String code = request.getStationCode().trim().toUpperCase();
        if (!station.getStationCode().equalsIgnoreCase(code) && stationRepository.existsByStationCode(code)) {
            throw new DuplicateResourceException("Station with code " + code + " already exists");
        }

        station.setStationCode(code);
        station.setStationName(request.getStationName().trim());
        station.setCity(request.getCity().trim());
        station.setState(request.getState().trim());
        station.setActiveStatus(request.isActiveStatus());

        Station saved = stationRepository.save(station);
        return mapToDto(saved);
    }

    @Override
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));
        station.setActiveStatus(false);
        stationRepository.save(station);
    }

    private StationResponse mapToDto(Station s) {
        return new StationResponse(
                s.getId(),
                s.getStationCode(),
                s.getStationName(),
                s.getCity(),
                s.getState(),
                s.isActiveStatus()
        );
    }
}