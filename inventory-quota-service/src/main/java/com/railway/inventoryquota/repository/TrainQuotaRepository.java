package com.railway.inventoryquota.repository;

import com.railway.inventoryquota.entity.TrainQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainQuotaRepository extends JpaRepository<TrainQuota, Long> {
    List<TrainQuota> findByTrainId(Long trainId);
    Optional<TrainQuota> findByTrainIdAndClassTypeAndQuotaType(Long trainId, String classType, String quotaType);
}