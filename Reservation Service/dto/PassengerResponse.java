package com.railway.reservation.dto;

public class PassengerResponse {
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String seatPreference;
    private String seatNumber;
    private String berthType;
    private String status;
    private String concessionType;

    public PassengerResponse() {}

    public PassengerResponse(Long id, String name, String gender, Integer age, String seatPreference, String seatNumber, String berthType, String status, String concessionType) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.seatPreference = seatPreference;
        this.seatNumber = seatNumber;
        this.berthType = berthType;
        this.status = status;
        this.concessionType = concessionType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getSeatPreference() { return seatPreference; }
    public void setSeatPreference(String seatPreference) { this.seatPreference = seatPreference; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getBerthType() { return berthType; }
    public void setBerthType(String berthType) { this.berthType = berthType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConcessionType() { return concessionType; }
    public void setConcessionType(String concessionType) { this.concessionType = concessionType; }
}