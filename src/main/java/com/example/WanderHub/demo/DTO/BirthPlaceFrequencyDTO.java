package com.example.WanderHub.demo.DTO;


public class BirthPlaceFrequencyDTO {
    private String birthPlace;
    private int count;

    // Costruttore, getter, setter e toString
    public BirthPlaceFrequencyDTO(String birthPlace, int count) {
        this.birthPlace = birthPlace;
        this.count = count;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "BirthPlaceFrequency{" +
                "birthPlace='" + birthPlace + '\'' +
                ", count=" + count +
                '}';
    }
}
