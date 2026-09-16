package com.railway.customer.service;

import com.railway.customer.dto.AdminReplyRequest;
import com.railway.customer.dto.CustomerQueryRequest;
import com.railway.customer.dto.CustomerQueryResponse;
import com.railway.customer.entity.Customer;
import com.railway.customer.entity.CustomerQuery;
import com.railway.customer.exception.BadRequestException;
import com.railway.customer.exception.ResourceNotFoundException;
import com.railway.customer.repository.CustomerQueryRepository;
import com.railway.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupportServiceImpl implements SupportService {

    private final CustomerQueryRepository queryRepository;
    private final CustomerRepository customerRepository;

    public SupportServiceImpl(CustomerQueryRepository queryRepository, CustomerRepository customerRepository) {
        this.queryRepository = queryRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerQueryResponse submitQuery(Long userId, String email, CustomerQueryRequest request) {
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new BadRequestException("Subject cannot be blank");
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new BadRequestException("Message cannot be blank");
        }

        String customerName = request.getCustomerName();
        String customerEmail = email != null && !email.isBlank() ? email : request.getCustomerEmail();

        if (userId != null && (customerName == null || customerName.isBlank())) {
            Optional<Customer> customerOpt = customerRepository.findByUserId(userId);
            if (customerOpt.isPresent()) {
                customerName = customerOpt.get().getFullName();
                if (customerEmail == null || customerEmail.isBlank()) {
                    customerEmail = customerOpt.get().getEmail();
                }
            }
        }

        if (customerName == null || customerName.isBlank()) {
            customerName = "Customer User";
        }
        if (customerEmail == null || customerEmail.isBlank()) {
            customerEmail = "customer@example.com";
        }

        CustomerQuery query = new CustomerQuery();
        query.setUserId(userId);
        query.setCustomerName(customerName);
        query.setCustomerEmail(customerEmail);
        query.setCategory(request.getCategory() != null && !request.getCategory().isBlank() ? request.getCategory() : "General Inquiry");
        query.setSubject(request.getSubject().trim());
        query.setMessage(request.getMessage().trim());
        query.setStatus("OPEN");

        CustomerQuery saved = queryRepository.save(query);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerQueryResponse> getMyQueries(Long userId, String email) {
        List<CustomerQuery> queries;
        if (userId != null && email != null && !email.isBlank()) {
            queries = queryRepository.findByUserIdOrCustomerEmailOrderByCreatedAtDesc(userId, email);
        } else if (userId != null) {
            queries = queryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (email != null && !email.isBlank()) {
            queries = queryRepository.findByCustomerEmailOrderByCreatedAtDesc(email);
        } else {
            queries = List.of();
        }
        return queries.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerQueryResponse> getAllQueries(String statusFilter) {
        List<CustomerQuery> queries;
        if (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter)) {
            queries = queryRepository.findByStatusOrderByCreatedAtDesc(statusFilter.toUpperCase());
        } else {
            queries = queryRepository.findAllByOrderByCreatedAtDesc();
        }
        return queries.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CustomerQueryResponse replyToQuery(Long queryId, String adminEmail, AdminReplyRequest request) {
        CustomerQuery query = queryRepository.findById(queryId)
                .orElseThrow(() -> new ResourceNotFoundException("Query ticket not found with id: " + queryId));

        if (request.getAdminReply() == null || request.getAdminReply().isBlank()) {
            throw new BadRequestException("Admin reply cannot be blank");
        }

        query.setAdminReply(request.getAdminReply().trim());
        query.setRepliedBy(request.getRepliedBy() != null ? request.getRepliedBy() : (adminEmail != null ? adminEmail : "Railway Support Desk"));
        query.setRepliedAt(LocalDateTime.now());
        query.setStatus(request.getStatus() != null && !request.getStatus().isBlank() ? request.getStatus().toUpperCase() : "RESOLVED");

        CustomerQuery saved = queryRepository.save(query);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerQueryResponse getQueryById(Long queryId) {
        CustomerQuery query = queryRepository.findById(queryId)
                .orElseThrow(() -> new ResourceNotFoundException("Query ticket not found with id: " + queryId));
        return mapToDto(query);
    }

    private CustomerQueryResponse mapToDto(CustomerQuery q) {
        return new CustomerQueryResponse(
                q.getId(),
                q.getUserId(),
                q.getCustomerName(),
                q.getCustomerEmail(),
                q.getCategory(),
                q.getSubject(),
                q.getMessage(),
                q.getStatus(),
                q.getAdminReply(),
                q.getRepliedBy(),
                q.getRepliedAt(),
                q.getCreatedAt()
        );
    }
}
