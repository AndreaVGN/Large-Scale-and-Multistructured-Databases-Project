package com.example.WanderHub.demo.service;


import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.ReviewRepository;
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
}
