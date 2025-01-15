package com.example.WanderHub.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Accomodation {
    @JsonProperty("id")
    private int accomodationId;
    private String description;
    private String type;
    private boolean[] facilities;
    private String place;
    private String address;
    private String[] dates;
    private int maxGuestSize;
    private int costPerNight;
    private double averageRate;
    private String[] photos;


    public Accomodation() {
    }

    public Accomodation(int accomodationId, String description, String type, boolean[] facilities, String place, String address, String[] dates, int maxGuestSize, int costPerNight, double averageRate, String[] photos) {
        this.accomodationId = accomodationId;
        this.description = description;
        this.type = type;
        this.facilities = facilities;
        this.place = place;
        this.address = address;
        this.dates = dates;
        this.maxGuestSize = maxGuestSize;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photos = photos;
    }



    public int getAccomodationId() {return accomodationId;}
    public String getDescription() {return description;}
    public String getType() {return type;}
    public boolean[] getFacilities() {return facilities;}
    public String getPlace() {return place;}
    public String getAddress() {return address;}
    public String[] getDates() {return dates;}
    public int getMaxGuestSize() {return maxGuestSize;}
    public int getCostPerNight() {return costPerNight;}
    public double getAverageRate() {return averageRate;}
    public String[] getPhotos() {return photos;}

    public void setAccomodationId(int accomodationId) {this.accomodationId = accomodationId;}
    public void setDescription(String description) {this.description = description;}
    public void setType(String type) {this.type = type;}
    public void setFacilities(boolean[] facilities) {this.facilities = facilities;}
    public void setPlace(String place) {this.place = place;}
    public void setAddress(String address) {this.address = address;}
    public void setDates(String[] dates) {this.dates = dates;}
    public void setMaxGuestSize(int maxGuestSize) {this.maxGuestSize = maxGuestSize;}
    public void setCostPerNight(int costPerNight) {this.costPerNight = costPerNight;}
    public void setAverageRate(double averageRate) {this.averageRate = averageRate;}
    public void setPhotos(String[] photos) {this.photos = photos;}

    @Override
    public String toString() {
        return "Accomodation{" +
                "accomodationId=" + accomodationId +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", facilities=" + java.util.Arrays.toString(facilities) +
                ", place='" + place + '\'' +
                ", address='" + address + '\'' +
                ", dates=" + java.util.Arrays.toString(dates) +
                ", maxGuestSize=" + maxGuestSize +
                ", costPerNight=" + costPerNight +
                ", averageRate=" + averageRate +
                ", photos=" + java.util.Arrays.toString(photos) +
                '}';
    }

}
