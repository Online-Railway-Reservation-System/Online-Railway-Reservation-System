package com.railway.customer.repository;

import com.railway.customer.entity.Concession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConcessionRepository extends JpaRepository<Concession, Long> {
    List<Concession> findByCustomerId(Long customerId);
    Optional<Concession> findByCustomerIdAndConcessionType(Long customerId, String concessionType);
    Optional<Concession> findByConcessionNumber(String concessionNumber);
}