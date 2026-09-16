package com.railway.stationroute.repository;

import com.railway.stationroute.entity.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
    List<RouteStation> findByTrainIdOrderByStopSequenceAsc(Long trainId);
    Optional<RouteStation> findByTrainIdAndStationCode(Long trainId, String stationCode);
    void deleteByTrainId(Long trainId);
}