package com.railway.customer.dto;

import java.time.LocalDateTime;

public class CustomerQueryResponse {

    private Long id;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String category;
    private String subject;
    private String message;
    private String status;
    private String adminReply;
    private String repliedBy;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;

    public CustomerQueryResponse() {}

    public CustomerQueryResponse(Long id, Long userId, String customerName, String customerEmail,
                                 String category, String subject, String message, String status,
                                 String adminReply, String repliedBy, LocalDateTime repliedAt,
                                 LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.category = category;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.adminReply = adminReply;
        this.repliedBy = repliedBy;
        this.repliedAt = repliedAt;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public String getRepliedBy() { return repliedBy; }
    public void setRepliedBy(String repliedBy) { this.repliedBy = repliedBy; }

    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
