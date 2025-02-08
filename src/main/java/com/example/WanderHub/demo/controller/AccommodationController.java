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















    @GetMapping("/average-rating/{city}")
    public List<FacilityRatingDTO> getAverageRatingByFacility(@PathVariable String city) {

        return accommodationService.getAverageRatingByFacility(city);
    }

    @GetMapping("/{city}/viewAvgCostPerNight")
    public ResponseEntity<List<AverageCostDTO>> viewAvgCostPerNight(@PathVariable String city){

        return new ResponseEntity<>(accommodationService.viewAvgCostPerNight(city),HttpStatus.OK);
    }


}

