package com.example.WanderHub.demo.service;
import com.example.WanderHub.demo.model.Review;
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
    public List<Accommodation> getAccommodationsByCity(String city) {
        return accommodationRepository.findAccommodationsByCity(city);
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
    public List<Accommodation> findAvailableAccommodations(String city, int minGuests, String startDate, String endDate) {
        return accommodationRepository.findAvailableAccommodations(city, minGuests, startDate, endDate);
    }

    public Accommodation addBookToAccommodation(int accommodationId, Book newBook) {
        // Trova la sistemazione esistente
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));

        // Aggiungi la nuova prenotazione (Book) all'array di books
        List<Book> booksList = accommodation.getBooks();

        booksList.add(newBook);  // Aggiungi il nuovo oggetto Book

        // Salva la sistemazione aggiornata con la nuova prenotazione
        accommodation.setBooks(booksList);

        return accommodationRepository.save(accommodation);  // Salva l'accommodation aggiornata
    }

    public Accommodation addReviewToAccommodation(int accommodationId, Review newReview) {
        // Trova la sistemazione esistente
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));

        // Recupera la lista delle recensioni e aggiungi la nuova recensione
        List<Review> reviewsList = accommodation.getReviews();

        if (reviewsList == null) {
            reviewsList = new ArrayList<>();  // Crea una nuova lista se null
        }

        reviewsList.add(newReview);  // Aggiungi la nuova recensione

        // Imposta la lista aggiornata di recensioni
        accommodation.setReviews(reviewsList);

        // Salva l'accommodation aggiornata con la nuova recensione
        return accommodationRepository.save(accommodation);
    }

    public List<Review> getReviewsByAccommodationId(String username, int accommodationId) {
        // Trova l'accommodation per ID
        Accommodation accommodation = accommodationRepository.findReviewsByAccommodationId(accommodationId);
        System.out.println("Reviews: " + accommodation);
        // Se l'accommodation esiste, restituisci le recensioni
        List<Review> reviews = accommodation.getReviews();
        System.out.println("Reviews: " + reviews); // Stampa le recensioni
        return reviews;
    }

    public List<Accommodation> findAccommodationsByUsername(String username) {
        return accommodationRepository.findByHostUsername(username);
    }
    public List<Review> getReviewsByUsername(String username){
        return accommodationRepository.findReviewsByUsername(username);
    }
    public List<Book> getPendingBookings(String username) {
        return accommodationRepository.findPendingBookingsByUsername(username);
    }
}

