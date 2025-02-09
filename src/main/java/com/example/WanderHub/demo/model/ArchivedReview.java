package com.example.WanderHub.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "archivedReviews") // Nome della collection in MongoDB
/*
public class ArchivedReview {
    private String accommodationId; // Aggiunto per mantenere il riferimento all'alloggio
    private String username;
    private int rating;
    private String reviewText;
    private LocalDate date; // Data della recensione archiviata


    // Costruttore
    public ArchivedReview(String accommodationId, String username, int rating, String reviewText, LocalDate date) {
        this.accommodationId = accommodationId;  // Usa il setter per convertire l'ObjectId in String
        this.username = username;
        this.rating = rating;
        this.reviewText = reviewText;
        this.date = date;
    }

    // Getter e Setter per accommodationId
    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }

    // Getter e Setter per username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter e Setter per rate
    public double getRate() {
        return rating;
    }

    public void setRate(int rate) {
        this.rating = rate;
    }

    // Getter e Setter per reviewText
    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    // Getter e Setter per date
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}*/

public class ArchivedReview extends Review {

    private String accommodationId;

    public ArchivedReview() {
        super();
    }

    public ArchivedReview(String reviewText, double rating, String username, LocalDate date, String accommodationId) {
        super();
        this.setReviewText(reviewText);
        this.setRating(rating);
        this.setUsername(username);
        this.setDate(date);
        this.accommodationId = accommodationId;
    }

    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }

    @Override
    public String toString() {
        return "ArchivedReview{" +
                "accommodationId='" + accommodationId + '\'' +
                ", reviewText='" + getReviewText() + '\'' +
                ", rating=" + getRating() +
                ", username='" + getUsername() + '\'' +
                ", date=" + getDate() +
                '}';
    }
}


