package com.example.WanderHub.demo.DTO;

import java.util.List;
import com.example.WanderHub.demo.model.Accommodation;  // Assumendo che Review sia un modello esistente


public class AccommodationDTO {
    private String description;
    private String type;
    private String city;
    private String hostUsername;
    private int costPerNight;
    private double averageRate;
    private String photo;

    // Costruttore, getter, setter
    public AccommodationDTO(String description, String type, String city, String hostUsername, int costPerNight, double averageRate, String photo) {
        this.description = description;
        this.type = type;
        this.city = city;
        this.hostUsername = hostUsername;
        this.costPerNight = costPerNight;
        this.averageRate = averageRate;
        this.photo = photo;
    }

    // Getter e Setter
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

    public String getPhotos() { return photo; }
    public void setPhotos(String photo) { this.photo = photo; }
}
