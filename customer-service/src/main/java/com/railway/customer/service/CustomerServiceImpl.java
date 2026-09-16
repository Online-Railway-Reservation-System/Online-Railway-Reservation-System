package com.railway.customer.service;

import com.railway.customer.dto.*;
import com.railway.customer.entity.Concession;
import com.railway.customer.entity.ConcessionVerification;
import com.railway.customer.entity.Customer;
import com.railway.customer.exception.BadRequestException;
import com.railway.customer.exception.ResourceNotFoundException;
import com.railway.customer.repository.ConcessionRepository;
import com.railway.customer.repository.ConcessionVerificationRepository;
import com.railway.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final ConcessionRepository concessionRepository;
    private final ConcessionVerificationRepository verificationRepository;
    private final ConcessionVerificationProvider verificationProvider;
    private final RestTemplate restTemplate;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ConcessionRepository concessionRepository,
                               ConcessionVerificationRepository verificationRepository,
                               ConcessionVerificationProvider verificationProvider,
                               @org.springframework.beans.factory.annotation.Autowired(required = false) RestTemplate restTemplate) {
        this.customerRepository = customerRepository;
        this.concessionRepository = concessionRepository;
        this.verificationRepository = verificationRepository;
        this.verificationProvider = verificationProvider;
        this.restTemplate = restTemplate;
    }

    @Override
    public CustomerProfileResponse getProfile(Long userId, String email) {
        Customer customer = getOrCreateCustomer(userId, email);
        return mapToDto(customer);
    }

    @Override
    public CustomerProfileResponse updateProfile(Long userId, String email, CustomerProfileRequest request) {
        Customer customer = getOrCreateCustomer(userId, email);
        customer.setFullName(request.getFullName().trim());
        customer.setMobile(request.getMobile());
        customer.setAddress(request.getAddress());
        customer.setGender(request.getGender());
        customer.setDateOfBirth(request.getDateOfBirth());
        Customer saved = customerRepository.save(customer);

        // Sync full name & mobile to auth-service
        if (restTemplate != null) {
            try {
                String uri = "http://AUTH-SERVICE/api/v1/auth/users/profile?userId=" + (customer.getUserId() != null ? customer.getUserId() : "") +
                        "&email=" + (customer.getEmail() != null ? customer.getEmail() : "") +
                        "&fullName=" + java.net.URLEncoder.encode(request.getFullName().trim(), java.nio.charset.StandardCharsets.UTF_8) +
                        "&mobile=" + (request.getMobile() != null ? java.net.URLEncoder.encode(request.getMobile().trim(), java.nio.charset.StandardCharsets.UTF_8) : "");
                restTemplate.exchange(uri, org.springframework.http.HttpMethod.PUT, new org.springframework.http.HttpEntity<>(new org.springframework.http.HttpHeaders()), Void.class);
                log.info("[AUTH PROFILE SYNCED] Synced profile update for userId: {}, email: {}", customer.getUserId(), customer.getEmail());
            } catch (Exception ex) {
                log.warn("Failed to sync profile update to auth-service: {}", ex.getMessage());
            }
        }

        return mapToDto(saved);
    }

    @Override
    public List<ConcessionResponse> getCustomerConcessions(Long userId, String email) {
        Customer customer = getOrCreateCustomer(userId, email);
        return concessionRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::mapConcessionToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ConcessionResponse addConcession(Long userId, String email, ConcessionRequest request) {
        Customer customer = getOrCreateCustomer(userId, email);

        ConcessionVerificationRequest verReq = new ConcessionVerificationRequest(
                customer.getId(),
                request.getConcessionType(),
                request.getConcessionNumber()
        );

        ConcessionVerificationResponse verResp = verificationProvider.verifyConcession(verReq);

        Concession concession = new Concession();
        concession.setCustomerId(customer.getId());
        concession.setConcessionType(request.getConcessionType().toUpperCase());
        concession.setConcessionNumber(request.getConcessionNumber().trim());
        concession.setValidFrom(LocalDate.now());
        concession.setValidUpto(LocalDate.now().plusYears(1));
        concession.setVerificationStatus(verResp.getVerificationStatus());
        if (verResp.isEligible()) {
            concession.setVerifiedAt(LocalDateTime.now());
        }

        Concession saved = concessionRepository.save(concession);
        return mapConcessionToDto(saved);
    }

    @Override
    public ConcessionVerificationResponse verifyConcession(ConcessionVerificationRequest request) {
        return verificationProvider.verifyConcession(request);
    }

    @Override
    public List<CustomerProfileResponse> getAllCustomers() {
        syncCustomersFromAuthService();
        return customerRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CustomerProfileResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .or(() -> customerRepository.findByUserId(customerId))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return mapToDto(customer);
    }

    @Override
    public CustomerProfileResponse updateCustomerStatus(Long customerId, String status) {
        Customer customer = customerRepository.findById(customerId)
                .or(() -> customerRepository.findByUserId(customerId))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        String cleanStatus = status != null ? status.trim().toUpperCase() : "ACTIVE";
        customer.setStatus(cleanStatus);
        Customer saved = customerRepository.save(customer);

        boolean active = "ACTIVE".equalsIgnoreCase(cleanStatus);

        // 1. Sync active status to auth-service
        if (restTemplate != null) {
            try {
                if (customer.getUserId() != null && customer.getUserId() > 0) {
                    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
                    restTemplate.exchange(
                            "http://AUTH-SERVICE/api/v1/auth/users/" + customer.getUserId() + "/status?active=" + active,
                            org.springframework.http.HttpMethod.POST,
                            entity,
                            Void.class
                    );
                } else if (customer.getEmail() != null && !customer.getEmail().isBlank()) {
                    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
                    restTemplate.exchange(
                            "http://AUTH-SERVICE/api/v1/auth/users/email/status?email=" + customer.getEmail().trim() + "&active=" + active,
                            org.springframework.http.HttpMethod.POST,
                            entity,
                            Void.class
                    );
                }
                log.info("[AUTH STATUS SYNCED] Customer: {}, Status: {}, Active: {}", customer.getId(), cleanStatus, active);
            } catch (Exception ex) {
                log.warn("Failed to sync customer status to auth-service: {}", ex.getMessage());
            }

            // 2. Dispatch dynamic notification email to the customer
            try {
                String subject;
                String content;
                String customerName = customer.getFullName() != null ? customer.getFullName() : "Valued Customer";
                if ("BLOCKED".equalsIgnoreCase(cleanStatus) || "SUSPENDED".equalsIgnoreCase(cleanStatus)) {
                    subject = "Notice of Account Suspension - Pakistan Railways OCC";
                    content = String.format(
                            "Dear %s,\n\nYour Pakistan Railways account associated with %s has been BLOCKED/SUSPENDED by the administrator.\n\nWhile suspended:\n• Login access to the portal is prohibited\n• New reservations cannot be booked\n• Re-registering with this email is disabled\n\nIf you believe this was done in error, please contact railway customer support at support@pakrailways.gov.pk or call 117.\n\nPakistan Railways Operations Control Centre",
                            customerName, customer.getEmail()
                    );
                } else {
                    subject = "Account Re-activated - Pakistan Railways OCC";
                    content = String.format(
                            "Dear %s,\n\nGood news! Your Pakistan Railways account associated with %s has been UNBLOCKED and RE-ACTIVATED by the administrator.\n\nYou may now log in to the portal and resume booking your train journeys.\n\nPakistan Railways Operations Control Centre",
                            customerName, customer.getEmail()
                    );
                }

                Map<String, Object> notifReq = new HashMap<>();
                notifReq.put("recipientEmail", customer.getEmail());
                notifReq.put("recipientPhone", customer.getMobile() != null ? customer.getMobile() : "+919876543210");
                notifReq.put("channel", "EMAIL");
                notifReq.put("notificationType", "ACCOUNT_STATUS");
                notifReq.put("customerId", customer.getId());
                notifReq.put("subject", subject);
                notifReq.put("content", content);

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(notifReq, headers);

                restTemplate.postForObject("http://NOTIFICATION-SERVICE/api/v1/notifications/send", entity, Void.class);
                log.info("[STATUS EMAIL SENT] To: {}, Subject: {}", customer.getEmail(), subject);
            } catch (Exception ex) {
                log.warn("Failed to dispatch status email to {}: {}", customer.getEmail(), ex.getMessage());
            }
        }

        return mapToDto(saved);
    }

    @Override
    public void addVerificationRecord(ConcessionVerificationRequest request, String holderName) {
        ConcessionVerification cv = new ConcessionVerification();
        cv.setConcessionNumber(request.getConcessionNumber().trim());
        cv.setConcessionType(request.getConcessionType().toUpperCase());
        cv.setHolderName(holderName);
        cv.setValidFrom(LocalDate.now().minusMonths(1));
        cv.setValidUpto(LocalDate.now().plusYears(2));
        cv.setStatus("ACTIVE");
        verificationRepository.save(cv);
    }

    private void syncCustomersFromAuthService() {
        if (restTemplate == null) return;
        try {
            org.springframework.http.ResponseEntity<ApiResponse<List<Map<String, Object>>>> response =
                    restTemplate.exchange(
                            "http://AUTH-SERVICE/api/v1/auth/users",
                            org.springframework.http.HttpMethod.GET,
                            null,
                            new org.springframework.core.ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {}
                    );
            if (response.getBody() != null && response.getBody().getData() != null) {
                List<Map<String, Object>> users = response.getBody().getData();
                for (Map<String, Object> u : users) {
                    String role = (String) u.get("role");
                    if ("ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)) {
                        continue;
                    }
                    String email = (String) u.get("email");
                    if (email == null || email.isBlank()) continue;

                    Number idNum = (Number) u.get("id");
                    Long userId = idNum != null ? idNum.longValue() : null;
                    String fullName = (String) u.get("fullName");
                    String mobile = (String) u.get("mobile");
                    Boolean active = (Boolean) u.get("active");
                    String status = (active != null && !active) ? "BLOCKED" : "ACTIVE";

                    java.util.Optional<Customer> existing = customerRepository.findByEmail(email.toLowerCase().trim());
                    if (existing.isEmpty() && userId != null) {
                        existing = customerRepository.findByUserId(userId);
                    }

                    if (existing.isPresent()) {
                        Customer c = existing.get();
                        boolean changed = false;
                        if (c.getUserId() == null || (userId != null && !userId.equals(c.getUserId()))) {
                            c.setUserId(userId);
                            changed = true;
                        }
                        if (fullName != null && !fullName.isBlank() && (c.getFullName() == null || c.getFullName().startsWith("Customer "))) {
                            c.setFullName(fullName.trim());
                            changed = true;
                        }
                        if (mobile != null && !mobile.isBlank() && (c.getMobile() == null || c.getMobile().isBlank())) {
                            c.setMobile(mobile.trim());
                            changed = true;
                        }
                        if (status != null && !status.equalsIgnoreCase(c.getStatus())) {
                            c.setStatus(status);
                            changed = true;
                        }
                        if (changed) {
                            customerRepository.save(c);
                        }
                    } else {
                        Customer c = new Customer();
                        c.setUserId(userId != null ? userId : 0L);
                        c.setEmail(email.toLowerCase().trim());
                        c.setFullName(fullName != null && !fullName.isBlank() ? fullName.trim() : "Customer User");
                        c.setMobile(mobile != null ? mobile.trim() : null);
                        c.setStatus(status);
                        customerRepository.save(c);
                        log.info("[CUSTOMER SYNCED] Imported user {} ({}) to customer database", email, fullName);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to sync customers from auth-service: {}", e.getMessage());
        }
    }

    private Customer getOrCreateCustomer(Long userId, String email) {
        if (userId != null && userId > 0) {
            java.util.Optional<Customer> byId = customerRepository.findByUserId(userId);
            if (byId.isPresent()) {
                return byId.get();
            }
        }
        if (email != null && !email.isBlank()) {
            java.util.Optional<Customer> byEmail = customerRepository.findByEmail(email.toLowerCase().trim());
            if (byEmail.isPresent()) {
                Customer c = byEmail.get();
                if (userId != null && userId > 0 && (c.getUserId() == null || c.getUserId() == 0L)) {
                    c.setUserId(userId);
                    customerRepository.save(c);
                }
                return c;
            }
        }
        if (userId != null || (email != null && !email.isBlank())) {
            return createCustomerFromAuth(userId, email);
        }
        throw new BadRequestException("Neither user id nor email was supplied in request headers");
    }

    private Customer createCustomerFromAuth(Long userId, String email) {
        Customer c = new Customer();
        c.setUserId(userId != null ? userId : 0L);
        c.setEmail(email != null ? email.toLowerCase().trim() : ("customer" + (userId != null ? userId : System.currentTimeMillis()) + "@example.com"));
        c.setFullName("Customer " + (userId != null ? userId : "User"));
        c.setStatus("ACTIVE");

        if (restTemplate != null) {
            try {
                String fetchUrl = (userId != null && userId > 0)
                        ? "http://AUTH-SERVICE/api/v1/auth/users/" + userId
                        : null;
                if (fetchUrl != null) {
                    org.springframework.http.ResponseEntity<ApiResponse<Map<String, Object>>> resp =
                            restTemplate.exchange(
                                    fetchUrl,
                                    org.springframework.http.HttpMethod.GET,
                                    null,
                                    new org.springframework.core.ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {}
                            );
                    if (resp.getBody() != null && resp.getBody().getData() != null) {
                        Map<String, Object> u = resp.getBody().getData();
                        String fullName = (String) u.get("fullName");
                        String mobile = (String) u.get("mobile");
                        String userEmail = (String) u.get("email");
                        Boolean active = (Boolean) u.get("active");
                        if (fullName != null && !fullName.isBlank()) c.setFullName(fullName.trim());
                        if (mobile != null && !mobile.isBlank()) c.setMobile(mobile.trim());
                        if (userEmail != null && !userEmail.isBlank()) c.setEmail(userEmail.toLowerCase().trim());
                        if (active != null && !active) c.setStatus("BLOCKED");
                    }
                }
            } catch (Exception ex) {
                log.warn("Could not query user details from auth-service for userId {}: {}", userId, ex.getMessage());
            }
        }
        return customerRepository.save(c);
    }

    private CustomerProfileResponse mapToDto(Customer c) {
        return new CustomerProfileResponse(
                c.getId(),
                c.getUserId(),
                c.getFullName(),
                c.getEmail(),
                c.getMobile(),
                c.getAddress(),
                c.getGender(),
                c.getDateOfBirth(),
                c.getStatus()
        );
    }

    private ConcessionResponse mapConcessionToDto(Concession con) {
        return new ConcessionResponse(
                con.getId(),
                con.getCustomerId(),
                con.getConcessionType(),
                con.getConcessionNumber(),
                con.getValidFrom(),
                con.getValidUpto(),
                con.getVerificationStatus(),
                con.getVerifiedAt()
        );
    }
}