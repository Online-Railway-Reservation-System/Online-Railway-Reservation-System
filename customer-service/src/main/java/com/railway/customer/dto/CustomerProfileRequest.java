package com.railway.customer.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class CustomerProfileRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private String mobile;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;

    public CustomerProfileRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}