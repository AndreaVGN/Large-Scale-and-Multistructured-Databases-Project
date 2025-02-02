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
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import com.example.WanderHub.demo.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    public Accommodation createAccommodation(Accommodation accommodation) {
        try {

            Validator.validateAccommodation(accommodation);

            // If all checks pass, save the accommodation in the database
            return accommodationRepository.save(accommodation);

        } catch (IllegalArgumentException e) {
            // Re-throw the exception to notify the controller
            throw e;
        } catch (DataAccessException e) {
            // Handle database-related errors (connection, query, etc.)
            throw new RuntimeException("Error while saving accommodation to the database: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle other generic errors
            throw new RuntimeException("Error while creating accommodation: " + e.getMessage(), e);
        }
    }

    public AccommodationDTO getAccommodationById(int accommodationId) {
        try {
            // Trova la sistemazione esistente
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));

            // Use the factory method to create a DTO with complete data (excluding books)
            return AccommodationDTO.fromFullDetails(accommodation);
        } catch (DataAccessException e) {
            // Handles database-related errors (connection, query, etc.)
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handles other generic errors
            throw new RuntimeException("Error while retrieving accommodation: " + e.getMessage(), e);
        }
    }

    // Eliminazione di una sistemazione
    public boolean deleteAccommodationById(int accommodationId) {
        if (accommodationRepository.existsByAccommodationId(accommodationId)) {
            accommodationRepository.deleteByAccommodationId(accommodationId);
            return true;
        }
        return false;
    }


    public List<AccommodationDTO> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate) {
        try {
            // Validazione dei parametri
            if (minGuests <= 0) {
                throw new IllegalArgumentException("guestSize must be greater than zero.");
            }
            if(place==null || place.trim().isEmpty()){
                throw new IllegalArgumentException("place cannot be null.");
            }
            if (startDate == null || startDate.trim().isEmpty()) {
                throw new IllegalArgumentException("startDate cannot be empty.");
            }
            if (endDate == null || endDate.trim().isEmpty()) {
                throw new IllegalArgumentException("endDate cannot be empty.");
            }

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (end.isBefore(start)) {
                throw new IllegalArgumentException("endDate cannot be before startDate.");
            }

            // Esegui la query che restituisce le Accommodation
            List<Accommodation> accommodations = accommodationRepository.findAvailableAccommodations(place, minGuests, startDate, endDate);

            // Mappa le Accommodation in DTO utilizzando il factory method
            return accommodations.stream()
                    .map(AccommodationDTO::fromLimitedInfo)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD format", e);
        }catch (DataAccessException e) {
            // Handle database-related errors (connection, query, etc.)
            throw new RuntimeException("Error while getting available accommodations from database: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while searching for available accommodations.", e);
        }
    }

    public List<AccommodationDTO> getReviewsByUsername(String username) {
        try {
            List<Accommodation> reviews = accommodationRepository.findReviewsByUsername(username);
            return reviews.stream()
                    .map(AccommodationDTO::withReviews)
                    .collect(Collectors.toList());
        }
        catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving reviews from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving reviews from the database: ", e);
        }


    }
    
    public List<AccommodationDTO> getPendingBookings(String username) {
        try {
            // Ottieni BookDTO dal repository
            //return accommodationRepository.findPendingBookingsByUsername(username);
            List<Accommodation> books = accommodationRepository.findPendingBookingsByUsername(username);
            return books.stream()
                    .map(AccommodationDTO::fromSomeInfo)
                    .collect(Collectors.toList());
        }
        catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving pending bookings from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving pending bookings from the database: ", e);
        }
    }


    public List<AccommodationDTO> findOwnAccommodations(String hostUsername) {
        try {
            // Esegui la query per ottenere le accommodations del proprietario
            List<Accommodation> accommodations = accommodationRepository.findOwnAccommodations(hostUsername);

            // Mappa le accommodations in DTO utilizzando il factory method
            return accommodations.stream()
                    .map(AccommodationDTO::fromBasicInfo)
                    .collect(Collectors.toList());
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }


    public List<Book> viewAccommodationBooks(String hostUsername, int id){
        try {
            List<BookDTO> booksDTOList = accommodationRepository.viewAccommodationBooks(hostUsername, id);

            return booksDTOList.stream()
                    .flatMap(dto -> dto.getBooks().stream())
                    .collect(Collectors.toList());
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }


    public List<ReviewDTO> viewAccommodationReviews(String hostUsername, int id) {
        try {
            // Recupera la lista di Accommodation corrispondente alla query
            return accommodationRepository.viewAccommodationReviews(hostUsername, id);
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }


    // Metodo per aggiungere una prenotazione alla casa scelta dal cliente
    public Accommodation addBookToAccommodation(String username, int accommodationId, Book newBook) {
        try {
            Validator.validateBook(newBook);


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
        } catch (DataAccessException e) {
            // Handle database errors (connection, query, etc.)
            throw new RuntimeException("Error occurred while saving the booking to the database: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // Handle validation errors for fields
            throw new IllegalArgumentException("Validation error: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle any other general errors
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }

    public boolean deleteBook(String username, int accommodationId, int bookId) {
        try {
            // Retrieve the accommodation by its ID
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            // Retrieve the customer user who is performing the cancellation
            RegisteredUser customer = registeredUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Find the booking to be deleted
            Book bookToDelete = accommodation.getBooks().stream()
                    .filter(book -> book.getBookId() == bookId && book.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Calculate the difference in days between the booking start date and today
            long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), bookToDelete.getStartDate());

            if (daysUntilStart <= 2) {
                // Remove the booking from the list and update the accommodation
                accommodation.getBooks().remove(bookToDelete);
                accommodationRepository.save(accommodation);
                return true;
            } else {
                throw new IllegalArgumentException("Booking cannot be canceled as it exceeds the allowed cancellation period.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting the booking: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Validation error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while deleting the booking: " + e.getMessage(), e);
        }
    }


}

