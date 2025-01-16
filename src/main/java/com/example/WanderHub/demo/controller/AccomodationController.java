package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Accomodation;
import com.example.WanderHub.demo.service.AccomodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/accomodations")
public class AccomodationController {

    @Autowired
    private AccomodationService accomodationService;

    @PostMapping
    public Accomodation createAccomodation(@RequestBody Accomodation accomodation) {
        return accomodationService.createAccomodation(accomodation);
    }

    @GetMapping("/{id}")
    public Accomodation getAccomodation(@PathVariable int id) {
        return accomodationService.getAccomodationById(id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable int id) {
        boolean isDeleted = accomodationService.deleteAccommodationById(id);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK); // Success, no content returned
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Resource not found
        }
    }

}
