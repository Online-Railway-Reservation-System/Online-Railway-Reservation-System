package com.railway.notification.dto;

import jakarta.validation.constraints.NotBlank;

public class NotificationRequest {

    private String recipientEmail;
    private String recipientPhone;

    @NotBlank(message = "Channel is required (EMAIL, SMS, PUSH)")
    private String channel = "EMAIL";

    @NotBlank(message = "Notification type is required")
    private String notificationType;

    private String pnr;
    private Long customerId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    public NotificationRequest() {}

    public NotificationRequest(String recipientEmail, String recipientPhone, String channel, String notificationType, String pnr, Long customerId, String subject, String content) {
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.channel = channel;
        this.notificationType = notificationType;
        this.pnr = pnr;
        this.customerId = customerId;
        this.subject = subject;
        this.content = content;
    }

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
}
