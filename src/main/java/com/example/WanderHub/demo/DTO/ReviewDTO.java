package com.example.WanderHub.demo.DTO;

import java.util.List;
import com.example.WanderHub.demo.model.Review;  // Assumendo che Review sia un modello esistente

public class ReviewDTO {

    private List<Review> reviews;  // Lista di recensioni

    // Getter e setter
    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    // Costruttore
    public ReviewDTO(List<Review> reviews) {
        this.reviews = reviews;
    }
}

/*

package com.example.WanderHub.demo.dto;

public class ReviewDTO {
    private int reviewId;
    private String reviewText;
    private double rating;

    // Costruttore senza parametri
    public ReviewDTO() {}

    // Costruttore con parametri
    public ReviewDTO(int reviewId, String reviewText, double rating) {
        this.reviewId = reviewId;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    // Getter e Setter
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
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

    // Metodo toString
    @Override
    public String toString() {
        return "ReviewDTO{" +
                "reviewId=" + reviewId +
                ", reviewText='" + reviewText + '\'' +
                ", rating=" + rating +
                '}';
    }
}
 */