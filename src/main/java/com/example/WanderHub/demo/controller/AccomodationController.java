package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Accomodation;
import com.example.WanderHub.demo.service.AccomodationService;
import org.springframework.beans.factory.annotation.Autowired;
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

}
