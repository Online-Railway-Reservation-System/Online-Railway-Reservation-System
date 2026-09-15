package com.railway.inventoryquota.repository;

import com.railway.inventoryquota.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    List<Coach> findByTrainIdAndActiveStatusTrue(Long trainId);
    List<Coach> findByTrainIdAndClassTypeAndActiveStatusTrue(Long trainId, String classType);
}