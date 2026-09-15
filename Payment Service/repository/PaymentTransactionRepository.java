package com.railway.payment.repository;

import com.railway.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionReference(String transactionReference);
    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);
    Optional<PaymentTransaction> findByReservationId(Long reservationId);
    Optional<PaymentTransaction> findByPnr(String pnr);
    List<PaymentTransaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
