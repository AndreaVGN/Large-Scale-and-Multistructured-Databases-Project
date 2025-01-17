package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    // Creazione di una nuova sistemazione
    public Accommodation createAccommodation(Accommodation accommodation) {
        return accommodationRepository.save(accommodation);
    }

    // Recupero sistemazione per id
    public Accommodation getAccommodationById(int accommodationId) {
        return accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));
    }

    // Eliminazione di una sistemazione
    public boolean deleteAccommodationById(int accommodationId) {
        if (accommodationRepository.existsByAccommodationId(accommodationId)) {
            accommodationRepository.deleteByAccommodationId(accommodationId);
            return true;
        }
        return false;
    }

    // Ricerca sistemazioni disponibili
    public List<Accommodation> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate) {
        return accommodationRepository.findAvailableAccommodations(place, minGuests, startDate, endDate);
    }

    // Aggiunta di un book a una sistemazione
    public Accommodation addBookToAccommodation(int accommodationId, Book newBook) {
        // Trova l'accommodation esistente per id
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));

        // Aggiungi la nuova book all'array books
        List<Book> booksList = new ArrayList<>(Arrays.asList(accommodation.getBooks()));  // Copia i libri esistenti
        booksList.add(newBook);  // Aggiungi la nuova prenotazione

        // Riconverti la lista in un array e imposta il campo books
        accommodation.setBooks(booksList.toArray(new Book[0]));  // Imposta il nuovo array di books

        // Salva l'accommodation aggiornata nel database
        return accommodationRepository.save(accommodation);
    }
}

