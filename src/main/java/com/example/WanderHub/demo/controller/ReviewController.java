package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.AccommodationService;
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

    @Autowired
    private AccommodationService accommodationService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }


    @GetMapping("/{hostUsername}/{id}/AccommodationReviews")
    public ResponseEntity<?> viewAccommodationReviews(@PathVariable String hostUsername, @PathVariable int id, HttpSession session) {
        if (!SessionUtilility.isLogged(session, hostUsername)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        List<ReviewDTO> reviews = reviewService.viewAccommodationReviews(hostUsername, id);

        if (reviews.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No reviews found for this host.");
        }

        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{username}/{accommodationId}/newDraftReview")
    public ResponseEntity<?> writeDraftReview(@PathVariable String username, @PathVariable ObjectId accommodationId, @RequestBody Review review, HttpSession session) {
        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        review.setUsername(username);
        review.setDate(LocalDate.now());

        reviewService.addDraftReviewToAccommodation(username,accommodationId,review);
        return new ResponseEntity<>("Bozza aggiunta con successo!", HttpStatus.OK);
    }

    @PutMapping("/{username}/{accommodationId}/newReview")
    public ResponseEntity<?> writeReview(@PathVariable String username, @PathVariable ObjectId accommodationId, @RequestBody Review review, HttpSession session) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        review.setUsername((String) session.getAttribute("user"));
        review.setDate(LocalDate.now());

        reviewService.addReviewToAccommodation(username,accommodationId,review);

        return new ResponseEntity<>("Review added successfully!", HttpStatus.OK);
    }











}
