package com.railway.stationroute.repository;

import com.railway.stationroute.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByStationCode(String stationCode);
    boolean existsByStationCode(String stationCode);
    List<Station> findByActiveStatusTrue();
}