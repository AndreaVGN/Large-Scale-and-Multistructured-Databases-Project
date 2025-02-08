package com.example.WanderHub.demo.DTO;

import lombok.*;


public class CityBookingRankingDTO {
    private String city;
    private long bookingCount;

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(long bookingCount) {
        this.bookingCount = bookingCount;
    }
}
