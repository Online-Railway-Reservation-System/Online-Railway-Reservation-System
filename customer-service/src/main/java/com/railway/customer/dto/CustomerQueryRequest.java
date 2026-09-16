package com.railway.customer.dto;

import jakarta.validation.constraints.NotBlank;

public class CustomerQueryRequest {

    private String category;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private String customerName;
    private String customerEmail;

    public CustomerQueryRequest() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}
