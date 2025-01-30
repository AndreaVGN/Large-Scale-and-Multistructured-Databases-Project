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
   @GetMapping("/{id}")
    public Accommodation getAccommodationById(@PathVariable int id) {
        return accommodationService.getAccommodationById(id);
   }
    @GetMapping("/findAccommodations")
    public List<Accommodation> findAccommodations(
            @RequestParam("city") String place,
            @RequestParam("guestSize") int minGuests,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        return accommodationService.findAvailableAccommodations(place, minGuests, startDate, endDate);
    }
    @GetMapping("/{hostUsername}/viewOwnAccommodations")
    public List<Accommodation> viewOwnAccommodations(@PathVariable String hostUsername) {
        return accommodationService.findOwnAccommodations(hostUsername);
    }
    @GetMapping("/{hostUsername}/viewAccommodationDetails/{id}")
    public List<Accommodation> viewAccommodationDetails(@PathVariable String hostUsername, @PathVariable int id) {
        return accommodationService.viewAccommodationDetails(hostUsername,id);
    }
}

