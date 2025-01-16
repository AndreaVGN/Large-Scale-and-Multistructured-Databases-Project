package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createAccommodation(review);
    }

    @GetMapping("/{id}")
    public Accommodation getAccommodation(@PathVariable int id) {
        return accommodationService.getAccommodationById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable int id) {
        boolean isDeleted = accommodationService.deleteAccommodationById(id);

        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
