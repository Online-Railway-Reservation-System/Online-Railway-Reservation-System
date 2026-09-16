package com.railway.customer.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminReplyRequest {

    @NotBlank(message = "Reply message cannot be empty")
    private String adminReply;

    private String status = "RESOLVED";

    private String repliedBy;

    public AdminReplyRequest() {}

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRepliedBy() { return repliedBy; }
    public void setRepliedBy(String repliedBy) { this.repliedBy = repliedBy; }
}
