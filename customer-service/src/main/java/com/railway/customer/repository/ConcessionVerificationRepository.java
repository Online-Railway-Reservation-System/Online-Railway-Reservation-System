package com.railway.customer.repository;

import com.railway.customer.entity.ConcessionVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcessionVerificationRepository extends JpaRepository<ConcessionVerification, Long> {
    Optional<ConcessionVerification> findByConcessionNumberAndConcessionType(String concessionNumber, String concessionType);
    Optional<ConcessionVerification> findByConcessionNumber(String concessionNumber);
}