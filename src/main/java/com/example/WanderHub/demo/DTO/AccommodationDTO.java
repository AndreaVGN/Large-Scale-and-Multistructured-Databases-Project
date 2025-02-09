package com.example.WanderHub.demo.DTO;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.WanderHub.demo.model.Review;


import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccommodationDTO {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String accommodationId;
    private String description;
    private String type;
    private String city;
    private String hostUsername;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int costPerNight;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private double averageRate;
    private List<String> photos;
    private List<Book> books;
    private List<Review> reviews;

    private AccommodationDTO(String accommodationId, String description, String type, String city, String hostUsername,
                            int costPerNight, double averageRate, List<String> photos, List<Review> reviews) {
        this.accommodationId = accommodationId;
        this.description = description;
        this.type = type;
        this.city = city;
        this.hostUsername = hostUsername;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photos = photos;
        this.reviews = reviews;
    }

    private AccommodationDTO(String accommodationId, String description, String type, String city, String hostUsername, int costPerNight, double averageRate, List<String> photos) {
        this.accommodationId = accommodationId;
        this.description = description;
        this.type = type;
        this.city = city;
        this.hostUsername = hostUsername;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photos = photos;
    }

    private AccommodationDTO(String accommodationId, String description, List<Book> books, List<Review> reviews){
       this.accommodationId = accommodationId;
        this.description = description;
        this.books = books;
        this.reviews = reviews;
    }

    public static AccommodationDTO idDescription(Accommodation accommodation) {
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                null,
                null,
                null,
                0,
                0.0,
                null,
                null
        );
    }

    public static AccommodationDTO fromFullDetails(Accommodation accommodation) {
        List<String> allPhotos = (accommodation.getPhotos() != null && accommodation.getPhotos().length > 0) ?
                Arrays.asList(accommodation.getPhotos()) : null;
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                accommodation.getType(),
                accommodation.getCity(),
                accommodation.getHostUsername(),
                accommodation.getCostPerNight(),
                accommodation.getAverageRate(),
                allPhotos,
                accommodation.getReviews()
        );
    }

    public static AccommodationDTO fromLimitedInfo(Accommodation accommodation) {
        String firstPhoto = (accommodation.getPhotos() != null && accommodation.getPhotos().length > 0) ?
                accommodation.getPhotos()[0] : null;
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                accommodation.getType(),
                accommodation.getCity(),
                accommodation.getHostUsername(),
                accommodation.getCostPerNight(),
                accommodation.getAverageRate(),
                firstPhoto != null ? Arrays.asList(firstPhoto) : null
        );
    }
    public static AccommodationDTO idDescriptionBooks(Accommodation accommodation){
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                accommodation.getBooks(),
                null
        );
    }

    public static AccommodationDTO withReviews(Accommodation accommodation) {
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                null,
                accommodation.getReviews()
        );
    }

    public String getAccommodationId() { return accommodationId; }
    public void setAccommodationId(String accommodationId) { this.accommodationId = accommodationId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getHostUsername() { return hostUsername; }
    public void setHostUsername(String hostUsername) { this.hostUsername = hostUsername; }

    public int getCostPerNight() { return costPerNight; }
    public void setCostPerNight(int costPerNight) { this.costPerNight = costPerNight; }

    public double getAverageRate() { return averageRate; }
    public void setAverageRate(double averageRate) { this.averageRate = averageRate; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
