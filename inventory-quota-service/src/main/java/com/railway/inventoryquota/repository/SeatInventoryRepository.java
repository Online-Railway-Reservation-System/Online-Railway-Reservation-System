package com.railway.inventoryquota.repository;

import com.railway.inventoryquota.entity.SeatInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatInventory s WHERE s.trainId = :trainId AND s.journeyDate = :journeyDate AND s.classType = :classType AND s.quotaType = :quotaType AND s.status = 'AVAILABLE' ORDER BY s.id ASC")
    List<SeatInventory> findAvailableSeatsWithLock(
            @Param("trainId") Long trainId,
            @Param("journeyDate") LocalDate journeyDate,
            @Param("classType") String classType,
            @Param("quotaType") String quotaType,
            Pageable pageable
    );

    long countByTrainIdAndJourneyDateAndClassTypeAndQuotaTypeAndStatus(
            Long trainId, LocalDate journeyDate, String classType, String quotaType, String status
    );

    List<SeatInventory> findByHoldReference(String holdReference);

    List<SeatInventory> findByPnr(String pnr);

    List<SeatInventory> findByTrainIdAndJourneyDateAndClassType(Long trainId, LocalDate journeyDate, String classType);

    List<SeatInventory> findByTrainIdAndJourneyDateAndClassTypeAndQuotaTypeOrderBySeatNumberAsc(
            Long trainId, LocalDate journeyDate, String classType, String quotaType
    );

    long countByTrainIdAndJourneyDateAndClassTypeAndQuotaType(
            Long trainId, LocalDate journeyDate, String classType, String quotaType
    );

    @Query("SELECT s FROM SeatInventory s WHERE s.status = 'HELD' AND s.holdExpiry < :now")
    List<SeatInventory> findExpiredHolds(@Param("now") LocalDateTime now);
}