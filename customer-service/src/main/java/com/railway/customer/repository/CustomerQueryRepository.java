package com.railway.customer.repository;

import com.railway.customer.entity.CustomerQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerQueryRepository extends JpaRepository<CustomerQuery, Long> {
    List<CustomerQuery> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<CustomerQuery> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    List<CustomerQuery> findByUserIdOrCustomerEmailOrderByCreatedAtDesc(Long userId, String customerEmail);
    List<CustomerQuery> findAllByOrderByCreatedAtDesc();
    List<CustomerQuery> findByStatusOrderByCreatedAtDesc(String status);
}
