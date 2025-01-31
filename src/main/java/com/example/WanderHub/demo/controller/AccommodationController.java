package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
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
    public List<AccommodationDTO> viewOwnAccommodations(@PathVariable String hostUsername) {
        return accommodationService.findOwnAccommodations(hostUsername);
    }

    @GetMapping("/{hostUsername}/viewAccommodationBooks/{id}")
    public List<Accommodation> viewAccommodationBooks(@PathVariable String hostUsername, @PathVariable int id) {
        return accommodationService.viewAccommodationBooks(hostUsername,id);
    }
    @GetMapping("/{hostUsername}/viewAccommodationReviews/{id}")
    public ReviewDTO viewAccommodationReviews(@PathVariable String hostUsername, @PathVariable int id) {
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

