package com.luxevistaresort.Models;

public class Booking {

    private int id;
    private int userId;
    private int roomId;
    private String roomTitle;
    private String roomImageUrl;
    private String bookedDate;
    private String roomPrice;
    private String availability;
    private String rating;

    private int nights;

    private int status;

    public Booking(int id, int userId, int roomId, String roomTitle, String roomImageUrl, String bookedDate, String roomPrice, String availability, String rating, int nights, int status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.roomImageUrl = roomImageUrl;
        this.bookedDate = bookedDate;
        this.roomPrice = roomPrice;
        this.availability = availability;
        this.rating = rating;
        this.nights = nights;
        this.status = status;
    }

    public Booking() {}
    public Booking (String roomImageUrl, String roomTitle, String bookedDate, String roomPrice){
        this.roomImageUrl = roomImageUrl;
        this.roomTitle = roomTitle;
        this.bookedDate = bookedDate;
        this.roomPrice = roomPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getRoomImageUrl() {
        return roomImageUrl;
    }

    public void setRoomImageUrl(String roomImageUrl) {
        this.roomImageUrl = roomImageUrl;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(String bookedDate) {
        this.bookedDate = bookedDate;
    }

    public String getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(String roomPrice) {
        this.roomPrice = roomPrice;
    }
}
