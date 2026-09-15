package com.railway.reservation.repository;

import com.railway.reservation.entity.FoodMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodMenuRepository extends JpaRepository<FoodMenu, Long> {
    List<FoodMenu> findByAvailableTrue();
    List<FoodMenu> findByCategoryAndAvailableTrue(String category);
}