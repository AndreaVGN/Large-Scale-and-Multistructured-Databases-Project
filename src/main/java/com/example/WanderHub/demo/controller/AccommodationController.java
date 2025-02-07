package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.*;
import com.example.WanderHub.demo.service.ArchivedBookService;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.BookService;

import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
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
    @Autowired
    private BookService bookService;
    @Autowired
    private ArchivedBookService archivedBookService;

    @PostMapping("/{username}")
    public ResponseEntity<?> createAccommodation(@PathVariable String username, @RequestBody Accommodation accommodation, HttpSession session) {
        // Controlla se l'utente nella sessione è lo stesso che è nel path
        String usernam = (String) session.getAttribute("user");

        if (usernam == null || !usernam.equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Logica per creare l'accommodation
        accommodationService.createAccommodation(accommodation);
        return ResponseEntity.ok("Accommodation created successfully");
    }

    @GetMapping("/{id}")
    public AccommodationDTO getAccommodationById(@PathVariable ObjectId id) {
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

    @GetMapping("/average-rating/{city}")
    public List<FacilityRatingDTO> getAverageRatingByFacility(@PathVariable String city) {
        return accommodationService.getAverageRatingByFacility(city);
    }

    @GetMapping("/{city}/viewAvgCostPerNight")
    public ResponseEntity<List<AverageCostDTO>> viewAvgCostPerNight(@PathVariable String city){
        return new ResponseEntity<>(accommodationService.viewAvgCostPerNight(city),HttpStatus.OK);
    }


}

