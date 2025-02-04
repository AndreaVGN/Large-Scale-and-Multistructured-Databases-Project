package com.example.WanderHub.demo.model;

import java.util.Date;

public class ArchivedBooking {
    private String accommodationId;
    private String hostUsername;
    private String city;
    private String country;
    private Date startDate;
    private Date endDate;
    private int nights;
    private double totalCost;
    private String username;
    private int guestCount;

    public ArchivedBooking(String accommodationId, String hostUsername, String city, String country,
                           Date startDate, Date endDate, int nights, double totalCost,
                           String username, int guestCount) {
        this.accommodationId = accommodationId;
        this.hostUsername = hostUsername;
        this.city = city;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nights = nights;
        this.totalCost = totalCost;
        this.username = username;
        this.guestCount = guestCount;
    }

    // Getter e Setter
    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNights() {
        return nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    @Override
    public String toString() {
        return "ArchivedBooking{" +
                "accommodationId='" + accommodationId + '\'' +
                ", hostUsername='" + hostUsername + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", nights=" + nights +
                ", totalCost=" + totalCost +
                ", username='" + username + '\'' +
                ", guestCount=" + guestCount +
                '}';
    }
}
