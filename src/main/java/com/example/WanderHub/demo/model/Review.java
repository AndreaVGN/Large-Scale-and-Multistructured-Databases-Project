package com.example.WanderHub.demo.model;

public class Review {
    private int reviewId;
    private String reviewText;
    private double rating;

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
        return "Review{" +
                "reviewId=" + reviewId +
                ", reviewText='" + reviewText + '\'' +
                ", rating=" + rating +
                '}';
    }
}

