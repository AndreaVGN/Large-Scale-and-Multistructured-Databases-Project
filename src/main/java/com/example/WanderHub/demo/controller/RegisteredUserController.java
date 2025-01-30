package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class RegisteredUserController {

    // Iniezione del servizio tramite @Autowired
    @Autowired
    private RegisteredUserService registeredUserService;
    @Autowired
    private AccommodationService accommodationService;

    @PostMapping
    public RegisteredUser createRegisteredUser(@RequestBody RegisteredUser registeredUser) {
        return registeredUserService.createRegisteredUser(registeredUser);
    }

    @GetMapping("/{user}")
    public RegisteredUser getRegisteredUser(@PathVariable String user) {
        return registeredUserService.getRegisteredUserByUsername(user);
    }

    @DeleteMapping("/{user}")
    public ResponseEntity<Void> deleteRegisteredUser(@PathVariable String user) {
        boolean isDeleted = registeredUserService.deleteRegisteredUserByUsername(user);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
/*
    @PutMapping("/{username}/addBook")
    public ResponseEntity<RegisteredUser> addBookToAccommodation(
            @PathVariable String username,
            @RequestBody Book newBook) {

        // Aggiungi la nuova book alla sistemazione
        RegisteredUser updatedRegisteredUser = accommodationService.addBookToRegisteredUser(username, newBook);


        return new ResponseEntity<>(updatedRegisteredUser, HttpStatus.OK);
    }

    @PutMapping("/{username}/addAccommodation")
    public ResponseEntity<RegisteredUser> addAccommodation(
            @PathVariable String username,
            @RequestBody Accommodation accommodation) {

        RegisteredUser updatedRegisteredUser = accommodationService.addAccommodationToRegisteredUser(username, accommodation);


        return new ResponseEntity<>(updatedRegisteredUser, HttpStatus.OK);
    }
*/
    @GetMapping("/{username}/pendingBookings")
    public ResponseEntity<List<Book>> getPendingBookings(@PathVariable String username) {
        List<Book> pendingBookings = accommodationService.getPendingBookings(username);
        return new ResponseEntity<>(pendingBookings, HttpStatus.OK);
    }

    @GetMapping("/{username}/reviews")
    public ResponseEntity<List<Review>> getReviewsByAccommodationId(@PathVariable String username) {
        List<Review> accommodationReviews = accommodationService.getReviewsByUsername(username);
        return new ResponseEntity<>(accommodationReviews, HttpStatus.OK);
    }
/*
    @GetMapping("/{username}/viewAccommodations")
    public ResponseEntity<List<Integer>> getAccommodations(@PathVariable String username) {
        List<Integer> accommodations = accommodationService.getAccommodationByUsername(username);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @DeleteMapping("/{username}/deleteAccommodation/{id}")
    public ResponseEntity<Void> deleteAccommodationProperty(@PathVariable String username, @PathVariable int id) {


        boolean aux = accommodationService.deleteAccommodation(username, id);
        if (aux) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }*/
}
