package com.railway.auth.dto;

public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String mobile;
    private String role;
    private boolean active;

    public UserDto() {}

    public UserDto(Long id, String email, String fullName, String mobile, String role, boolean active) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.mobile = mobile;
        this.role = role;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}