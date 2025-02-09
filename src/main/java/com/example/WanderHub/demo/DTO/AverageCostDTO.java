package com.example.WanderHub.demo.DTO;


public class AverageCostDTO {
    private String city;
    private int guestCount;
    private double averageCostPerNight;

    public AverageCostDTO() {
    }

    public AverageCostDTO(String city, int guestCount, double averageCostPerNight) {
        this.city = city;
        this.guestCount = guestCount;
        this.averageCostPerNight = averageCostPerNight;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public double getAverageCostPerNight() {
        return averageCostPerNight;
    }

    public void setAverageCostPerNight(double averageCostPerNight) {
        this.averageCostPerNight = averageCostPerNight;
    }

    // Metodo toString
    @Override
    public String toString() {
        return "AverageCostDTO{" +
                "city='" + city + '\'' +
                ", guestCount=" + guestCount +
                ", averageCostPerNight=" + averageCostPerNight +
                '}';
    }
}

