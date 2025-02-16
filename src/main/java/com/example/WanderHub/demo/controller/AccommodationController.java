package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.*;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.utility.SessionUtilility;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    @PostMapping("/{username}")
    public ResponseEntity<?> createAccommodation(@PathVariable String username, @RequestBody Accommodation accommodation, HttpSession session) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        accommodationService.createAccommodation(accommodation, username);

        return ResponseEntity.ok("Accommodation created successfully");
    }

    @GetMapping("/{id}")
    public AccommodationDTO getAccommodationById(@PathVariable String id) {

        return accommodationService.getAccommodationById(id);
   }
    @GetMapping("/{username}/{id}")
    public ResponseEntity<?> viewAccommodationById(@PathVariable String id, @PathVariable String username, HttpSession session) {
        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }
        Accommodation accommodation = accommodationService.viewAccommodationById(id);
        return ResponseEntity.ok(accommodation);
    }

    @GetMapping("/availableAccommodations")
    public List<AccommodationDTO> findAccommodations(
            @RequestParam("city") String place,
            @RequestParam("guestSize") int minGuests,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("pageNumber") int pageNumber

    ) {

        return accommodationService.findAvailableAccommodations(place, minGuests, startDate, endDate, pageNumber);
    }

    @GetMapping("/{hostUsername}/myAccommodations")
    public ResponseEntity<?> viewOwnAccommodations(@PathVariable String hostUsername, HttpSession session) {

        if (!SessionUtilility.isLogged(session, hostUsername)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        List<Accommodation> accommodations = accommodationService.findOwnAccommodations(hostUsername);

        if (accommodations.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No accommodation found for this host");
        }

        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/{username}/{city}/average-rating")
    public ResponseEntity<?> getAverageRatingByFacility(@PathVariable String city, @PathVariable String username, HttpSession session) {

        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        List<FacilityRatingDTO> ratings = accommodationService.getAverageRatingByFacility(city);

        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{username}/{city}/AvgCostPerNight")
    public ResponseEntity<?> viewAvgCostPerNight(@PathVariable String city, @PathVariable String username, HttpSession session) {

        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        List<AverageCostDTO> avgCostList = accommodationService.viewAvgCostPerNight(city);

        return ResponseEntity.ok(avgCostList);
    }




}

