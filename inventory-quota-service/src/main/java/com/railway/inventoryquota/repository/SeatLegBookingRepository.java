package com.railway.inventoryquota.repository;

import com.railway.inventoryquota.entity.SeatLegBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface SeatLegBookingRepository extends JpaRepository<SeatLegBooking, Long> {

    @Query("SELECT DISTINCT b.seatNumber FROM SeatLegBooking b " +
           "WHERE b.trainId = :trainId " +
           "AND b.journeyDate = :journeyDate " +
           "AND b.classType = :classType " +
           "AND b.quotaType = :quotaType " +
           "AND (b.status = 'CONFIRMED' OR (b.status = 'HELD' AND b.holdExpiry > :now)) " +
           "AND b.fromStopSeq < :toSeq " +
           "AND b.toStopSeq > :fromSeq")
    Set<String> findOccupiedSeatNumbers(
            @Param("trainId") Long trainId,
            @Param("journeyDate") LocalDate journeyDate,
            @Param("classType") String classType,
            @Param("quotaType") String quotaType,
            @Param("fromSeq") Integer fromSeq,
            @Param("toSeq") Integer toSeq,
            @Param("now") LocalDateTime now
    );

    List<SeatLegBooking> findByHoldReference(String holdReference);

    List<SeatLegBooking> findByPnr(String pnr);

    @Query("SELECT b FROM SeatLegBooking b WHERE b.status = 'HELD' AND b.holdExpiry < :now")
    List<SeatLegBooking> findExpiredHolds(@Param("now") LocalDateTime now);
}
