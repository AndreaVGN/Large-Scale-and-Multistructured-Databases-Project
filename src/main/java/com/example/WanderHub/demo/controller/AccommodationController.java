package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.utility.SessionUtils;
import jakarta.servlet.http.HttpSession;
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

    @PostMapping("/{username}")
    public ResponseEntity<?> createAccommodation(@PathVariable String username, @RequestBody Accommodation accommodation, HttpSession session) {
        // Controlla se l'utente nella sessione è lo stesso che è nel path
        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");



        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }

        // Logica per creare l'accommodation
        accommodationService.createAccommodation(accommodation);
        return ResponseEntity.ok("Accommodation creata con successo");
    }


    @GetMapping("/{id}")
    public AccommodationDTO getAccommodationById(@PathVariable int id) {
        return accommodationService.getAccommodationById(id);
   }


    @GetMapping("/findAccommodations")
    public List<AccommodationDTO> findAccommodations(
            @RequestParam("city") String place,
            @RequestParam("guestSize") int minGuests,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        return accommodationService.findAvailableAccommodations(place, minGuests, startDate, endDate);
    }


    @GetMapping("/{hostUsername}/viewOwnAccommodations")
    public ResponseEntity<?> viewOwnAccommodations(@PathVariable String hostUsername, HttpSession session) {

        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");



        if (loggedInUser == null || !loggedInUser.getUsername().equals(hostUsername)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }
        List<AccommodationDTO> accommodations = accommodationService.findOwnAccommodations(hostUsername);
        if (accommodations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna accommodation trovata per questo host.");
        }

        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/{hostUsername}/viewAccommodationBooks/{id}")
    public List<Book> viewAccommodationBooks(@PathVariable String hostUsername, @PathVariable int id) {
        return accommodationService.viewAccommodationBooks(hostUsername,id);
    }
    @GetMapping("/{hostUsername}/viewAccommodationReviews/{id}")
    public List<ReviewDTO> viewAccommodationReviews(@PathVariable String hostUsername, @PathVariable int id) {
        return accommodationService.viewAccommodationReviews(hostUsername, id);
    }

    // Endpoint per aggiungere una prenotazione a un'accommodation scelta dal cliente
    @PutMapping("/{accommodationId}/addBook")
    public ResponseEntity<Accommodation> addBookToAccommodation(
            @PathVariable int accommodationId,
            @RequestBody Book newBook) {

        String username = "Unregistered User";
        // Aggiungi la nuova prenotazione alla casa selezionata dall'utente
        Accommodation updatedAccommodation = accommodationService.addBookToAccommodation(username, accommodationId, newBook);

        // Restituisci l'accommodation aggiornata
        return new ResponseEntity<>(updatedAccommodation, HttpStatus.OK);
    }
}

