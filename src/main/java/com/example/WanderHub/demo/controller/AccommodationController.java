package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.service.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accommodations")
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    @PostMapping
    public Accommodation createAccomodation(@RequestBody Accommodation accomodation) {
        return accommodationService.createAccomodation(accomodation);
    }

    @GetMapping("/{id}")
    public Accommodation getAccomodation(@PathVariable int id) {
        return accommodationService.getAccomodationById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccomodation(@PathVariable int id) {
        boolean isDeleted = accommodationService.deleteAccomodationById(id);

        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
