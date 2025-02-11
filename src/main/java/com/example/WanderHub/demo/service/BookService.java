package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.PendingBooksDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.AccommodationRepository;
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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate today = LocalDate.now();
            LocalDate startDate = LocalDate.parse(start, formatter);
            LocalDate endDate = LocalDate.parse(end, formatter);

            if (startDate.isBefore(today) || endDate.isAfter(today.plusYears(1)) || startDate.isAfter(endDate)) {
                throw new RuntimeException("Not valid time period");
            }

            if (username != null) {
                accommodationRepository.findByAccommodationId(accommodationId)
                        .orElseThrow(() -> new RuntimeException("Accommodation not found"));
            }

            String startFormatted = start.replaceAll("-", "");
            String endFormatted = end.replaceAll("-", "");
            String lockKey = "lock:accId:" + accommodationId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";

            Boolean successLock = redisUtility.lock(lockKey);

            if (successLock == null || !successLock) {
                return username == null ? null : false;
            }


            if (redisUtility.isOverlappingBooking(accommodationId, startFormatted, endFormatted)) {
                redisUtility.delete(lockKey);
                return username == null ? null : false;
            }


            String valueToStore = (username == null) ? String.valueOf(System.currentTimeMillis()) : username;
            redisUtility.setKey(lockKey, valueToStore, lockTTL);
            redisUtility.setKey( "booking" + lockKey + ":" + username, startFormatted + endFormatted, lockTTL);

            return username == null ? valueToStore : true;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while locking the house: " + e.getMessage(), e);
        }
    }

    // Unlock a previously locked accommodation (both for registered and unregistered users)
    public boolean unlockHouse(ObjectId houseId, String start, String end, String userIdentifier) {
        try {
            String startFormatted = start.replaceAll("-", "");
            String endFormatted = end.replaceAll("-", "");
            String lockKey = "lock:accId:" + houseId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";
            String username = redisUtility.getValue(lockKey);
            String bookingLockKey = "booking" + lockKey + ":" + username;

            String storedValue = redisUtility.getValue(lockKey);

            if (username != null && username.equals(userIdentifier)) {
                redisUtility.delete(lockKey);
                redisUtility.delete(bookingLockKey);
                return true;
            }

            return false;
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

                        Set<String> bookingKeys = redisUtility.getKeys("book:username:" + username + ":accId:" + accommodationId + ":*");

                        if (bookingKeys != null) {
                            for (String bookingKey : bookingKeys) {
                                try {
                                    String dateRange = redisUtility.getValue(bookingKey);

                                    if (dateRange == null || dateRange.length() < 16) {
                                        continue;
                                    }

                                    String startDateStr = dateRange.substring(0, 8);
                                    String endDateStr = dateRange.substring(8, 16);

                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                                    LocalDate startDate = LocalDate.parse(startDateStr, formatter);
                                    LocalDate endDate = LocalDate.parse(endDateStr, formatter);

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String startFormatted = startDate.format(formatter);
            String endFormatted = endDate.format(formatter);

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
                DateTimeFormatter formatterStart = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDateStart = startDate.format(formatterStart);
                String DateStart = formattedDateStart.replaceAll("-","");

                DateTimeFormatter formatterEnd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDateEnd = endDate.format(formatterEnd);
                String DateEnd = formattedDateEnd.replaceAll("-","");

                String keyAcc = "username:" + username + ":accId:" + accommodationId + ":period:" + DateStart+ DateEnd;
                String keyPeriod = "book:"+keyAcc;
                try {
                    redisUtility.setKey(keyAcc, accommodationId.toHexString(), evaluateTTL(formattedDateStart));
                    redisUtility.setKey(keyPeriod,DateStart+DateEnd, evaluateTTL(formattedDateStart));

                } catch (Exception redisException) {
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

            Book backupBook = new Book(bookToDelete);

            accommodation.getOccupiedDates().removeIf(period -> period.getStart().equals(startDate) &&
                    period.getEnd().equals(endDate));

            accommodation.getBooks().remove(bookToDelete);
            accommodationRepository.save(accommodation);

            String key = "username:" + username + ":accId:" + accommodationId + ":startDate:" + startDate;
            if (!redisUtility.delete(key)) {

                accommodation.getBooks().add(backupBook);

                accommodation.getOccupiedDates().addAll(bookToDelete.getOccupiedDates());
                accommodationRepository.save(accommodation);
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
