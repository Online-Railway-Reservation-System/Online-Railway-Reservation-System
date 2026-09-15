package com.railway.notification;

import com.railway.notification.dto.NotificationRequest;
import com.railway.notification.dto.NotificationResponse;
import com.railway.notification.event.PaymentEvent;
import com.railway.notification.event.RefundEvent;
import com.railway.notification.event.ReservationEvent;
import com.railway.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(NotificationServiceApplicationTests.TestRabbitConfig.class)
class NotificationServiceApplicationTests {

    @TestConfiguration
    static class TestRabbitConfig {
        @Bean
        @Primary
        public ConnectionFactory connectionFactory() {
            return Mockito.mock(ConnectionFactory.class);
        }

        @Bean
        @Primary
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            return new RabbitTemplate(connectionFactory) {
                @Override
                public void convertAndSend(String exchange, String routingKey, Object message) {
                    // No-op for tests
                }
            };
        }
    }

    @Autowired
    private NotificationService notificationService;

    @Test
    void contextLoads() {
        assertNotNull(notificationService);
    }

    @Test
    void testSendNotificationDirectly() {
        NotificationRequest request = new NotificationRequest(
                "user@example.com",
                "+919876543210",
                "EMAIL",
                "GENERAL_ALERT",
                "2849182301",
                55L,
                "Platform Change Notice",
                "Train 12678 will now depart from Platform 4."
        );

        NotificationResponse resp = notificationService.sendNotification(request);
        assertNotNull(resp);
        assertNotNull(resp.getId());
        assertEquals("user@example.com", resp.getRecipientEmail());
        assertEquals("SENT", resp.getStatus());

        NotificationResponse byId = notificationService.getNotificationById(resp.getId());
        assertEquals(resp.getId(), byId.getId());
    }

    @Test
    void testHandleReservationConfirmedEvent() {
        ReservationEvent event = new ReservationEvent(
                "RESERVATION_CONFIRMED",
                101L,
                "2849182302",
                66L,
                "12678",
                920.0,
                "CONFIRMED",
                "Booking successful"
        );

        notificationService.handleReservationConfirmed(event);

        List<NotificationResponse> logs = notificationService.getNotificationsByPnr("2849182302");
        assertFalse(logs.isEmpty());
        assertTrue(logs.stream().anyMatch(l -> "EMAIL".equalsIgnoreCase(l.getChannel())));
        assertTrue(logs.stream().anyMatch(l -> "SMS".equalsIgnoreCase(l.getChannel())));
    }

    @Test
    void testHandlePaymentSuccessEvent() {
        PaymentEvent event = new PaymentEvent(
                "PAYMENT_SUCCESS",
                201L,
                "TXN-ABC12345678",
                101L,
                "2849182303",
                67L,
                1450.0,
                "SUCCESS"
        );

        notificationService.handlePaymentSuccess(event);

        List<NotificationResponse> logs = notificationService.getNotificationsByPnr("2849182303");
        assertFalse(logs.isEmpty());
        assertEquals("PAYMENT_RECEIPT", logs.get(0).getNotificationType());
    }

    @Test
    void testHandleRefundProcessedEvent() {
        RefundEvent event = new RefundEvent(
                "REFUND_PROCESSED",
                301L,
                "REF-XYZ1234567",
                101L,
                "2849182304",
                1000.0,
                880.0,
                120.0,
                "PROCESSED"
        );

        notificationService.handleRefundProcessed(event);

        List<NotificationResponse> logs = notificationService.getNotificationsByPnr("2849182304");
        assertFalse(logs.isEmpty());
        assertEquals("REFUND", logs.get(0).getNotificationType());
    }

    @Test
    void testQueryNotificationsByCustomerId() {
        NotificationRequest request = new NotificationRequest(
                "cust88@example.com",
                "+919876543210",
                "EMAIL",
                "WELCOME",
                null,
                88L,
                "Welcome to IRCTC",
                "Thank you for registering with Online Railway Reservation System."
        );
        notificationService.sendNotification(request);

        List<NotificationResponse> custLogs = notificationService.getNotificationsByCustomerId(88L);
        assertFalse(custLogs.isEmpty());
        assertEquals(88L, custLogs.get(0).getCustomerId());
    }
}
