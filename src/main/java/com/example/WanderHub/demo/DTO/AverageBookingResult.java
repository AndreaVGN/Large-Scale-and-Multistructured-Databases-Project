package com.example.WanderHub.demo.DTO;

public class AverageBookingResult {
    private String city;
    private double averageDays;

    // Costruttore
    public AverageBookingResult(String city, double averageDays) {
        this.city = city;
        this.averageDays = averageDays;
    }

    // Costruttore senza parametri (necessario per alcune librerie di serializzazione)
    public AverageBookingResult() {
    }

    // Getter per city
    public String getCity() {
        return city;
    }

    // Setter per city
    public void setCity(String city) {
        this.city = city;
    }

    // Getter per averageDays
    public double getAverageDays() {
        return averageDays;
    }

    // Setter per averageDays
    public void setAverageDays(double averageDays) {
        this.averageDays = averageDays;
    }

    @Override
    public String toString() {
        return "AverageBookingResult{" +
                "city='" + city + '\'' +
                ", averageDays=" + averageDays +
                '}';
    }
}

