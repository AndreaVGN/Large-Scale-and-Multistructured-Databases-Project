package com.example.WanderHub.demo.DTO;




public class CityAverageAgeDTO {
    private String city;
    private Double averageAge;

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

