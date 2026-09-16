package com.railway.customer.service;

import com.railway.customer.dto.AdminReplyRequest;
import com.railway.customer.dto.CustomerQueryRequest;
import com.railway.customer.dto.CustomerQueryResponse;

import java.util.List;

public interface SupportService {
    CustomerQueryResponse submitQuery(Long userId, String email, CustomerQueryRequest request);
    List<CustomerQueryResponse> getMyQueries(Long userId, String email);
    List<CustomerQueryResponse> getAllQueries(String statusFilter);
    CustomerQueryResponse replyToQuery(Long queryId, String adminEmail, AdminReplyRequest request);
    CustomerQueryResponse getQueryById(Long queryId);
}
