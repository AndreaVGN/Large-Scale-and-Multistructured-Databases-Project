package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.PendingBooksDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.PendingBook;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import com.example.WanderHub.demo.utility.DateFormatterUtil;
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import com.example.WanderHub.demo.utility.RedisUtility;
import com.example.WanderHub.demo.utility.Validator;
import jakarta.transaction.Transactional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static com.example.WanderHub.demo.utility.RedisUtility.evaluateTTL;

@Service
public class BookService {


    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RedisUtility redisUtility;

    @Autowired
    private RegisteredUserRepository registeredUserRepository;


    private static final long lockTTL = 600; // 10 minutes for complete the book process

    // Block temporally accommodation to avoid overbooking problem
    // (both for registered (return boolean) and unregistered users (return String))
    public Object lockHouse(ObjectId accommodationId, String start, String end, String username) {
        try {

            LocalDate today = LocalDate.now();
            LocalDate startDate = DateFormatterUtil.parseWithDashes(start);  // yyyy-MM-dd
            LocalDate endDate = DateFormatterUtil.parseWithDashes(end);  // yyyy-MM-dd

            if (startDate.isBefore(today) || endDate.isAfter(today.plusYears(1)) || startDate.isAfter(endDate)) {
                throw new RuntimeException("Not valid time period");
            }

            if (username != null) {
                accommodationRepository.findByAccommodationId(accommodationId)
                        .orElseThrow(() -> new RuntimeException("Accommodation not found"));
            }

            String startFormatted = DateFormatterUtil.formatWithoutDashes(startDate);  // yyyymmdd
            String endFormatted = DateFormatterUtil.formatWithoutDashes(endDate);  // yyyymmdd
            String lockKey = "lock:accId:" + accommodationId + ":start:" + startFormatted + ":end:" + endFormatted;

            Boolean successLock = redisUtility.lockBook(lockKey, lockTTL, startDate, endDate);

            if (successLock == null || !successLock) {
                return username == null ? null : false;
            }

            if (redisUtility.isOverlappingBooking(accommodationId, startFormatted, endFormatted)) {
                redisUtility.delete(lockKey);
                return username == null ? null : false;
            }

            String userIdentifier = (username == null) ? String.valueOf(System.currentTimeMillis()) : username;
                try {
                    redisUtility.setKey("booking" + lockKey + ":user", userIdentifier, lockTTL);
                } catch (Exception redisException) {
                    redisUtility.delete(lockKey);
                    throw new RuntimeException(redisException);
                }

            return username == null ? userIdentifier : true;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while locking the house: " + e.getMessage(), e);
        }
    }

    // Unlock a previously locked accommodation (both for registered and unregistered users)
    public boolean unlockHouse(ObjectId houseId, String start, String end, String userIdentifier) {
        try {
            // Utilizzo di DateFormatterUtil per formattare le date e ottenere il formato yyyymmdd
            LocalDate startDate = DateFormatterUtil.parseWithDashes(start); // yyyy-MM-dd -> LocalDate
            LocalDate endDate = DateFormatterUtil.parseWithDashes(end); // yyyy-MM-dd -> LocalDate

            // Ora formattiamo le date nel formato yyyymmdd
            String startFormatted = DateFormatterUtil.formatWithoutDashes(startDate); // yyyymmdd
            String endFormatted = DateFormatterUtil.formatWithoutDashes(endDate); // yyyymmdd

            String bookinglockKey = "bookinglock:accId:" + houseId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";
            String username = redisUtility.getValue(bookinglockKey);
            String lockKey = "lock:accId:" + houseId + ":start:" + startFormatted + ":end:" + endFormatted;

            // Verifica se la chiave è presente
            String storedValue = redisUtility.getValue(bookinglockKey);

            // Se l'utente è quello che ha bloccato la casa, procediamo con il rilascio
            if (username != null && username.equals(userIdentifier)) {
                // Proviamo a eliminare le chiavi dalla cache Redis
                boolean lockKeyDel = redisUtility.delete(bookinglockKey);
                boolean bookingLockKeyDel = redisUtility.delete(lockKey);

                if (!lockKeyDel || !bookingLockKeyDel) {
                    // Se qualcosa va storto, dobbiamo fare un rollback
                    if (lockKeyDel) {
                        redisUtility.setKey(bookinglockKey, storedValue, lockTTL); // Rimuoviamo rollback se il lock è stato eliminato correttamente
                    }
                    if (bookingLockKeyDel) {
                        redisUtility.setKey(lockKey, startFormatted + endFormatted, lockTTL); // Rimuoviamo rollback per la chiave di prenotazione
                    }
                    throw new RuntimeException("Failed to delete booking from Redis, rollback executed.");
                }
                return true; // Successo nel rilascio
            }

            return false; // Se l'utente non è quello che ha bloccato la casa, restituiamo false
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while unlocking the house: " + e.getMessage(), e);
        }
    }

