package com.railway.reservation.repository;

import com.railway.reservation.entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    Optional<FoodOrder> findByReservationId(Long reservationId);
    List<FoodOrder> findByPnr(String pnr);
}