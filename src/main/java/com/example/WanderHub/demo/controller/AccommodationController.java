package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.service.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accommodations")
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    // Creazione di una nuova sistemazione con books embeddata
    @PostMapping
    public Accommodation createAccommodation(@RequestBody Accommodation accommodation) {
        return accommodationService.createAccommodation(accommodation);
    }

    // Recupero una sistemazione per id
    @GetMapping("/{id}")
    public Accommodation getAccommodation(@PathVariable int id) {
        return accommodationService.getAccommodationById(id);
    }

    // Eliminazione di una sistemazione per id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable int id) {
        boolean isDeleted = accommodationService.deleteAccommodationById(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Ricerca di sistemazioni disponibili con parametri
    @GetMapping("/findAccommodations")
    public List<Accommodation> findAccommodations(
            @RequestParam("city") String city,
            @RequestParam("guestSize") int minGuests,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        return accommodationService.findAvailableAccommodations(city, minGuests, startDate, endDate);
    }

    @PutMapping("/{accommodationId}/addBook")
    public ResponseEntity<Accommodation> addBookToAccommodation(
            @PathVariable int accommodationId,
            @RequestBody Book newBook) {

        // Aggiungi la nuova book alla sistemazione
        Accommodation updatedAccommodation = accommodationService.addBookToAccommodation(accommodationId, newBook);


        return new ResponseEntity<>(updatedAccommodation, HttpStatus.OK);
    }

    @PutMapping("/{accommodationId}/addReview")
    public ResponseEntity<Accommodation> addReviewToAccommodation(
            @PathVariable int accommodationId,
            @RequestBody Review newReview) {

        // Aggiungi la nuova recensione alla sistemazione
        Accommodation updatedAccommodation = accommodationService.addReviewToAccommodation(accommodationId, newReview);

        return new ResponseEntity<>(updatedAccommodation, HttpStatus.OK);
    }
    @GetMapping("/{username}/findPersonalAccommodations")
    public ResponseEntity<List<Accommodation>> findPersonalAccommodations(@PathVariable String username) {
        List<Accommodation> personalAccommodations = accommodationService.findAccommodationsByUsername(username);
        return new ResponseEntity<>(personalAccommodations,HttpStatus.OK);
    }
}

