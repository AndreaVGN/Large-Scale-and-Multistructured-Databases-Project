package com.example.WanderHub.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import com.example.WanderHub.demo.utility.OccupiedPeriod;

//@Document(collection = "accommodations")
public class Accommodation {
    @Id
    private int accommodationId;
    private String description;
    private String type;
    private boolean[] facilities;
    private String place;
    private String address;
    private List<OccupiedPeriod> occupiedDates; // Elenco dei periodi occupati
    private int maxGuestSize;
    private int costPerNight;
    private double averageRate;
    private String[] photos;

    @Field("books") // Embedding the books array inside Accommodation
    private List<Book> books;

    // Costruttore vuoto
    public Accommodation() {
    }

    // Costruttore con parametri
    public Accommodation(int accommodationId, String description, String type, boolean[] facilities, String place, String address,
                         List<OccupiedPeriod> occupiedDates, int maxGuestSize, int costPerNight, double averageRate,
                         String[] photos, List<Book> books) {
        this.accommodationId = accommodationId;
        this.description = description;
        this.type = type;
        this.facilities = facilities;
        this.place = place;
        this.address = address;
        this.occupiedDates = occupiedDates;
        this.maxGuestSize = maxGuestSize;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photos = photos;
        this.books = books;
    }

    // Getter e Setter
    public int getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(int accommodationId) {
        this.accommodationId = accommodationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean[] getFacilities() {
        return facilities;
    }

    public void setFacilities(boolean[] facilities) {
        this.facilities = facilities;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OccupiedPeriod> getOccupiedDates() {
        return occupiedDates;
    }

    public void setOccupiedDates(List<OccupiedPeriod> occupiedDates) {
        this.occupiedDates = occupiedDates;
    }

    public int getMaxGuestSize() {
        return maxGuestSize;
    }

    public void setMaxGuestSize(int maxGuestSize) {
        this.maxGuestSize = maxGuestSize;
    }

    public int getCostPerNight() {
        return costPerNight;
    }

    public void setCostPerNight(int costPerNight) {
        this.costPerNight = costPerNight;
    }

    public double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(double averageRate) {
        this.averageRate = averageRate;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "accommodationId=" + accommodationId +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", facilities=" + java.util.Arrays.toString(facilities) +
                ", place='" + place + '\'' +
                ", address='" + address + '\'' +
                ", occupiedDates=" + occupiedDates +
                ", maxGuestSize=" + maxGuestSize +
                ", costPerNight=" + costPerNight +
                ", averageRate=" + averageRate +
                ", photos=" + java.util.Arrays.toString(photos) +
                ", books=" + books +
                '}';
    }
}

