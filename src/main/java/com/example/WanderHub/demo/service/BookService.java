package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.PendingBooksDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.utility.DateFormatterUtil;
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import com.example.WanderHub.demo.utility.RedisUtility;
import com.example.WanderHub.demo.utility.Validator;
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


import static com.example.WanderHub.demo.utility.RedisUtility.evaluateTTL;

@Service
public class BookService {


    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RedisUtility redisUtility;


    private static final long lockTTL = 600; // 10 minutes for complete the book process

    // Block temporally accommodation to avoid overbooking problem
    // (both for registered (return boolean) and unregistered users (return String))
    public Object lockHouse(ObjectId accommodationId, String start, String end, String username) {
        try {
            // Utilizzo di DateFormatterUtil per il parsing delle date
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

            // Utilizzo di DateFormatterUtil per formattare la data senza trattini
            String startFormatted = DateFormatterUtil.formatWithoutDashes(startDate);  // yyyymmdd
            String endFormatted = DateFormatterUtil.formatWithoutDashes(endDate);  // yyyymmdd
            String lockKey = "lock:accId:" + accommodationId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";

            Boolean successLock = redisUtility.lock(lockKey, lockTTL);

            if (successLock == null || !successLock) {
                return username == null ? null : false;
            }

            if (redisUtility.isOverlappingBooking(accommodationId, startFormatted, endFormatted)) {
                redisUtility.delete(lockKey);
                return username == null ? null : false;
            }

            String valueToStore = (username == null) ? String.valueOf(System.currentTimeMillis()) : username;
            try {
                redisUtility.setKey(lockKey, valueToStore, lockTTL);
                redisUtility.setKey("booking" + lockKey + ":" + valueToStore, startFormatted + endFormatted, lockTTL);
            }
            catch (Exception redisException){
                redisUtility.delete(lockKey);
                redisUtility.delete("booking" + lockKey + ":" + valueToStore);
            }

            return username == null ? valueToStore : true;
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

            String lockKey = "lock:accId:" + houseId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";
            String username = redisUtility.getValue(lockKey);
            String bookingLockKey = "booking" + lockKey + ":" + username;

            // Verifica se la chiave è presente
            String storedValue = redisUtility.getValue(lockKey);

            // Se l'utente è quello che ha bloccato la casa, procediamo con il rilascio
            if (username != null && username.equals(userIdentifier)) {
                // Proviamo a eliminare le chiavi dalla cache Redis
                boolean lockKeyDel = redisUtility.delete(lockKey);
                boolean bookingLockKeyDel = redisUtility.delete(bookingLockKey);

                if (!lockKeyDel || !bookingLockKeyDel) {
                    // Se qualcosa va storto, dobbiamo fare un rollback
                    if (lockKeyDel) {
                        redisUtility.setKey(lockKey, storedValue, lockTTL); // Rimuoviamo rollback se il lock è stato eliminato correttamente
                    }
                    if (bookingLockKeyDel) {
                        redisUtility.setKey(bookingLockKey, startFormatted + endFormatted, lockTTL); // Rimuoviamo rollback per la chiave di prenotazione
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



    // Return a specific detailed future book of a accommodation done by a registered user
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

    // Return all the future books (basic informations) of a accommodation done by a registered user

    public List<PendingBooksDTO> getPendingBookings(String username) {
        List<PendingBooksDTO> pendingBookings = new ArrayList<>();

        try {
            Set<String> existingKeys = redisUtility.getKeys("username:" + username + ":accId:*");

            if (existingKeys != null) {
                for (String key : existingKeys) {
                    try {
                        String accommodationId = redisUtility.getValue(key);

                        if (accommodationId == null) {
                            continue;
                        }

                        Set<String> bookingKeys = redisUtility.getKeys("pendingbook:username:" + username + ":accId:" + accommodationId + ":*");

                        if (bookingKeys != null) {
                            for (String bookingKey : bookingKeys) {
                                try {
                                    String dateRange = redisUtility.getValue(bookingKey);

                                    if (dateRange == null || dateRange.length() < 16) {
                                        continue;
                                    }

                                    // Utilizzo di DateFormatterUtil per ottenere le date di inizio e fine
                                    String startDateStr = dateRange.substring(0, 8);
                                    String endDateStr = dateRange.substring(8, 16);

                                    // Parsing delle date usando DateFormatterUtil
                                    LocalDate startDate = DateFormatterUtil.parseWithoutDashes(startDateStr);
                                    LocalDate endDate = DateFormatterUtil.parseWithoutDashes(endDateStr);

                                    // Aggiungi la prenotazione alla lista
                                    pendingBookings.add(new PendingBooksDTO(username, accommodationId, startDate, endDate));
                                } catch (Exception e) {
                                    System.err.println("Error reading booking data for key " + bookingKey + ": " + e.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error retrieving accommodationId for key " + key + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("General error while retrieving pending bookings for user " + username + ": " + e.getMessage());
        }

        return pendingBookings;
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

            String lockKey = "lock:accId:" + accommodationId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";
            if (accommodation.getHostUsername().equals(username)) {
                redisUtility.delete(lockKey);
                String userIdentifier = redisUtility.getValue(lockKey);
                String bookingLockKey = "booking" + lockKey + ":" + userIdentifier;
                redisUtility.delete(bookingLockKey);
                throw new RuntimeException("Host cannot book their own accommodation.");
            }

            String existingBooking = redisUtility.getValue(lockKey);

            if (!username.equals(existingBooking)) {
                throw new RuntimeException("You have to lock the accommodation before!");
            }

            newBook.setUsername(username);

            accommodation.getBooks().add(newBook);

            if (accommodation.getOccupiedDates() == null) {
                accommodation.setOccupiedDates(new ArrayList<>());
            }
            accommodation.getOccupiedDates().add(new OccupiedPeriod(startDate, endDate));

            Accommodation savedAccommodation = accommodationRepository.save(accommodation);
            System.out.println("MongoDB: Accommodation saved!");

            if (isLogged) {
                String formattedDateStart = DateFormatterUtil.formatWithDashes(startDate); // yyyy-MM-dd
                String dateStartWithoutDashes = DateFormatterUtil.formatWithoutDashes(startDate); // yyyymmdd
                String dateEndWithoutDashes = DateFormatterUtil.formatWithoutDashes(endDate); // yyyymmdd

                String keyAcc = "username:" + username + ":accId:" + accommodationId + ":period:" + dateStartWithoutDashes + dateEndWithoutDashes;
                String keyPeriod = "pendingbook:" + keyAcc;

                try {
                    // Prima setKey
                    redisUtility.setKey(keyAcc, accommodationId.toHexString(), evaluateTTL(formattedDateStart));

                    // Seconda setKey
                    redisUtility.setKey(keyPeriod, dateStartWithoutDashes + dateEndWithoutDashes, evaluateTTL(formattedDateStart));

                } catch (Exception redisException) {
                    // Rollback in caso di errore su Redis
                    // Elimina solo le chiavi già impostate (in un caso di rollback più chiaro)
                    redisUtility.delete(keyAcc);
                    redisUtility.delete(keyPeriod);

                    accommodation.getBooks().remove(newBook);
                    accommodation.getOccupiedDates().removeIf(period -> period.getStart().equals(startDate) && period.getEnd().equals(endDate));
                    accommodationRepository.save(accommodation);

                    throw new RuntimeException("Failed to write booking to Redis, rollback MongoDB changes: " + redisException.getMessage(), redisException);
                }
            }

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
    public boolean deleteBook(String username, ObjectId accommodationId, LocalDate startDate, LocalDate endDate) {
        try {
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            Book bookToDelete = accommodation.getBooks().stream()
                    .filter(book -> book.getStartDate().equals(startDate) &&
                            book.getEndDate().equals(endDate) &&
                            book.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), bookToDelete.getStartDate());
            if (daysUntilStart < 2) {
                throw new IllegalArgumentException("Booking cannot be canceled as it exceeds the allowed cancellation period.");
            }

            // Backup della prenotazione per il rollback
            Book backupBook = new Book(bookToDelete);

            // Rimuoviamo la prenotazione dal database
            accommodation.getOccupiedDates().removeIf(period -> period.getStart().equals(startDate) &&
                    period.getEnd().equals(endDate));
            accommodation.getBooks().remove(bookToDelete);
            accommodationRepository.save(accommodation);

            // Utilizzo di DateFormatterUtil per formattare le date
            String dateStart = DateFormatterUtil.formatWithoutDashes(startDate);
            String dateEnd = DateFormatterUtil.formatWithoutDashes(endDate);
            String keyAcc = "username:" + username + ":accId:" + accommodationId + ":period:" + dateStart + dateEnd;
            String keyPeriod = "pendingbook:" + keyAcc;

            // Tentiamo di eliminare entrambe le chiavi Redis
            boolean deletedKeyAcc = redisUtility.delete(keyAcc);
            boolean deletedKeyPeriod = redisUtility.delete(keyPeriod);

            // Se almeno una delete fallisce, facciamo rollback completo (DB + Redis)
            if (!deletedKeyAcc || !deletedKeyPeriod) {
                // Ripristino della prenotazione nel database
                accommodation.getBooks().add(backupBook);
                accommodation.getOccupiedDates().addAll(bookToDelete.getOccupiedDates());
                accommodationRepository.save(accommodation);

                // Ripristino delle chiavi Redis se necessario
                String formattedStartDate = DateFormatterUtil.formatWithDashes(startDate); // yyyy-MM-dd per TTL
                if (deletedKeyAcc) {
                    redisUtility.setKey(keyAcc, accommodationId.toHexString(), evaluateTTL(formattedStartDate));
                }
                if (deletedKeyPeriod) {
                    redisUtility.setKey(keyPeriod, dateStart + dateEnd, evaluateTTL(formattedStartDate));
                }

                throw new RuntimeException("Failed to delete booking from Redis, rollback executed.");
            }

            return true;

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting the booking: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Validation error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while deleting the booking: " + e.getMessage(), e);
        }
    }




}
