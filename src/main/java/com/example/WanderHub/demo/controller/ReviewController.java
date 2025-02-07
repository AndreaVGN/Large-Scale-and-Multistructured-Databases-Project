package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AccommodationService accommodationService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @GetMapping("/{hostUsername}/viewAccommodationReviews/{id}")
    public ResponseEntity<?> viewAccommodationReviews(@PathVariable String hostUsername, @PathVariable int id, HttpSession session) {
        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");


        if (loggedInUser == null || !loggedInUser.getUsername().equals(hostUsername)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }

        List<ReviewDTO> reviews = accommodationService.viewAccommodationReviews(hostUsername, id);
        System.out.println(reviews);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna reviews trovata per questo host.");
        }
        return ResponseEntity.ok(reviews);
    }




}