    // Return all the future books (basic informations) of a accommodation done by a registered user
    public List<PendingBook> getPendingBookings(String username) {
        // Recupera l'utente tramite username
        RegisteredUser user = registeredUserRepository.findById(username).orElse(null);

        System.out.println(user);
        // Se l'utente non esiste, ritorna una lista vuota
        if (user == null) {
            return List.of();
        }

        // Filtra tutte le PendingBook future (startDate > oggi)
        LocalDate today = LocalDate.now();
        return user.getBooks().stream()
                .filter(pendingBook -> pendingBook.getStartDate().isAfter(today))
                .collect(Collectors.toList());
    }


    // Add a new book for both registered and unregistered users to an accommodation.
    // SCHEME:
    // 1 - Insert a new book in MongoDB
    // 2 - If 1) success and isLogged is true (book by a registered users) insert a new key in Redis (for store pending book)
    // 3 - If 1) not success abort the operation
    // 4 - If 1) success but 2) not success rollback 1)
    public Accommodation addBookToAccommodation(String username, ObjectId accommodationId, Book newBook, boolean isLogged) {
        try {
            LocalDate startDate = newBook.getStartDate();
            LocalDate endDate = newBook.getEndDate();

            // Utilizzo di DateFormatterUtil per evitare codice ripetuto
            String startFormatted = DateFormatterUtil.formatWithoutDashes(startDate);
            String endFormatted = DateFormatterUtil.formatWithoutDashes(endDate);

            int overlappingPeriodCount = accommodationRepository.checkAvailability(accommodationId, startDate, endDate);

            if (overlappingPeriodCount > 0) {
                throw new IllegalArgumentException("Accommodation " + accommodationId + " is not available for the selected period.");
            }

            Validator.validateBook(newBook);

            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            String lockKey = "bookinglock:accId:" + accommodationId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";


            if (accommodation.getHostUsername().equals(username)) {
                throw new RuntimeException("Host cannot book their own accommodation.");
            }

            String existingBooking = redisUtility.getValue(lockKey);

            if (!username.equals(existingBooking)) {
                throw new RuntimeException("You have to lock the accommodation before!");
            }

            newBook.setUsername(username);
            newBook.setBookDate(LocalDate.now());

            accommodation.getBooks().add(newBook);

            if (accommodation.getOccupiedDates() == null) {
                accommodation.setOccupiedDates(new ArrayList<>());
            }
            accommodation.getOccupiedDates().add(new OccupiedPeriod(startDate, endDate));

            Accommodation savedAccommodation = accommodationRepository.save(accommodation);
            System.out.println("MongoDB: Accommodation saved!");

            return savedAccommodation;

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }


    // Delete a book with startDate of the book at least 2 days forward than the current date
    // SCHEME:
    // 1 -- Delete the book from MongoDB
    // 2 -- If 1) success delete the correspondent pending book from Redis
    // 3 -- If 1) not success abort
    // 4 -- If 1) success but 2) not success rollback 1)
    @Transactional
    public boolean deleteBook(String username, ObjectId accommodationId, LocalDate startDate, LocalDate endDate) {
        // Trova l'utente in base allo username
        RegisteredUser user = registeredUserRepository.findById(username).orElse(null);
        if (user == null) {
            return false; // Se l'utente non esiste, ritorna false
        }

        // Trova la prenotazione da rimuovere dalla lista dei PendingBook
        PendingBook pendingBookToRemove = null;
        for (PendingBook pendingBook : user.getBooks()) {
            if (pendingBook.getAccommodationId().equals(accommodationId) &&
                    pendingBook.getStartDate().equals(startDate) &&
                    pendingBook.getEndDate().equals(endDate)) {
                pendingBookToRemove = pendingBook;
                break;
            }
        }

        if (pendingBookToRemove == null) {
            return false; // Se la prenotazione non è stata trovata, ritorna false
        }

        // Rimuovi la PendingBook dalla lista dell'utente
        user.getBooks().remove(pendingBookToRemove);
        registeredUserRepository.save(user); // Salva le modifiche nel RegisteredUser

        // Trova l'Accommodation
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId).orElse(null);
        if (accommodation == null) {
            return false; // Se l'Accommodation non esiste, ritorna false
        }

        // Rimuovi la Book dalla lista di books in Accommodation
        Book bookToRemove = null;
        for (Book book : accommodation.getBooks()) {
            if (book.getUsername().equals(username) &&
                    book.getStartDate().equals(startDate) &&
                    book.getEndDate().equals(endDate)) {
                bookToRemove = book;
                break;
            }
        }

        if (bookToRemove != null) {
            accommodation.getBooks().remove(bookToRemove);
        }

        // Rimuovi il periodo occupato dalle occupiedDates in Accommodation
        accommodation.getOccupiedDates().removeIf(period ->
                period.getStart().equals(startDate) && period.getEnd().equals(endDate));

        // Salva le modifiche in Accommodation
        accommodationRepository.save(accommodation);

        return true; // Se tutto è andato a buon fine, ritorna true
    }




}
