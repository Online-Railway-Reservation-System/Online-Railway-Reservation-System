package com.railway.payment.service;

import com.railway.payment.config.RabbitMqConfig;
import com.railway.payment.dto.RefundCalculateRequest;
import com.railway.payment.dto.RefundCalculateResponse;
import com.railway.payment.dto.RefundRequest;
import com.railway.payment.dto.RefundResponse;
import com.railway.payment.entity.PaymentTransaction;
import com.railway.payment.entity.RefundTransaction;
import com.railway.payment.event.RefundEvent;
import com.railway.payment.exception.BadRequestException;
import com.railway.payment.exception.ResourceNotFoundException;
import com.railway.payment.repository.PaymentTransactionRepository;
import com.railway.payment.repository.RefundTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);

    private final RefundTransactionRepository refundRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    public RefundServiceImpl(RefundTransactionRepository refundRepository,
                             PaymentTransactionRepository paymentRepository,
                             RabbitTemplate rabbitTemplate) {
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public RefundCalculateResponse calculateRefund(RefundCalculateRequest request) {
        double originalAmount = request.getTicketAmount() != null ? request.getTicketAmount() : 0.0;
        if (originalAmount <= 0) {
            throw new BadRequestException("Ticket amount must be greater than zero");
        }

        String classType = request.getClassType() != null ? request.getClassType().toUpperCase() : "SL";
        double flatCharge = getFlatCancellationCharge(classType);

        long hoursRemaining = 72; // Default to > 48 hours if dates not provided
        if (request.getJourneyDate() != null) {
            LocalTime depTime = LocalTime.of(6, 0);
            if (request.getDepartureTime() != null && request.getDepartureTime().contains(":")) {
                try {
                    String[] parts = request.getDepartureTime().split(":");
                    depTime = LocalTime.of(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
                } catch (Exception ignored) {}
            }
            LocalDateTime departureDateTime = LocalDateTime.of(request.getJourneyDate(), depTime);
            hoursRemaining = Duration.between(LocalDateTime.now(), departureDateTime).toHours();
        }

        double cancellationCharge;
        int refundPercentage;
        String policyDescription;

        if (hoursRemaining > 48) {
            cancellationCharge = Math.min(originalAmount, flatCharge);
            double refund = originalAmount - cancellationCharge;
            refundPercentage = (int) Math.round((refund / originalAmount) * 100);
            policyDescription = "More than 48 hours before departure: Flat clerkage/cancellation charge applied (" + flatCharge + " INR)";
        } else if (hoursRemaining >= 12) {
            double fee = Math.max(flatCharge, originalAmount * 0.25);
            cancellationCharge = Math.min(originalAmount, fee);
            refundPercentage = 75;
            policyDescription = "Between 12 and 48 hours before departure: 25% cancellation charge applied";
        } else if (hoursRemaining >= 4) {
            double fee = Math.max(flatCharge, originalAmount * 0.50);
            cancellationCharge = Math.min(originalAmount, fee);
            refundPercentage = 50;
            policyDescription = "Between 4 and 12 hours before departure: 50% cancellation charge applied";
        } else {
            cancellationCharge = originalAmount;
            refundPercentage = 0;
            policyDescription = "Less than 4 hours before departure: No refund permissible under Indian Railways policy";
        }

        double finalRefund = Math.max(0.0, originalAmount - cancellationCharge);

        return new RefundCalculateResponse(
                originalAmount,
                hoursRemaining,
                cancellationCharge,
                finalRefund,
                refundPercentage,
                policyDescription
        );
    }

    @Override
    public RefundResponse processRefund(RefundRequest request) {
        double originalAmount = request.getAmount() != null ? request.getAmount() : 0.0;
        if (originalAmount <= 0) {
            throw new BadRequestException("Refund amount must be greater than zero");
        }

        RefundCalculateRequest calcReq = new RefundCalculateRequest(
                originalAmount,
                request.getJourneyDate(),
                request.getDepartureTime(),
                request.getClassType()
        );
        RefundCalculateResponse calcResp = calculateRefund(calcReq);

        String refNumber = "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        RefundTransaction refund = new RefundTransaction();
        refund.setRefundReference(refNumber);
        refund.setPaymentTransactionId(request.getPaymentTransactionId());
        refund.setReservationId(request.getReservationId());
        refund.setPnr(request.getPnr());
        refund.setOriginalAmount(calcResp.getOriginalAmount());
        refund.setDeductionAmount(calcResp.getCancellationCharge());
        refund.setRefundAmount(calcResp.getRefundAmount());
        refund.setReason(request.getReason());
        refund.setStatus("PROCESSED");

        RefundTransaction saved = refundRepository.save(refund);

        // Publish refund event to RabbitMQ
        try {
            RefundEvent event = new RefundEvent(
                    "REFUND_PROCESSED",
                    saved.getId(),
                    saved.getRefundReference(),
                    saved.getReservationId(),
                    saved.getPnr(),
                    saved.getOriginalAmount(),
                    saved.getRefundAmount(),
                    saved.getDeductionAmount(),
                    saved.getStatus()
            );
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "railway.refund.processed", event);
        } catch (Exception e) {
            log.warn("RabbitMQ refund event publish degraded: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getRefundById(Long id) {
        RefundTransaction ref = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund transaction not found with id: " + id));
        return mapToResponse(ref);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getRefundByReference(String refundReference) {
        RefundTransaction ref = refundRepository.findByRefundReference(refundReference)
                .orElseThrow(() -> new ResourceNotFoundException("Refund transaction not found with reference: " + refundReference));
        return mapToResponse(ref);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getRefundByReservationId(Long reservationId) {
        RefundTransaction ref = refundRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund transaction not found for reservation: " + reservationId));
        return mapToResponse(ref);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getRefundByPnr(String pnr) {
        RefundTransaction ref = refundRepository.findByPnr(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Refund transaction not found for PNR: " + pnr));
        return mapToResponse(ref);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RefundResponse> getAllRefunds(Pageable pageable) {
        return refundRepository.findAll(pageable).map(this::mapToResponse);
    }

    private double getFlatCancellationCharge(String classType) {
        return switch (classType.toUpperCase()) {
            case "1A", "EC" -> 240.0;
            case "2A" -> 200.0;
            case "3A", "CC" -> 180.0;
            case "SL" -> 120.0;
            case "2S" -> 60.0;
            default -> 120.0;
        };
    }

    private RefundResponse mapToResponse(RefundTransaction r) {
        return new RefundResponse(
                r.getId(),
                r.getRefundReference(),
                r.getPaymentTransactionId(),
                r.getReservationId(),
                r.getPnr(),
                r.getOriginalAmount(),
                r.getDeductionAmount(),
                r.getRefundAmount(),
                r.getStatus(),
                r.getReason(),
                r.getProcessedAt()
        );
    }
}
