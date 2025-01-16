package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReviewService {

    @Autowired
    private ReviewRepository ReviewRepository;

    // Creazione di una nuova sistemazione
    public Review createReview(Review review) {
        return ReviewRepository.save(review);
    }

    public Review getREviewById(int reviewId) {
        return ReviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
    }

    public boolean deleteReviewById(int reviewId) {
        if(ReviewRepository.existsByReviewId(reviewId)) {
            ReviewRepository.deleteByReviewId(reviewId);
            return true;
        }
        return false;
    }

    // Altri metodi per gestire le sistemazioni

}
