package com.example.WanderHub.demo.DTO;

public class FacilityRatingDTO {
    private String facility;
    private String city;
    private double averageRating;

    public FacilityRatingDTO(String city, String facility, double averageRating) {
        this.city = city;
        this.facility = facility;
        this.averageRating = averageRating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
