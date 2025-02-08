package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.*;
import com.example.WanderHub.demo.service.AccommodationService;

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
@RequestMapping("/accommodations")
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    @PostMapping("/{username}")
    public ResponseEntity<?> createAccommodation(@PathVariable String username, @RequestBody Accommodation accommodation, HttpSession session) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

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
            @RequestParam("endDate") String endDate,
            @RequestParam("pageNumber") int pageNumber

    ) {

        return accommodationService.findAvailableAccommodations(place, minGuests, startDate, endDate, pageNumber);
    }

    @GetMapping("/{hostUsername}/viewOwnAccommodations")
    public ResponseEntity<?> viewOwnAccommodations(@PathVariable String hostUsername, HttpSession session) {

        if (!SessionUtilility.isLogged(session, hostUsername)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        List<AccommodationDTO> accommodations = accommodationService.findOwnAccommodations(hostUsername);

        if (accommodations.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No accommodation found for this host");
        }

        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/{username}/average-rating/{city}")
    public ResponseEntity<?> getAverageRatingByFacility(@PathVariable String city, @PathVariable String username, HttpSession session) {
        // Verifica se l'utente è loggato e ha il ruolo di admin
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            // Restituisce un errore 403 (Forbidden) se l'utente non è autorizzato
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Recupera la lista delle valutazioni medie per le strutture
        List<FacilityRatingDTO> ratings = accommodationService.getAverageRatingByFacility(city);

        // Restituisce la lista con stato 200 (OK)
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{username}/{city}/viewAvgCostPerNight")
    public ResponseEntity<?> viewAvgCostPerNight(@PathVariable String city, @PathVariable String username, HttpSession session) {
        // Verifica se l'utente è loggato e ha il ruolo di admin
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            // Restituisce un errore 403 (Forbidden) se l'utente non è autorizzato
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Recupera la lista dei costi medi per notte
        List<AverageCostDTO> avgCostList = accommodationService.viewAvgCostPerNight(city);

        // Restituisce la lista con stato 200 (OK)
        return ResponseEntity.ok(avgCostList);
    }




}

