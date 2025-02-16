package com.example.WanderHub.demo.DTO;

import java.util.List;
import com.example.WanderHub.demo.model.Review;  // Assumendo che Review sia un modello esistente
import lombok.*;

public class ReviewDTO {

    private List<Review> reviews;  // Lista di recensioni

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public ReviewDTO(List<Review> reviews) {
        this.reviews = reviews;
    }
}

