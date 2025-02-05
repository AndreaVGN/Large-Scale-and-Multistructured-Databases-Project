package com.example.WanderHub.demo.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.WanderHub.demo.utility.OccupiedPeriod;

@Document(collection = "accommodations")  // La collezione in MongoDB
public class Accommodation {
    @Id
    private String accommodationId;

    private String description;
    private String type;
    private Map<String, Integer> facilities;
    private String place;
    private String city;
    private String address;
    private String hostUsername;
    private double latitude;
    private double longitude;
    private List<OccupiedPeriod> occupiedDates; // Elenco dei periodi occupati
    private int maxGuestSize;
    private int costPerNight;
    private double averageRate;
    private String[] photos;

    @Field("books") // Embedding the books array inside Accommodation
    private List<Book> books;

    @Field("reviews") // Embedding the reviews array inside Accommodation
    private List<Review> reviews;

    // Costruttore senza parametri
    public Accommodation() {}

    // Costruttore con parametri
    public Accommodation(String description, String type, Map<String, Integer> facilities, String place,
                         String city, String address, String hostUsername, double latitude, double longitude,
                         List<OccupiedPeriod> occupiedDates, int maxGuestSize, int costPerNight, double averageRate,
                         String[] photos, List<Book> books, List<Review> reviews) {
        this.description = description;
        this.type = type;
        this.facilities = facilities;
        this.place = place;
        this.city = city;
        this.address = address;
        this.hostUsername = hostUsername;
        this.latitude = latitude;
        this.longitude = longitude;
        this.occupiedDates = occupiedDates;
        this.maxGuestSize = maxGuestSize;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photos = photos;
        this.books = books;
        this.reviews = reviews;
    }

    public String getAccommodationId() { return accommodationId; }
    public void setAccommodationId(String accommodationId) { this.accommodationId = accommodationId; }
    // Getter e Setter
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

    public Map<String, Integer> getFacilities() {
        return facilities;
    }

    public void setFacilities(Map<String, Integer> facilities) {
        this.facilities = facilities;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    // Metodo toString
    @Override
    public String toString() {
        return "Accommodation{" +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", facilities=" + facilities.toString() +
                ", place='" + place + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", hostUsername='" + hostUsername + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", occupiedDates=" + occupiedDates +
                ", maxGuestSize=" + maxGuestSize +
                ", costPerNight=" + costPerNight +
                ", averageRate=" + averageRate +
                ", photos=" + Arrays.toString(photos) +
                ", books=" + books +
                ", reviews=" + reviews +
                '}';
    }
}
