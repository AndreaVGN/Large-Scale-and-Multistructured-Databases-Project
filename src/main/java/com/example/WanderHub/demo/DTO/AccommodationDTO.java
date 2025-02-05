package com.example.WanderHub.demo.DTO;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.WanderHub.demo.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)  // Include solo campi non nulli
public class AccommodationDTO {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)  // Ignora se vale 0
    private String accommodationId;
    //private ObjectId accommodationId;
    private String description;
    private String type;
    private String city;
    private String hostUsername;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT) // Esclude numeri se valgono 0 o 0.0
    private int costPerNight;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT) // Esclude numeri se valgono 0 o 0.0
    private double averageRate;
    private List<String> photos;  // Cambiato per supportare sia una singola foto che una lista di foto
    private List<Book> books;
    private List<Review> reviews;  // Aggiunto campo reviews

    // Costruttore privato per forzare l'uso dei metodi factory
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

    // Costruttore per il caso con informazioni limitate (senza ID e reviews)
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

    public static AccommodationDTO fromBasicInfo(Accommodation accommodation) {
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                null, // Non necessario per il caso basic
                null, // Non necessario per il caso basic
                null, // Non necessario per il caso basic
                0,   // Non necessario per il caso basic
                0.0, // Non necessario per il caso basic
                null, // Non necessario per il caso basic
                null  // Non necessario per il caso basic
        );
    }

    // 🔹 Factory method per dati completi (tranne books)
    public static AccommodationDTO fromFullDetails(Accommodation accommodation) {
        List<String> allPhotos = (accommodation.getPhotos() != null && accommodation.getPhotos().length > 0) ?
                Arrays.asList(accommodation.getPhotos()) : null;  // Restituisce tutte le foto come lista
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

    // 🔹 Factory method per il caso con solo alcuni dati
    public static AccommodationDTO fromLimitedInfo(Accommodation accommodation) {
        String firstPhoto = (accommodation.getPhotos() != null && accommodation.getPhotos().length > 0) ?
                accommodation.getPhotos()[0] : null; // Restituisce la prima foto o null
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                accommodation.getType(),
                accommodation.getCity(),
                accommodation.getHostUsername(),
                accommodation.getCostPerNight(),
                accommodation.getAverageRate(),
                firstPhoto != null ? Arrays.asList(firstPhoto) : null  // Converte la singola foto in lista
        );
    }
    public static AccommodationDTO fromSomeInfo(Accommodation accommodation){
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

    // Getter e Setter
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
