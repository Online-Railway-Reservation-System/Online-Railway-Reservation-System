package com.railway.schedulefare.repository;

import com.railway.schedulefare.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {
    List<TrainSchedule> findByTrainId(Long trainId);
    Optional<TrainSchedule> findTopByTrainIdAndActiveStatusTrue(Long trainId);
}