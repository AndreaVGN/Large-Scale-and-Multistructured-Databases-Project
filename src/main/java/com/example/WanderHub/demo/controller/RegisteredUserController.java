package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.BookDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.RegisteredUserService;
import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<?> addBookToAccommodation(
            @PathVariable String username,
            @PathVariable int accommodationId,
            @RequestBody Book newBook,
            HttpSession session) {

        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");


        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }

        newBook.setUsername(loggedInUser.getUsername());
        newBook.setEmail(loggedInUser.getEmail());
        newBook.setBirthPlace(loggedInUser.getBirthPlace());
        newBook.setAddress(loggedInUser.getAddress());
        newBook.setAddressNumber(loggedInUser.getAddressNumber());

        newBook.setBirthDate(loggedInUser.getBirthDate());
        // Aggiungi la nuova prenotazione alla casa selezionata dall'utente
        Accommodation updatedAccommodation = accommodationService.addBookToAccommodation(username, accommodationId, newBook);

        return new ResponseEntity<>("Prenotazione avvenuta con successo!", HttpStatus.OK);

    }


    @GetMapping("/{username}/pendingBookings")
    public ResponseEntity<?> getPendingBookings(@PathVariable String username, HttpSession session) {

        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");

        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }
        List<BookDTO> pendingBookings = accommodationService.getPendingBookings(username);
        if (pendingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna accommodation trovata per questo host.");
        }
        return ResponseEntity.ok(pendingBookings);
    }

    @GetMapping("/{username}/reviews")
    public ResponseEntity<?> getReviewsByUsername(@PathVariable String username, HttpSession session) {

        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");

        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }
        List<Review> reviews = accommodationService.getReviewsByUsername(username);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna accommodation trovata per questo host.");
        }
        return ResponseEntity.ok(reviews);
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
