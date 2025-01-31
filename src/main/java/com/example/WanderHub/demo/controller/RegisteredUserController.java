package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.ReviewDTO;
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

    // Endpoint per aggiungere una prenotazione a un'accommodation scelta dal cliente
    @PutMapping("/{accommodationId}/addBook/{username}")
    public ResponseEntity<Accommodation> addBookToAccommodation(
            @PathVariable String username,
            @PathVariable int accommodationId,
            @RequestBody Book newBook) {

        // Aggiungi la nuova prenotazione alla casa selezionata dall'utente
        Accommodation updatedAccommodation = accommodationService.addBookToAccommodation(username, accommodationId, newBook);

        // Restituisci l'accommodation aggiornata
        return new ResponseEntity<>(updatedAccommodation, HttpStatus.OK);
    }

    @PutMapping("/{username}/addAccommodation")
    public ResponseEntity<RegisteredUser> addAccommodation(
            @PathVariable String username,
            @RequestBody Accommodation accommodation) {

        RegisteredUser updatedRegisteredUser = accommodationService.addAccommodationToRegisteredUser(username, accommodation);


        return new ResponseEntity<>(updatedRegisteredUser, HttpStatus.OK);
    }

    @GetMapping("/{username}/pendingBookings")
    public ResponseEntity<List<Book>> getPendingBookings(@PathVariable String username) {
        List<Book> pendingBookings = accommodationService.getPendingBookings(username);
        return new ResponseEntity<>(pendingBookings, HttpStatus.OK);
    }

    @GetMapping("/{username}/reviews")
    public ResponseEntity<List<Review>> getReviewsByUsername(@PathVariable String username) {
        List<Review> reviews = accommodationService.getReviewsByUsername(username);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{username}/accommodations")
    public ResponseEntity<List<Accommodation>> getAccommodationsByHost(@PathVariable String username) {
        List<Accommodation> accommodations = accommodationService.findAccommodationsByHost(username);
        return new ResponseEntity<>(accommodations, HttpStatus.OK);
    }

    @DeleteMapping("/{username}/accommodation/{accommodationId}/book/{bookId}")
    public ResponseEntity<String> deleteBook(
            @PathVariable String username,
            @PathVariable int accommodationId,
            @PathVariable int bookId) {

        boolean isDeleted = accommodationService.deleteBook(username, accommodationId, bookId);

        if (isDeleted) {
            return ResponseEntity.ok("Prenotazione cancellata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Non è possibile cancellare la prenotazione. Verifica che la prenotazione esista e che sia più vicina di due giorni alla data di inizio.");
        }
    }

}
