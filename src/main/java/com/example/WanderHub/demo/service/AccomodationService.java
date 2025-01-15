package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accomodation;
import com.example.WanderHub.demo.repository.AccomodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccomodationService {

    @Autowired
    private AccomodationRepository accomodationRepository;

    // Creazione di una nuova sistemazione
    public Accomodation createAccomodation(Accomodation accomodation) {
        return accomodationRepository.save(accomodation);
    }

    // Altri metodi per gestire le sistemazioni

}
