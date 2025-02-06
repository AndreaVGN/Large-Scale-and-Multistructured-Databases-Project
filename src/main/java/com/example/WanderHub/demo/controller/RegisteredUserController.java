package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.BookDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.BookingService;
import com.example.WanderHub.demo.service.RegisteredUserService;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @Autowired
    private BookingService bookingService;

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
            @PathVariable ObjectId accommodationId,
            @RequestBody Book newBook,
            HttpSession session) {


        String usernam = (String) session.getAttribute("user");
        String email = (String) session.getAttribute("email");
        String birthPlace = (String) session.getAttribute("birthPlace");
        String address = (String) session.getAttribute("address");
        int addressNumber = (int) session.getAttribute("addressNumber");
        String birthDate = (String) session.getAttribute("birthDate");

/*
        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");*/


        if (usernam == null || !usernam.equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }

        newBook.setUsername(usernam);
        newBook.setEmail(email);
        newBook.setBirthPlace(birthPlace);
        newBook.setAddress(address);
        newBook.setAddressNumber(addressNumber);


        newBook.setBirthDate(birthDate);
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
        List<AccommodationDTO> pendingBookings = accommodationService.getPendingBookings(username);
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
        List<AccommodationDTO> reviews = accommodationService.getReviewsByUsername(username);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna accommodation trovata per questo host.");
        }
        return ResponseEntity.ok(reviews);
    }


    @DeleteMapping("/{username}/accommodation/{accommodationId}/book/{bookId}")
    public ResponseEntity<String> deleteBook(
            @PathVariable String username,
            @PathVariable ObjectId accommodationId,
            @PathVariable int bookId) {

      //  boolean isDeleted = accommodationService.deleteBook(username, accommodationId, bookId);

    /*    if (isDeleted) {
            return ResponseEntity.ok("Prenotazione cancellata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Non è possibile cancellare la prenotazione. Verifica che la prenotazione esista e che sia più vicina di due giorni alla data di inizio.");
        }*/

        return null;
    }
    @PostMapping("/{username}/{accommodationId}/lock")
    public ResponseEntity<String> lockHouse(@PathVariable ObjectId accommodationId, @PathVariable String username, @RequestParam String startDate, @RequestParam String endDate) {
        System.out.println("Controller received startDate: " + startDate + ", endDate: " + endDate);
        boolean success = bookingService.bookHouseReg(accommodationId, username, startDate,endDate);
        if (success) {
            return ResponseEntity.ok("Casa prenotata temporaneamente!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Casa già prenotata da un altro utente. Oppure periodo di tempo non valido");
        }
    }
    @PutMapping("/{username}/{accommodationId}/writeReview")
    public ResponseEntity<?> writeReview(@PathVariable String username, @PathVariable ObjectId accommodationId, @RequestBody Review review, HttpSession session) {
        String usernam = (String) session.getAttribute("user");
        System.out.println(usernam);

        if (usernam == null || !usernam.equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }
        review.setUsername(usernam);
        review.setDate(LocalDate.now());
       Accommodation accommodation = accommodationService.addReviewToAccommodation(username,accommodationId,review);
        return new ResponseEntity<>("Review aggiunte con successo!", HttpStatus.OK);
    }
    @DeleteMapping("/{username}/{accommodationId}/unlock")
    public ResponseEntity<String> unlockHouse(
            @PathVariable ObjectId accommodationId,
            @PathVariable String username,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        boolean success = bookingService.unlockHouseReg(accommodationId, username, startDate, endDate);

        if (success) {
            return ResponseEntity.ok("Casa sbloccata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Errore nello sblocco della casa o la prenotazione non esiste.");
        }
    }
    @PostMapping("/{username}/{accommodationId}/writeBozza")
    public ResponseEntity<?> writeBozza(@PathVariable String username, @PathVariable ObjectId accommodationId, @RequestBody Review review, HttpSession session) {
        String usernam = (String) session.getAttribute("user");
        System.out.println(usernam);

        if (usernam == null || !usernam.equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }
        review.setUsername(usernam);
        review.setDate(LocalDate.now());
        //if(accommodationService.addBozzaToAccommodation(username,accommodationId,review))
        accommodationService.addBozzaToAccommodation(username,accommodationId,review);
        return new ResponseEntity<>("Bozza aggiunta con successo!", HttpStatus.OK);
    }
}
