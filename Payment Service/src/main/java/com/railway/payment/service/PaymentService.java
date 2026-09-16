package com.railway.payment.service;

import com.railway.payment.dto.PaymentRequest;
import com.railway.payment.dto.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse getPaymentByTransactionReference(String transactionReference);
    PaymentResponse getPaymentByReservationId(Long reservationId);
    PaymentResponse getPaymentByPnr(String pnr);
    List<PaymentResponse> getPaymentsByCustomerId(Long customerId);
    Page<PaymentResponse> getAllPayments(Pageable pageable);
}
