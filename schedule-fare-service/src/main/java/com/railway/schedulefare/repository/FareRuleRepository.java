package com.railway.schedulefare.repository;

import com.railway.schedulefare.entity.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FareRuleRepository extends JpaRepository<FareRule, Long> {
    List<FareRule> findByTrainId(Long trainId);
    Optional<FareRule> findByTrainIdAndClassType(Long trainId, String classType);
    Optional<FareRule> findTopByClassTypeAndTrainIdIsNull(String classType);
}