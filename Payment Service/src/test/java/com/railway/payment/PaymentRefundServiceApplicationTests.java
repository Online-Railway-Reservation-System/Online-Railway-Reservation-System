package com.railway.payment;

import com.railway.payment.dto.*;
import com.railway.payment.service.PaymentService;
import com.railway.payment.service.RefundService;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(PaymentRefundServiceApplicationTests.TestRabbitConfig.class)
class PaymentRefundServiceApplicationTests {

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
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    @Test
    void contextLoads() {
        assertNotNull(paymentService);
        assertNotNull(refundService);
    }

    @Test
    void testProcessPaymentAndQuery() {
        PaymentRequest request = new PaymentRequest();
        request.setCustomerId(101L);
        request.setReservationId(555L);
        request.setPnr("2849182371");
        request.setAmount(1250.0);
        request.setPaymentMethod("UPI");

        PaymentResponse resp = paymentService.processPayment(request);
        assertNotNull(resp);
        assertNotNull(resp.getId());
        assertNotNull(resp.getTransactionReference());
        assertTrue(resp.getTransactionReference().startsWith("TXN-"));
        assertEquals("SUCCESS", resp.getStatus());
        assertEquals(1250.0, resp.getAmount());

        // Query by transaction reference
        PaymentResponse byRef = paymentService.getPaymentByTransactionReference(resp.getTransactionReference());
        assertEquals(resp.getId(), byRef.getId());
        assertEquals("2849182371", byRef.getPnr());

        // Query by PNR
        PaymentResponse byPnr = paymentService.getPaymentByPnr("2849182371");
        assertEquals(resp.getId(), byPnr.getId());

        // Query by Customer
        List<PaymentResponse> custPayments = paymentService.getPaymentsByCustomerId(101L);
        assertFalse(custPayments.isEmpty());
    }

    @Test
    void testPaymentIdempotency() {
        String idempKey = "PAY-IDEMP-KEY-9999";

        PaymentRequest req1 = new PaymentRequest();
        req1.setCustomerId(102L);
        req1.setReservationId(556L);
        req1.setPnr("2849182372");
        req1.setAmount(640.0);
        req1.setPaymentMethod("CREDIT_CARD");
        req1.setIdempotencyKey(idempKey);

        PaymentResponse res1 = paymentService.processPayment(req1);
        assertNotNull(res1);

        PaymentRequest req2 = new PaymentRequest();
        req2.setCustomerId(102L);
        req2.setReservationId(556L);
        req2.setPnr("2849182372");
        req2.setAmount(640.0);
        req2.setPaymentMethod("CREDIT_CARD");
        req2.setIdempotencyKey(idempKey);

        PaymentResponse res2 = paymentService.processPayment(req2);
        assertNotNull(res2);
        assertEquals(res1.getId(), res2.getId());
        assertEquals(res1.getTransactionReference(), res2.getTransactionReference());
    }

    @Test
    void testCalculateRefundPolicyRules() {
        // > 48 hours before departure (SL flat fee = 120)
        RefundCalculateRequest req1 = new RefundCalculateRequest(
                1000.0,
                LocalDate.now().plusDays(5),
                "10:00",
                "SL"
        );
        RefundCalculateResponse resp1 = refundService.calculateRefund(req1);
        assertNotNull(resp1);
        assertEquals(120.0, resp1.getCancellationCharge());
        assertEquals(880.0, resp1.getRefundAmount());
        assertEquals(88, resp1.getRefundPercentage());

        // 1A class (> 48 hours flat fee = 240)
        RefundCalculateRequest req2 = new RefundCalculateRequest(
                2000.0,
                LocalDate.now().plusDays(5),
                "10:00",
                "1A"
        );
        RefundCalculateResponse resp2 = refundService.calculateRefund(req2);
        assertEquals(240.0, resp2.getCancellationCharge());
        assertEquals(1760.0, resp2.getRefundAmount());
    }

    @Test
    void testProcessRefund() {
        RefundRequest request = new RefundRequest();
        request.setReservationId(600L);
        request.setPnr("2849182399");
        request.setAmount(800.0);
        request.setClassType("3A");
        request.setJourneyDate(LocalDate.now().plusDays(4));
        request.setDepartureTime("08:30");
        request.setReason("Personal emergency");

        RefundResponse resp = refundService.processRefund(request);
        assertNotNull(resp);
        assertNotNull(resp.getRefundReference());
        assertTrue(resp.getRefundReference().startsWith("REF-"));
        assertEquals("PROCESSED", resp.getStatus());
        assertEquals(800.0, resp.getOriginalAmount());
        assertEquals(180.0, resp.getDeductionAmount()); // 3A flat charge 180
        assertEquals(620.0, resp.getRefundAmount());

        // Query by PNR
        RefundResponse queried = refundService.getRefundByPnr("2849182399");
        assertEquals(resp.getId(), queried.getId());
        assertEquals(resp.getRefundReference(), queried.getRefundReference());
    }
}
