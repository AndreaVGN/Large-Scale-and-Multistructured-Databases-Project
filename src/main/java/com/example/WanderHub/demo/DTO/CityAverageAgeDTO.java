package com.example.WanderHub.demo.DTO;

import lombok.*;



public class CityAverageAgeDTO {
    private String city;
    private Double averageAge;

    // Costruttori, getter e setter
    public CityAverageAgeDTO(String city, Double averageAge) {
        this.city = city;
        this.averageAge = averageAge;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(Double averageAge) {
        this.averageAge = averageAge;
    }
}

