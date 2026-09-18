package com.railway.notification.listener;

import com.railway.notification.config.RabbitMqConfig;
import com.railway.notification.event.FoodOrderEvent;
import com.railway.notification.event.PaymentEvent;
import com.railway.notification.event.RefundEvent;
import com.railway.notification.event.ReservationEvent;
import com.railway.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMqConfig.RESERVATION_QUEUE)
    public void onReservationEvent(ReservationEvent event) {
        log.info("NotificationListener received reservation event: {}", event.getEventType());
        if ("RESERVATION_CONFIRMED".equalsIgnoreCase(event.getEventType())) {
            notificationService.handleReservationConfirmed(event);
        } else if ("RESERVATION_CANCELLED".equalsIgnoreCase(event.getEventType())) {
            notificationService.handleReservationCancelled(event);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.PAYMENT_QUEUE)
    public void onPaymentEvent(PaymentEvent event) {
        log.info("NotificationListener received payment event: {}", event.getEventType());
        if ("PAYMENT_SUCCESS".equalsIgnoreCase(event.getEventType())) {
            notificationService.handlePaymentSuccess(event);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.REFUND_QUEUE)
    public void onRefundEvent(RefundEvent event) {
        log.info("NotificationListener received refund event: {}", event.getEventType());
        if ("REFUND_PROCESSED".equalsIgnoreCase(event.getEventType())) {
            notificationService.handleRefundProcessed(event);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.FOOD_QUEUE)
    public void onFoodOrderEvent(FoodOrderEvent event) {
        log.info("NotificationListener received catering food event: {}", event.getEventType());
        if ("FOOD_ORDER_CONFIRMED".equalsIgnoreCase(event.getEventType())) {
            notificationService.handleFoodOrderConfirmed(event);
        }
    }
}
