package com.example.WanderHub.demo.DTO;

import com.example.WanderHub.demo.model.Accommodation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.WanderHub.demo.model.Review;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)  // Include solo campi non nulli
public class AccommodationDTO {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)  // Ignora se vale 0
    private int accommodationId;
    private String description;
    private String type;
    private String city;
    private String hostUsername;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT) // Esclude numeri se valgono 0 o 0.0
    private int costPerNight;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT) // Esclude numeri se valgono 0 o 0.0
    private double averageRate;
    private String photo;
    private List<Review> reviews;  // Aggiunto campo reviews

    // Costruttore privato per forzare l'uso dei metodi factory
    public AccommodationDTO(int accommodationId, String description, String type, String city, String hostUsername,
                             int costPerNight, double averageRate, String photo, List<Review> reviews) {
        this.accommodationId = accommodationId;
        this.description = description;
        this.type = type;
        this.city = city;
        this.hostUsername = hostUsername;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photo = photo;
        this.reviews = reviews;
    }

    private AccommodationDTO(int accommodationId, String description) {
        this.accommodationId = accommodationId;
        this.description = description;
    }

    // Costruttore per il caso con informazioni limitate (senza ID e reviews)
    private AccommodationDTO(String description, String type, String city, String hostUsername, int costPerNight, double averageRate, String photo) {
        this.description = description;
        this.type = type;
        this.city = city;
        this.hostUsername = hostUsername;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photo = photo;
    }

    public static AccommodationDTO fromBasicInfo(Accommodation accommodation) {
        AccommodationDTO prova =  new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription()
        );
        System.out.println(prova.averageRate);
        return prova;
    }

    // 🔹 Factory method per dati completi (tranne books)
    public static AccommodationDTO fromFullDetails(Accommodation accommodation) {
        return new AccommodationDTO(
                accommodation.getAccommodationId(),
                accommodation.getDescription(),
                accommodation.getType(),
                accommodation.getCity(),
                accommodation.getHostUsername(),
                accommodation.getCostPerNight(),
                accommodation.getAverageRate(),
                (accommodation.getPhotos() != null && accommodation.getPhotos().length > 0) ? accommodation.getPhotos()[0] : null,
                accommodation.getReviews()
        );
    }

    // 🔹 Factory method per il caso con solo alcuni dati
    public static AccommodationDTO fromLimitedInfo(Accommodation accommodation) {
        return new AccommodationDTO(
                accommodation.getDescription(),
                accommodation.getType(),
                accommodation.getCity(),
                accommodation.getHostUsername(),
                accommodation.getCostPerNight(),
                accommodation.getAverageRate(),
                (accommodation.getPhotos() != null && accommodation.getPhotos().length > 0) ? accommodation.getPhotos()[0] : null
        );
    }

    // Getter e Setter
    public int getAccommodationId() { return accommodationId; }
    public void setAccommodationId(int accommodationId) { this.accommodationId = accommodationId; }

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

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
