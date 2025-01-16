package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Accommodation;
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

    @PostMapping
    public Accommodation createAccommodation(@RequestBody Accommodation accommodation) {
        return accommodationService.createAccommodation(accommodation);
    }

    @GetMapping("/{id}")
    public Accommodation getAccommodation(@PathVariable int id) {
        return accommodationService.getAccommodationById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable int id) {
        boolean isDeleted = accommodationService.deleteAccommodationById(id);

        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findAccommodations")
    public List<Accommodation> findAccommodations(
            @RequestParam("city") String place,
            @RequestParam("guestSize") int minGuests,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate)
    {

        return accommodationService.findAvailableAccommodations(place, minGuests, startDate, endDate);
    }

}
