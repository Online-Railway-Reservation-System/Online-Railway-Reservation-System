package com.railway.schedulefare.repository;

import com.railway.schedulefare.entity.TatkalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TatkalConfigRepository extends JpaRepository<TatkalConfig, Long> {
    Optional<TatkalConfig> findByTrainId(Long trainId);
}