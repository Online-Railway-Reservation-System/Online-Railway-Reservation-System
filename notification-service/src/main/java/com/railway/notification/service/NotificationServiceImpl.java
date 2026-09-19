package com.railway.notification.service;

import com.railway.notification.dto.NotificationRequest;
import com.railway.notification.dto.NotificationResponse;
import com.railway.notification.entity.NotificationLog;
import com.railway.notification.event.FoodOrderEvent;
import com.railway.notification.event.PaymentEvent;
import com.railway.notification.event.RefundEvent;
import com.railway.notification.event.ReservationEvent;
import com.railway.notification.exception.BadRequestException;
import com.railway.notification.exception.ResourceNotFoundException;
import com.railway.notification.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationLogRepository logRepository;
    private final org.springframework.mail.javamail.JavaMailSender mailSender;

    public NotificationServiceImpl(NotificationLogRepository logRepository,
                                   @org.springframework.beans.factory.annotation.Autowired(required = false) org.springframework.mail.javamail.JavaMailSender mailSender) {
        this.logRepository = logRepository;
        this.mailSender = mailSender;
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new BadRequestException("Notification subject cannot be blank");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BadRequestException("Notification content cannot be blank");
        }

        NotificationLog nLog = new NotificationLog();
        nLog.setRecipientEmail(request.getRecipientEmail());
        nLog.setRecipientPhone(request.getRecipientPhone());
        nLog.setChannel(request.getChannel() != null ? request.getChannel().toUpperCase() : "EMAIL");
        nLog.setNotificationType(request.getNotificationType());
        nLog.setPnr(request.getPnr());
        nLog.setCustomerId(request.getCustomerId());
        nLog.setSubject(request.getSubject());
        nLog.setContent(request.getContent());
        nLog.setStatus("SENT");

        if ("EMAIL".equalsIgnoreCase(nLog.getChannel()) && nLog.getRecipientEmail() != null && !nLog.getRecipientEmail().isBlank() && mailSender != null) {
            String recipient = nLog.getRecipientEmail().trim().toLowerCase();
            if (recipient.endsWith("@railway.com") || recipient.contains("example.com")) {
                log.info("[SKIP DUMMY RECIPIENT] Recipient {} is a synthetic placeholder, skipping SMTP network send.", recipient);
                nLog.setStatus("SKIPPED_SYNTHETIC");
            } else {
                try {
                    org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                    message.setFrom("abulhasanrathinamohamed@gmail.com");
                    message.setTo(nLog.getRecipientEmail());
                    message.setSubject(nLog.getSubject());
                    message.setText(nLog.getContent());
                    mailSender.send(message);
                    nLog.setStatus("SENT");
                    log.info("[REAL EMAIL SENT] To: {}, Subject: {}", nLog.getRecipientEmail(), nLog.getSubject());
                } catch (Exception e) {
                    log.error("[EMAIL SEND FAILED] To: {}, Subject: {}, Error: {}", nLog.getRecipientEmail(), nLog.getSubject(), e.getMessage());
                    nLog.setStatus("FAILED");
                }
            }
        } else {
            log.info("[NOTIFICATION DISPATCHED] Channel: {}, Recipient: {}/{}, Subject: {}",
                    nLog.getChannel(), nLog.getRecipientEmail(), nLog.getRecipientPhone(), nLog.getSubject());
        }

        NotificationLog saved = logRepository.save(nLog);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        NotificationLog nLog = logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification log not found with id: " + id));
        return mapToDto(nLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByPnr(String pnr) {
        return logRepository.findByPnrOrderBySentAtDesc(pnr)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByCustomerId(Long customerId) {
        return logRepository.findByCustomerIdOrderBySentAtDesc(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        return logRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public void handleReservationConfirmed(ReservationEvent event) {
        log.info("Received RESERVATION_CONFIRMED event for PNR: {}", event.getPnr());

        String emailSubject = "Booking Confirmed - PNR: " + event.getPnr();
        String emailBody = String.format(
                "Dear Customer, your ticket for train %s has been CONFIRMED. PNR: %s. Total Fare: INR %.2f. Happy Journey!",
                event.getTrainNumber(), event.getPnr(), event.getTotalAmount()
        );

        String recipientEmail = (event.getCustomerEmail() != null && !event.getCustomerEmail().isBlank())
                ? event.getCustomerEmail()
                : ("customer" + event.getCustomerId() + "@railway.com");

        NotificationRequest emailReq = new NotificationRequest(
                recipientEmail,
                "+919876543210",
                "EMAIL",
                "BOOKING_CONFIRMATION",
                event.getPnr(),
                event.getCustomerId(),
                emailSubject,
                emailBody
        );
        sendNotification(emailReq);

        // Also send SMS alert
        NotificationRequest smsReq = new NotificationRequest(
                null,
                "+919876543210",
                "SMS",
                "BOOKING_CONFIRMATION",
                event.getPnr(),
                event.getCustomerId(),
                "IRCTC Booking Alert",
                "PNR " + event.getPnr() + " confirmed. Train: " + event.getTrainNumber()
        );
        sendNotification(smsReq);
    }

    @Override
    public void handleReservationCancelled(ReservationEvent event) {
        log.info("Received RESERVATION_CANCELLED event for PNR: {}", event.getPnr());

        String emailSubject = "Ticket Cancelled - PNR: " + event.getPnr();
        String emailBody = String.format(
                "Dear Customer, your reservation for PNR: %s has been CANCELLED. Refund will be processed back to original source according to IRCTC rules.",
                event.getPnr()
        );

        String cancelRecipientEmail = (event.getCustomerEmail() != null && !event.getCustomerEmail().isBlank())
                ? event.getCustomerEmail()
                : ("customer" + event.getCustomerId() + "@railway.com");

        NotificationRequest emailReq = new NotificationRequest(
                cancelRecipientEmail,
                "+919876543210",
                "EMAIL",
                "CANCELLATION",
                event.getPnr(),
                event.getCustomerId(),
                emailSubject,
                emailBody
        );
        sendNotification(emailReq);
    }

    @Override
    public void handlePaymentSuccess(PaymentEvent event) {
        log.info("Received PAYMENT_SUCCESS event for TXN: {}, PNR: {}", event.getTransactionReference(), event.getPnr());

        String emailSubject = "Payment Receipt - " + event.getTransactionReference();
        String emailBody = String.format(
                "Payment of INR %.2f was successfully received for PNR: %s. Transaction Ref: %s.",
                event.getAmount(), event.getPnr(), event.getTransactionReference()
        );

        NotificationRequest emailReq = new NotificationRequest(
                "customer" + event.getCustomerId() + "@railway.com",
                "+919876543210",
                "EMAIL",
                "PAYMENT_RECEIPT",
                event.getPnr(),
                event.getCustomerId(),
                emailSubject,
                emailBody
        );
        sendNotification(emailReq);
    }

    @Override
    public void handleRefundProcessed(RefundEvent event) {
        log.info("Received REFUND_PROCESSED event for RefundRef: {}, PNR: {}", event.getRefundReference(), event.getPnr());

        String emailSubject = "Refund Processed - Ref: " + event.getRefundReference();
        String emailBody = String.format(
                "Refund of INR %.2f has been initiated for PNR: %s. Deduction charge: INR %.2f. Reference: %s.",
                event.getRefundAmount(), event.getPnr(), event.getDeductionAmount(), event.getRefundReference()
        );

        NotificationRequest emailReq = new NotificationRequest(
                "customer@railway.com",
                "+919876543210",
                "EMAIL",
                "REFUND",
                event.getPnr(),
                null,
                emailSubject,
                emailBody
        );
        sendNotification(emailReq);
    }

    @Override
    public void handleFoodOrderConfirmed(FoodOrderEvent event) {
        log.info("Received FOOD_ORDER_CONFIRMED event for FoodOrder: {}, PNR: {}", event.getFoodOrderId(), event.getPnr());

        String emailSubject = "Catering Order Confirmed - PNR: " + event.getPnr();
        String emailBody = String.format(
                "Your meal order #%d for PNR %s has been confirmed! Total: INR %.2f. Meals will be delivered directly to your berth.",
                event.getFoodOrderId(), event.getPnr(), event.getTotalAmount()
        );

        NotificationRequest emailReq = new NotificationRequest(
                "customer@railway.com",
                "+919876543210",
                "EMAIL",
                "MEAL_ORDER",
                event.getPnr(),
                null,
                emailSubject,
                emailBody
        );
        sendNotification(emailReq);
    }

    private NotificationResponse mapToDto(NotificationLog n) {
        return new NotificationResponse(
                n.getId(),
                n.getRecipientEmail(),
                n.getRecipientPhone(),
                n.getChannel(),
                n.getNotificationType(),
                n.getPnr(),
                n.getCustomerId(),
                n.getSubject(),
                n.getContent(),
                n.getStatus(),
                n.getSentAt()
        );
    }
}
