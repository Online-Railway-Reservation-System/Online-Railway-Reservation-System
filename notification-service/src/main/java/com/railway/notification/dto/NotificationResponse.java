package com.railway.notification.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String recipientEmail;
    private String recipientPhone;
    private String channel;
    private String notificationType;
    private String pnr;
    private Long customerId;
    private String subject;
    private String content;
    private String status;
    private LocalDateTime sentAt;

    public NotificationResponse() {}

    public NotificationResponse(Long id, String recipientEmail, String recipientPhone, String channel, String notificationType, String pnr, Long customerId, String subject, String content, String status, LocalDateTime sentAt) {
        this.id = id;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.channel = channel;
        this.notificationType = notificationType;
        this.pnr = pnr;
        this.customerId = customerId;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.sentAt = sentAt;
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
