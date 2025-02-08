package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.BookRepository;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import com.example.WanderHub.demo.utility.RedisUtility;
import com.example.WanderHub.demo.utility.Validator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RedisUtility redisUtility;

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    private static final long lockTTL = 1200;


    // Creazione di una nuova sistemazione

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getBooksByCityAndPeriod(String city, String period) {
        return bookRepository.findByCityAndPeriod(city, period);
    }


    public Object lockHouse(ObjectId accommodationId, String start, String end, String username) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        if (startDate.isBefore(today) || endDate.isAfter(today.plusYears(1)) || startDate.isAfter(endDate)) {
            throw new RuntimeException("Periodo di tempo non valido.");
        }

        // Se è una prenotazione registrata, controlla che l'accommodation esista
        if (username != null) {
            accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));
        }

        String lockKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;

        // Tentiamo di acquisire il lock utilizzando SETNX
        Boolean successLock = redisUtility.lock(lockKey);
        if (successLock == null || !successLock) {
            return username == null ? null : false; // Restituisce null o false a seconda della modalità
        }

        // Se c'è una sovrapposizione, rilascia subito il lock e ritorna null/false
        if (redisUtility.isOverlappingBooking(accommodationId, start, end)) {
            redisUtility.delete(lockKey);
            return username == null ? null : false;
        }

        // Se è una prenotazione anonima, salva il timestamp, altrimenti salva lo username
        String valueToStore = (username == null) ? String.valueOf(System.currentTimeMillis()) : username;
        redisUtility.setKey(lockKey, valueToStore, lockTTL);

        return username == null ? valueToStore : true; // Restituisce il timestamp o true a seconda del caso
    }



    public boolean unlockHouse(ObjectId houseId, String start, String end, String userIdentifier) {
        String lockKey = "booking:accId:" + houseId + ":start:" + start + ":end:" + end;

        String storedValue = redisUtility.getValue(lockKey);

        if (storedValue != null && storedValue.equals(userIdentifier)) {
            redisUtility.delete(lockKey);
            return true;
        }

        return false;
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

    public Accommodation addBookToAccommodation(String username, ObjectId accommodationId, Book newBook) {
        try {
            LocalDate start = newBook.getStartDate();
            LocalDate end = newBook.getEndDate();

            int aux = accommodationRepository.checkAvailability(accommodationId, start, end);
            if (aux > 0) {
                throw new IllegalArgumentException("Accommodation " + accommodationId + " is not available for the selected period.");
            }

            Validator.validateBook(newBook);

            // Recupera l'accommodation dal database
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            // Controlla che l'utente non sia il proprietario dell'alloggio
            if (accommodation.getHostUsername().equals(username)) {
                String lockKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;
                redisUtility.delete(lockKey);
                throw new RuntimeException("Host cannot book their own accommodation.");
            }

            // Controlla se l'utente ha già una prenotazione nello stesso periodo
            String bookingKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;
            String existingBooking = redisUtility.getValue(bookingKey);

            System.out.println(existingBooking);
            if (!username.equals(existingBooking)) {
                throw new RuntimeException("You have to lock the accommodation before!");
            }

            // Imposta il nome utente sulla prenotazione
            newBook.setUsername(username);

            // **1. Aggiungi la prenotazione alla lista delle books dell'accommodation**
            accommodation.getBooks().add(newBook);

            // **2. Aggiorna il campo occupiedDates dell'accommodation con il nuovo periodo occupato**
            if (accommodation.getOccupiedDates() == null) {
                accommodation.setOccupiedDates(new ArrayList<>());
            }
            accommodation.getOccupiedDates().add(new OccupiedPeriod(start, end));

            // Salva l'accommodation aggiornata nel database
            return accommodationRepository.save(accommodation);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }



    public boolean deleteBook(String username, ObjectId accommodationId, LocalDate startDate, LocalDate endDate) {
        try {
            // Retrieve the accommodation by its ID
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            // Retrieve the customer user who is performing the cancellation
            RegisteredUser customer = registeredUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Find the booking to be deleted
            Book bookToDelete = accommodation.getBooks().stream()
                    .filter(book -> book.getStartDate().equals(startDate) &&
                            book.getEndDate().equals(endDate) &&
                            book.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Booking not found"));


            // Calculate the difference in days between the booking start date and today
            long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), bookToDelete.getStartDate());
            System.out.println(daysUntilStart);

            if (daysUntilStart >= 2) {
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
