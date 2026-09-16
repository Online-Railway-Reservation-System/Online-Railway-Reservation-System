package com.railway.payment.service;

import com.railway.payment.config.RabbitMqConfig;
import com.railway.payment.dto.PaymentRequest;
import com.railway.payment.dto.PaymentResponse;
import com.railway.payment.entity.PaymentTransaction;
import com.railway.payment.event.PaymentEvent;
import com.railway.payment.exception.BadRequestException;
import com.railway.payment.exception.ResourceNotFoundException;
import com.railway.payment.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentTransactionRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    public PaymentServiceImpl(PaymentTransactionRepository paymentRepository, RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. Check Idempotency Key
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<PaymentTransaction> existingOpt = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existingOpt.isPresent()) {
                log.info("Idempotent payment request detected for key: {}", request.getIdempotencyKey());
                return mapToResponse(existingOpt.get());
            }
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        String method = request.getPaymentMethod() != null ? request.getPaymentMethod().toUpperCase() : "CREDIT_CARD";
        if (!List.of("CREDIT_CARD", "DEBIT_CARD", "UPI", "NET_BANKING").contains(method)) {
            throw new BadRequestException("Unsupported payment method: " + method);
        }

        // 2. Generate Transaction Reference and Gateway Reference
        String txnRef = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String gwRef = "GW-" + System.currentTimeMillis();

        PaymentTransaction txn = new PaymentTransaction();
        txn.setTransactionReference(txnRef);
        txn.setReservationId(request.getReservationId());
        txn.setPnr(request.getPnr());
        txn.setCustomerId(request.getCustomerId() != null ? request.getCustomerId() : 1L);
        txn.setAmount(request.getAmount());
        txn.setPaymentMethod(method);
        txn.setStatus("SUCCESS");
        txn.setIdempotencyKey(request.getIdempotencyKey());
        txn.setGatewayReference(gwRef);

        PaymentTransaction saved = paymentRepository.save(txn);

        // 3. Publish Event to RabbitMQ
        try {
            PaymentEvent event = new PaymentEvent(
                    "PAYMENT_SUCCESS",
                    saved.getId(),
                    saved.getTransactionReference(),
                    saved.getReservationId(),
                    saved.getPnr(),
                    saved.getCustomerId(),
                    saved.getAmount(),
                    saved.getStatus()
            );
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "railway.payment.success", event);
        } catch (Exception e) {
            log.warn("RabbitMQ payment publish degraded: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        PaymentTransaction txn = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with id: " + id));
        return mapToResponse(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionReference(String transactionReference) {
        PaymentTransaction txn = paymentRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with reference: " + transactionReference));
        return mapToResponse(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReservationId(Long reservationId) {
        PaymentTransaction txn = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found for reservation: " + reservationId));
        return mapToResponse(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByPnr(String pnr) {
        PaymentTransaction txn = paymentRepository.findByPnr(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found for PNR: " + pnr));
        return mapToResponse(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::mapToResponse);
    }

    private PaymentResponse mapToResponse(PaymentTransaction t) {
        return new PaymentResponse(
                t.getId(),
                t.getTransactionReference(),
                t.getReservationId(),
                t.getPnr(),
                t.getCustomerId(),
                t.getAmount(),
                t.getPaymentMethod(),
                t.getStatus(),
                t.getGatewayReference(),
                t.getCreatedAt()
        );
    }
}
