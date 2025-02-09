package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.PendingBooksDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.BookRepository;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import com.example.WanderHub.demo.utility.RedisUtility;
import com.example.WanderHub.demo.utility.Validator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.WanderHub.demo.utility.RedisUtility.evaluateTTL;

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

    public Object lockHouse(ObjectId accommodationId, String start, String end, String username) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while locking the house: " + e.getMessage(), e);
        }
    }

    public boolean unlockHouse(ObjectId houseId, String start, String end, String userIdentifier) {
        try {
            String lockKey = "booking:accId:" + houseId + ":start:" + start + ":end:" + end;

            String storedValue = redisUtility.getValue(lockKey);

            if (storedValue != null && storedValue.equals(userIdentifier)) {
                redisUtility.delete(lockKey);
                return true;
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while unlocking the house: " + e.getMessage(), e);
        }
    }

    public AccommodationDTO viewPendingBooking(String username, String accommodationId, String startDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            Accommodation book = accommodationRepository.findPendingBookingByUsername(username, start, accommodationId);
            return AccommodationDTO.idDescriptionBooks(book);

        } catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving pending bookings from the database: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving pending bookings from the database: ", e);
        }
    }

    public List<PendingBooksDTO> getPendingBookings(String username) {
        Set<String> existingKeys = redisUtility.getKeys("username:"+username+":accId:*");
        List<PendingBooksDTO> pendingBookings = new ArrayList<>();
        if (existingKeys != null) {
            for (String key : existingKeys) {

                String[] parts = null;
                for(int i =0;i<6;i++) {
                     parts = key.split(":");
                }

                String user = parts[1];
                String accommodationId = parts[3];
                LocalDate startDate = LocalDate.parse(parts[5]);
                // Creiamo un nuovo DTO e lo aggiungiamo alla lista
                pendingBookings.add(new PendingBooksDTO(user, accommodationId, startDate));

            }
        }

        return pendingBookings;
    }


   public Accommodation addBookToAccommodation(String username, ObjectId accommodationId, Book newBook, boolean isLogged) {
        try {
            LocalDate start = newBook.getStartDate();
            LocalDate end = newBook.getEndDate();

            // Verifica la disponibilità dell'alloggio
            int aux = accommodationRepository.checkAvailability(accommodationId, start, end);
            System.out.println(aux);
            if (aux > 0) {
                throw new IllegalArgumentException("Accommodation " + accommodationId + " is not available for the selected period.");
            }

            // Validazione della prenotazione
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

            if (!username.equals(existingBooking)) {
                throw new RuntimeException("You have to lock the accommodation before!");
            }

            // Imposta il nome utente sulla prenotazione
            newBook.setUsername(username);

            // Aggiungi la prenotazione alla lista delle books dell'accommodation
            accommodation.getBooks().add(newBook);

            // Aggiorna il campo occupiedDates dell'accommodation con il nuovo periodo occupato
            if (accommodation.getOccupiedDates() == null) {
                accommodation.setOccupiedDates(new ArrayList<>());
            }
            accommodation.getOccupiedDates().add(new OccupiedPeriod(start, end));

            // Salva l'accommodation nel database MongoDB
            Accommodation savedAccommodation = accommodationRepository.save(accommodation);
            System.out.println("MongoDB: Accommodation saved!");

            // Se la scrittura su MongoDB è andata a buon fine, scrivi su Redis
            if (isLogged) {
                DateTimeFormatter formatterStart = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDateStart = start.format(formatterStart);
                String key = "username:" + username + ":accId:" + accommodationId + ":startDate:" + formattedDateStart;
                DateTimeFormatter formatterEnd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDateEnd = start.format(formatterEnd);

                try {
                    redisUtility.setKey(key, formattedDateEnd, evaluateTTL(formattedDateStart));
                } catch (Exception redisException) {
                    // **Rollback su MongoDB se la scrittura su Redis fallisce
                    accommodation.getBooks().remove(newBook);
                    accommodation.getOccupiedDates().removeIf(period -> period.getStart().equals(start) && period.getEnd().equals(end));
                    accommodationRepository.save(accommodation); // Ripristina lo stato di MongoDB
                    throw new RuntimeException("Failed to write booking to Redis, rollback MongoDB changes: " + redisException.getMessage(), redisException);
                }
            }

            return savedAccommodation;

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }

    public boolean deleteBook(String username, ObjectId accommodationId, LocalDate startDate, LocalDate endDate) {
        try {
            // Recupera l'accommodation e il booking da eliminare
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            Book bookToDelete = accommodation.getBooks().stream()
                    .filter(book -> book.getStartDate().equals(startDate) &&
                            book.getEndDate().equals(endDate) &&
                            book.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Controllo sulla cancellazione
            long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), bookToDelete.getStartDate());
            if (daysUntilStart < 2) {
                throw new IllegalArgumentException("Booking cannot be canceled as it exceeds the allowed cancellation period.");
            }

            //Backup del booking in caso di rollback
            Book backupBook = new Book(bookToDelete);

            //Rimuovi il periodo occupato da `occupiedDates`
            accommodation.getOccupiedDates().removeIf(period -> period.getStart().equals(startDate) &&
                    period.getEnd().equals(endDate));

            //Elimina il booking da MongoDB**
            accommodation.getBooks().remove(bookToDelete);
            accommodationRepository.save(accommodation); // Operazione su MongoDB

            //Elimina da Redis
            String key = "username:" + username + ":accId:" + accommodationId + ":startDate:" + startDate;
            if (!redisUtility.delete(key)) {  // Supponiamo che `delete()` restituisca `false` se fallisce
                // Se Redis fallisce, ripristina il booking su MongoDB
                accommodation.getBooks().add(backupBook);
                // Ripristina il periodo occupato
                accommodation.getOccupiedDates().addAll(bookToDelete.getOccupiedDates());
                accommodationRepository.save(accommodation);
                throw new RuntimeException("Failed to delete booking from Redis, rollback executed.");
            }

            return true; // Eliminazione riuscita

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting the booking: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Validation error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while deleting the booking: " + e.getMessage(), e);
        }
    }


}
