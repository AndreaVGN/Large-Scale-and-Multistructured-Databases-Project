package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Accomodation;
import com.example.WanderHub.demo.repository.AccomodationRepository;
import com.example.WanderHub.demo.service.AccomodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> deleteAccomodation(@PathVariable int id) {
        boolean isDeleted = accomodationService.deleteAccomodationById(id);

        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
