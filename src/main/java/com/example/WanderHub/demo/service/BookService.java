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
    public Object lockHouse(String accommodationId, String start, String end, String username) {
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
            String lockKey = "wanderhub:lock:accId:{" + accommodationId + "}:start:" + startFormatted + ":end:" + endFormatted;

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
                    redisUtility.setKey("wanderhub:booking" + "lock:accId:" + accommodationId + ":start:" + startFormatted + ":end:" +
                            endFormatted + ":user", userIdentifier, lockTTL);
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
    public boolean unlockHouse(String houseId, String start, String end, String userIdentifier) {
        try {

            LocalDate startDate = DateFormatterUtil.parseWithDashes(start); // yyyy-MM-dd -> LocalDate
            LocalDate endDate = DateFormatterUtil.parseWithDashes(end); // yyyy-MM-dd -> LocalDate

            String startFormatted = DateFormatterUtil.formatWithoutDashes(startDate); // yyyymmdd
            String endFormatted = DateFormatterUtil.formatWithoutDashes(endDate); // yyyymmdd

            String bookinglockKey = "wanderhub:bookinglock:accId:" + houseId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";
            String username = redisUtility.getValue(bookinglockKey);
            String lockKey = "wanderhub:lock:accId:" + houseId + ":start:" + startFormatted + ":end:" + endFormatted;

            String storedValue = redisUtility.getValue(bookinglockKey);

            if (username != null && username.equals(userIdentifier)) {
                boolean lockKeyDel = redisUtility.delete(bookinglockKey);
                boolean bookingLockKeyDel = redisUtility.delete(lockKey);

                if (!lockKeyDel || !bookingLockKeyDel) {
                    if (lockKeyDel) {
                        redisUtility.setKey(bookinglockKey, storedValue, lockTTL);
                    }
                    if (bookingLockKeyDel) {
                        redisUtility.setKey(lockKey, startFormatted + endFormatted, lockTTL);
                    }
                    throw new RuntimeException("Failed to delete booking from Redis, rollback executed.");
                }
                return true;
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while unlocking the house: " + e.getMessage(), e);
        }
    }

    // Return all the future books (basic informations) of a accommodation done by a registered user
    public List<PendingBook> getPendingBookings(String username) {
        RegisteredUser user = registeredUserRepository.findById(username).orElse(null);

        System.out.println(user);
        if (user == null) {
            return List.of();
        }

        LocalDate today = LocalDate.now();
        return user.getBooks().stream()
                .filter(pendingBook -> pendingBook.getStartDate().isAfter(today))
                .collect(Collectors.toList());
    }


    // Add a new book for both registered and unregistered users to an accommodation.
    public Accommodation addBookToAccommodation(String username, String accommodationId, Book newBook, boolean isLogged) {
        try {
            LocalDate startDate = newBook.getStartDate();
            LocalDate endDate = newBook.getEndDate();

            String startFormatted = DateFormatterUtil.formatWithoutDashes(startDate);
            String endFormatted = DateFormatterUtil.formatWithoutDashes(endDate);

            int overlappingPeriodCount = accommodationRepository.checkAvailability(accommodationId, startDate, endDate);

            if (overlappingPeriodCount > 0) {
                throw new IllegalArgumentException("Accommodation " + accommodationId + " is not available for the selected period.");
            }

            Validator.validateBook(newBook);

            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            String lockKey = "wanderhub:bookinglock:accId:" + accommodationId + ":start:" + startFormatted + ":end:" + endFormatted + ":user";


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
            return savedAccommodation;

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }


    // Delete a book with startDate of the book at least 2 days forward than the current date
    @Transactional
    public boolean deleteBook(String username, String accommodationId, LocalDate startDate, LocalDate endDate) {
        RegisteredUser user = registeredUserRepository.findById(username).orElse(null);
        if (user == null) {
            return false;
        }

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
            return false;
        }

        user.getBooks().remove(pendingBookToRemove);
        registeredUserRepository.save(user);

        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId).orElse(null);
        if (accommodation == null) {
            return false; // Se l'Accommodation non esiste, ritorna false
        }

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

        accommodation.getOccupiedDates().removeIf(period ->
                period.getStart().equals(startDate) && period.getEnd().equals(endDate));

        accommodationRepository.save(accommodation);

        return true;
    }




}
