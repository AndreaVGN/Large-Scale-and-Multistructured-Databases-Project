package com.example.WanderHub.demo.DTO;

public class AverageCostDTO {
    private String city;
    private int guestCount;
    private double averageCostPerNight;

    public AverageCostDTO(String city ,int guestCount, double averageCostPerNight) {
        this.city = city;
        this.guestCount = guestCount;
        this.averageCostPerNight = averageCostPerNight;
    }
}
