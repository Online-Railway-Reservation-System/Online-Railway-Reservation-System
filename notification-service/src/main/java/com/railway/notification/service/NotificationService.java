package com.railway.notification.service;

import com.railway.notification.dto.NotificationRequest;
import com.railway.notification.dto.NotificationResponse;
import com.railway.notification.event.FoodOrderEvent;
import com.railway.notification.event.PaymentEvent;
import com.railway.notification.event.RefundEvent;
import com.railway.notification.event.ReservationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationResponse sendNotification(NotificationRequest request);
    NotificationResponse getNotificationById(Long id);
    List<NotificationResponse> getNotificationsByPnr(String pnr);
    List<NotificationResponse> getNotificationsByCustomerId(Long customerId);
    Page<NotificationResponse> getAllNotifications(Pageable pageable);

    // Event handlers triggered by RabbitMQ messages
    void handleReservationConfirmed(ReservationEvent event);
    void handleReservationCancelled(ReservationEvent event);
    void handlePaymentSuccess(PaymentEvent event);
    void handleRefundProcessed(RefundEvent event);
    void handleFoodOrderConfirmed(FoodOrderEvent event);
}
