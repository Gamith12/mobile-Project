package com.luxevistaresort.Models;

import java.util.List;

public class Room {

    private int id;
    private String rating;
    private String type;
    private String price;
    private String shortDescription;
    private String roomTitle;
    private String imageUrl;

    private String availability;

    public Room() {
        // Empty constructor for cases like DB mapping
    }

    public Room(int id,String roomTitle, String price,String shortDescription, String imageUrl, String rating) {
        this.id = id;
        this.roomTitle = roomTitle;
        this.price = price;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    public Room(int id,String roomTitle, String type, String price,String imageUrl, String shortDescription,  String rating, String availability) {
        this.id = id;
        this.roomTitle = roomTitle;
        this.type = type;
        this.price = price;
        this.imageUrl = imageUrl;
        this.shortDescription = shortDescription;
        this.rating = rating;
        this.availability = availability;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public double getNumericPrice() {
        try {
            return Double.parseDouble(price.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getPrice() {

        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getId() {
        return id;
    }

    public String getRating() {
        return rating;
    }

    public String getType() {
        return type;
    }
}
