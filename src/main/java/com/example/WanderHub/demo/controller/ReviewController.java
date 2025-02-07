package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }




}
