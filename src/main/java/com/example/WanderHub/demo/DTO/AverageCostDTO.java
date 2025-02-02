package com.example.WanderHub.demo.DTO;

public class AverageCostDTO {
    private String city;
    private int guestCount;
    private double averageCostPerNight;

    // Costruttore di default
    public AverageCostDTO() {
    }

    public AverageCostDTO(String city, int guestCount, double averageCostPerNight) {
        this.city = city;
        this.guestCount = guestCount;
        this.averageCostPerNight = averageCostPerNight;
    }

    // Getter e Setter per city
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter e Setter per guestCount
    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    // Getter e Setter per averageCostPerNight
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

