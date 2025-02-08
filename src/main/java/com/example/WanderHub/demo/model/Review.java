package com.example.WanderHub.demo.model;

import lombok.*;

import java.time.LocalDate;


public class Review {
    private String reviewText;
    private double rating;
    private String username;
    private LocalDate date;

    // Costruttore senza parametri
    public Review() {}

    // Costruttore con parametri
    public Review( String reviewText, double rating, String username, LocalDate reviewDate) {
        this.reviewText = reviewText;
        this.rating = rating;
        this.username = username;
        this.date = reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate reviewDate) {
        this.date = reviewDate;
    }

    // Metodo toString
    @Override
    public String toString() {
        return "Review{" +
                ", reviewText='" + reviewText + '\'' +
                ", rating=" + rating +
                ", username='" + username + '\'' +
                ", reviewDate=" + date +
                '}';
    }
}
