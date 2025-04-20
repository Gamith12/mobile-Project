package com.luxevistaresort.Models;

public class ServiceReservation {

    private String serviceType;
    private String date;
    private String time;
    private String guestName;
    private int userId;

    public ServiceReservation(int userId, String serviceType, String date, String time, String guestName) {
        this.serviceType = serviceType;
        this.date = date;
        this.time = time;
        this.guestName = guestName;
        this.userId = userId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
