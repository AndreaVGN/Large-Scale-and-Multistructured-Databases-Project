package com.example.WanderHub.demo.DTO;


public class AverageBookingResultDTO {
    private String city;
    private double averageDays;

    public AverageBookingResultDTO(String city, double averageDays) {
        this.city = city;
        this.averageDays = averageDays;
    }

    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }

    public double getAverageDays() {
        return averageDays;
    }

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

