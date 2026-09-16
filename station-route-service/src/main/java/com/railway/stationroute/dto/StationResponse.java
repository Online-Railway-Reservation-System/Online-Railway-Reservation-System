package com.railway.stationroute.dto;

public class StationResponse {
    private Long id;
    private String stationCode;
    private String stationName;
    private String city;
    private String state;
    private boolean activeStatus;

    public StationResponse() {}

    public StationResponse(Long id, String stationCode, String stationName, String city, String state, boolean activeStatus) {
        this.id = id;
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.city = city;
        this.state = state;
        this.activeStatus = activeStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStationCode() { return stationCode; }
    public void setStationCode(String stationCode) { this.stationCode = stationCode; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public boolean isActiveStatus() { return activeStatus; }
    public void setActiveStatus(boolean activeStatus) { this.activeStatus = activeStatus; }
}