package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.ReviewService;
import com.example.WanderHub.demo.utility.SessionUtilility;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;



    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PostMapping("/{username}/{accommodationId}/newDraftReview")
    public ResponseEntity<?> writeDraftReview(@PathVariable String username, @PathVariable String accommodationId, @RequestBody Review review, HttpSession session) {
        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        review.setUsername(username);
        review.setDate(LocalDate.now());


        reviewService.addDraftReviewToAccommodation(username,accommodationId,review);
        return new ResponseEntity<>("Bozza aggiunta con successo!", HttpStatus.OK);
    }

    @PutMapping("/{username}/{accommodationId}/newReview")
    public ResponseEntity<?> writeReview(@PathVariable String username, @PathVariable String accommodationId, @RequestBody Review review, HttpSession session) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        review.setUsername((String) session.getAttribute("user"));
        review.setDate(LocalDate.now());

        reviewService.addReviewToAccommodation(username,accommodationId,review);

        return new ResponseEntity<>("Review added successfully!", HttpStatus.OK);
    }











}
