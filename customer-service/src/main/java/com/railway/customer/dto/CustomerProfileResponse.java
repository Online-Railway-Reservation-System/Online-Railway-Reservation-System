package com.railway.customer.dto;

import java.time.LocalDate;

public class CustomerProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String mobile;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String status;

    public CustomerProfileResponse() {}

    public CustomerProfileResponse(Long id, Long userId, String fullName, String email, String mobile, String address, String gender, LocalDate dateOfBirth, String status) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}