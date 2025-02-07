package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.BookRepository;
import com.example.WanderHub.demo.utility.RedisUtility;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RedisUtility redisUtility;

    private static final long TTL = 1200;
    private static final long reviewTTL = 21600;// 300 secondi (5 minuti)
    private static final String LOCK_KEY = "booking_lock:"; // Prefix per il lock

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Creazione di una nuova sistemazione
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

   public List<Book> getBooksByCityAndPeriod(String city, String period) {
        return bookRepository.findByCityAndPeriod(city, period);
    }


    private boolean isOverlappingBooking(ObjectId accommodationId, String start, String end) {

        Set<String> existingKeys = redisUtility.getKeys("booking:accId:" + accommodationId + ":*");
        if (existingKeys != null) {
            LocalDate newStart = LocalDate.parse(start);
            LocalDate newEnd = LocalDate.parse(end);

            for (String key : existingKeys) {
                String[] parts = key.split(":");
                if (parts.length < 7) continue; // Deve contenere description, start, end e timestamp/username

                LocalDate existingStart = LocalDate.parse(parts[4]);
                LocalDate existingEnd = LocalDate.parse(parts[6]);

                boolean isOverlapping = !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));

                if (isOverlapping) {
                    return true;  // Sovrapposizione trovata
                }
            }
        }
        return false;
    }

    public String lockHouse(ObjectId accommodationId, String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate inizio = LocalDate.parse(start, formatter);
        if (inizio.isBefore(today)) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        LocalDate fine = LocalDate.parse(end, formatter);
        if (fine.isAfter(today.plusYears(1))) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        if(inizio.isAfter(fine)){
            throw new RuntimeException("periodo di tempo non valido.");
        }

        String lockKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;

        // Tentiamo di acquisire il lock utilizzando SETNX (Set if Not Exists)

        if (redisUtility.lock(lockKey) == null || !redisUtility.lock(lockKey)) {
            return null; // Impossibile acquisire il lock
        }

        // Se c'è una sovrapposizione, rilascia subito il lock e ritorna false
        if (isOverlappingBooking(accommodationId, start, end)) {
            redisUtility.delete(lockKey); // Rilascia il lock subito
            return null;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());


        //redisTemplate.opsForValue().set(lockKey, timestamp, TTL, TimeUnit.SECONDS);
        redisUtility.setKey(lockKey, timestamp, TTL);

        return timestamp; // Restituisce il timestamp generato
    }

    public boolean lockHouseReg(ObjectId accommodationId, String username, String start, String end) {
        System.out.println("DEBUG: Start date received -> " + start);
        System.out.println("DEBUG: End date received -> " + end);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate inizio = LocalDate.parse(start, formatter);
        if (inizio.isBefore(today)) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        LocalDate fine = LocalDate.parse(end, formatter);
        if (fine.isAfter(today.plusYears(1))) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        if(inizio.isAfter(fine)){
            throw new RuntimeException("periodo di tempo non valido.");
        }
        accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new RuntimeException("Accommodation not found"));
        String lockKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;

        // Tentiamo di acquisire il lock utilizzando SETNX
        if (redisUtility.lock(lockKey) == null || !redisUtility.lock(lockKey)) {
            return false; // Impossibile acquisire il lock
        }

        // Se c'è una sovrapposizione, rilascia subito il lock e ritorna false
        if (isOverlappingBooking(accommodationId, start, end)) {
            System.out.println("Lock not acquired per sovrapposizione!");
            redisUtility.delete(lockKey);; // Rilascia il lock subito
            return false;
        }

        redisUtility.setKey(lockKey, username,TTL);

        return true;
    }

    public boolean unlockHouse(ObjectId houseId, String start, String end, String timestampCookie) {
        String lockKey = "booking:accId:" + houseId + ":start:" + start + ":end:" + end;

        String storedTimestamp = redisUtility.getValue(lockKey);

        if (storedTimestamp != null && storedTimestamp.equals(timestampCookie)) {
            redisUtility.delete(lockKey); // Rilascia il lock
            return true;
        }

        return false;
    }

    public boolean unlockHouseReg(ObjectId houseId, String username, String start, String end) {
        String lockKey = "booking:accId:" + houseId + ":start:" + start + ":end:" + end;

        String storedUsername = redisUtility.getValue(lockKey);

        if (storedUsername != null && storedUsername.equals(username)) {
            redisUtility.delete(lockKey); // Elimina la prenotazione
            return true;
        }

        return false;
    }

    public void addBozza(String username, ObjectId accommodationId, Review review) {
        String text = review.getReviewText();
        String rating = String.valueOf(review.getRating());

        String bozzaTextKey = "review:accId:" + accommodationId + ":username:" + username +":text";
        String bozzaRatingKey = "review:accId:" + accommodationId + ":username" + username +":rating";
        redisUtility.setKey(bozzaTextKey, text, reviewTTL);
        redisUtility.setKey(bozzaRatingKey, rating, reviewTTL);

    }

    public void insertAccommodation(Accommodation accommodation) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String baseKey = "accommodation:" + timestamp;
        redisUtility.saveAccommodation(accommodation,baseKey,TTL);

    }



}
