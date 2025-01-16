package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    // Creazione di una nuova sistemazione
    public Accommodation createAccommodation(Accommodation accommodation) {
        return accommodationRepository.save(accommodation);
    }

    public Accommodation getAccommodationById(int accommodationId) {
        return accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));
    }

    public boolean deleteAccommodationById(int accommodationId) {
        if(accommodationRepository.existsByAccommodationId(accommodationId)) {
            accommodationRepository.deleteByAccommodationId(accommodationId);
            return true;
        }
        return false;
    }

    public List<Accommodation> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate) {
        return accommodationRepository.findAvailableAccommodations(place, minGuests, startDate, endDate);
    }

    // Altri metodi per gestire le sistemazioni

}
