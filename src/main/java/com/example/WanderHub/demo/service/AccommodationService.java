package com.example.WanderHub.demo.service;
import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.BookDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    private final RegisteredUserRepository registeredUserRepository;

    @Autowired
    public AccommodationService(AccommodationRepository accommodationRepository, RegisteredUserRepository registeredUserRepository) {
        this.registeredUserRepository = registeredUserRepository;
    }

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

    /*
    // Ricerca sistemazioni disponibili
    public List<Accommodation> findAvailableAccommodations(String city, int minGuests, String startDate, String endDate) {
        return accommodationRepository.findAvailableAccommodations(city, minGuests, startDate, endDate);
    }*/

    public List<AccommodationDTO> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate) {
        // Esegui la query che restituisce le Accommodation
        List<Accommodation> accommodations = accommodationRepository.findAvailableAccommodations(place, minGuests, startDate, endDate);

        // Mappa le Accommodation in DTO
        return accommodations.stream()
                .map(accommodation -> new AccommodationDTO(
                        accommodation.getDescription(),
                        accommodation.getType(),
                        accommodation.getCity(),
                        accommodation.getHostUsername(),
                        accommodation.getCostPerNight(),
                        accommodation.getAverageRate(),
                        accommodation.getPhotos() != null && accommodation.getPhotos().length > 0 ? accommodation.getPhotos()[0] : null
                ))
                .collect(Collectors.toList());
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


    public List<Review> getReviewsByUsername(String username) {
        // Ottieni ReviewDTO dal repository
        List<ReviewDTO> reviewsDTOList = accommodationRepository.findReviewsByUsername(username);

        // Estrai le recensioni da ogni ReviewDTO e restituisci una lista di Review
        return reviewsDTOList.stream()
                .flatMap(dto -> dto.getReviews().stream())
                .collect(Collectors.toList());
    }
    public List<Book> getPendingBookings(String username) {
        // Ottieni ReviewDTO dal repository
        List<BookDTO> booksDTOList = accommodationRepository.findPendingBookingsByUsername(username);

        // Estrai le recensioni da ogni ReviewDTO e restituisci una lista di Review
        return booksDTOList.stream()
                .flatMap(dto -> dto.getBooks().stream())
                .collect(Collectors.toList());
    }
    public List<Accommodation> findOwnAccommodations(String hostUsername){
        return accommodationRepository.findOwnAccommodations(hostUsername);
    }
    public List<Accommodation> viewAccommodationBooks(String hostUsername, int id){
        return accommodationRepository.viewAccommodationBooks(hostUsername,id);
    }
    public List<Accommodation> viewAccommodationReviews(String hostUsername, int id){
        return accommodationRepository.viewAccommodationReviews(hostUsername,id);
    }

    // Metodo per aggiungere una prenotazione alla casa scelta dal cliente
    public Accommodation addBookToAccommodation(String username, int accommodationId, Book newBook) {
        // Recupera l'accommodation tramite il suo ID
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new RuntimeException("Accommodation not found"));

        // Recupera l'utente cliente che sta facendo la prenotazione

        RegisteredUser customer = registeredUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Aggiungi il nome dell'utente (cliente) alla prenotazione
        newBook.setUsername(username);

        // Aggiungi la nuova prenotazione alla lista delle prenotazioni della casa
        accommodation.getBooks().add(newBook);

        // Salva l'accommodation aggiornata nel database
        return accommodationRepository.save(accommodation);
    }

    public RegisteredUser addAccommodationToRegisteredUser(String username, Accommodation accommodation) {
        // Trova l'utente per username
        RegisteredUser registeredUser = registeredUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Imposta l'host dell'accommodation come l'utente
        accommodation.setHostUsername(registeredUser.getUsername());

        // Salva la nuova accommodation nella collection Accommodation
        accommodationRepository.save(accommodation);

        // Ritorna l'utente aggiornato (o altre informazioni se necessario)
        return registeredUser;
    }

    public List<Accommodation> findAccommodationsByHost(String hostUsername) {
        // Esegui la query per trovare tutte le case del proprietario
        return accommodationRepository.findByHostUsername(hostUsername);
    }

    public boolean deleteBook(String username, int accommodationId, int bookId) {
        // Trova l'accommodation per id e host
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(accommodationId);

        if (accommodationOptional.isPresent()) {
            Accommodation accommodation = accommodationOptional.get();

            // Trova la prenotazione da eliminare
            Optional<Book> bookOptional = accommodation.getBooks().stream()
                    .filter(book -> book.getBookId() == bookId && book.getUsername().equals(username))
                    .findFirst();

            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();

                // Calcola la differenza in giorni tra la data di inizio della prenotazione e la data odierna
                long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), book.getStartDate());

                if (daysUntilStart > 2) {
                    // Se sono passati più di 2 giorni, la prenotazione non può essere cancellata
                    return false;
                }

                // Rimuovi la prenotazione dalla lista e aggiorna la sistemazione
                accommodation.getBooks().remove(book);
                accommodationRepository.save(accommodation);
                return true;
            }
        }

        return false;
    }

}

