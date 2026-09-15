package com.railway.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs", indexes = {
        @Index(name = "idx_notif_pnr", columnList = "pnr"),
        @Index(name = "idx_notif_cust", columnList = "customer_id"),
        @Index(name = "idx_notif_channel", columnList = "channel")
})
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_email", length = 100)
    private String recipientEmail;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(nullable = false, length = 20)
    private String channel = "EMAIL"; // EMAIL, SMS, PUSH

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType; // BOOKING_CONFIRMATION, CANCELLATION, PAYMENT_RECEIPT, MEAL_ORDER, REFUND

    @Column(length = 20)
    private String pnr;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(nullable = false, length = 200)
    private String subject;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    private String status = "SENT"; // SENT, FAILED

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    public NotificationLog() {}

    public NotificationLog(String recipientEmail, String recipientPhone, String channel, String notificationType, String pnr, Long customerId, String subject, String content, String status) {
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.channel = channel;
        this.notificationType = notificationType;
        this.pnr = pnr;
        this.customerId = customerId;
        this.subject = subject;
        this.content = content;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
