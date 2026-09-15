package com.railway.payment.repository;

import com.railway.payment.entity.RefundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundTransactionRepository extends JpaRepository<RefundTransaction, Long> {
    Optional<RefundTransaction> findByRefundReference(String refundReference);
    Optional<RefundTransaction> findByReservationId(Long reservationId);
    Optional<RefundTransaction> findByPnr(String pnr);
    List<RefundTransaction> findByPaymentTransactionId(Long paymentTransactionId);
}
