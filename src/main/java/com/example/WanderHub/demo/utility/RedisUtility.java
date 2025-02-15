package com.example.WanderHub.demo.utility;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Review;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component  // Rende la classe un Bean di Spring
public class RedisUtility {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final long lock_TTL = 1200;



    public void setKey(String key, String value, Long ttl) {
        // Scrive il valore con TTL usando RedisTemplate
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);

    }

    public Set<String> getKeys(String pattern) {

            return redisTemplate.keys(pattern);

    }

    public Boolean lock(String pattern, long TTL) {

        return  redisTemplate.opsForValue().setIfAbsent(pattern, "locked", TTL, TimeUnit.SECONDS);

    }

    public Boolean lockBook(String pattern, long TTL, LocalDate start, LocalDate end) {

        return  redisTemplate.opsForValue().setIfAbsent(pattern, DateFormatterUtil.formatWithoutDashes(start) + DateFormatterUtil.formatWithoutDashes(end), TTL, TimeUnit.SECONDS);

    }

    public Boolean delete(String pattern) {

        return redisTemplate.delete(pattern);

    }


    public String getValue(String key){

            return (String) redisTemplate.opsForValue().get(key);
    }

    public void saveAccommodation(Accommodation accommodation, String accommodationKey, String username, String baseKey, Long TTL) {
        try {
            // Ottieni ValueOperations
            ObjectMapper objectMapper = new ObjectMapper();

            // Serializza l'oggetto Accommodation in JSON
            String json = objectMapper.writeValueAsString(accommodation);

            // Salva in Redis
            redisTemplate.opsForValue().set(baseKey, json);

        } catch (Exception redisException) {
            // Rollback: elimina tutte le chiavi impostate
            delete(accommodationKey);
            // Logga l'eccezione o lancia una RuntimeException
            throw new RuntimeException("Error while saving accommodation to Redis, rollback performed: " + redisException.getMessage(), redisException);
        }
    }

    public boolean isOverlappingBooking(ObjectId accommodationId, String newStart, String newEnd) {

        Set<String> existingKeys = getKeys("wanderhub:lock:accId:" + accommodationId + ":*");
        System.out.println(existingKeys);

        if (existingKeys != null) {

            for (String key : existingKeys) {

                String datesFormatted = getValue(key);
                System.out.println("Siamo qui" + datesFormatted);
                String oldStart = datesFormatted.substring(0, 8);
                String oldEnd = datesFormatted.substring(8, 16);

                boolean isOverlapping = !(newEnd.compareTo(oldStart) < 0 || newStart.compareTo(oldEnd) > 0);
                boolean isNotTheSame = !(newStart.equals(oldStart) && newEnd.equals(oldEnd));

                if (isOverlapping && isNotTheSame) {
                    return true;
                }

            }

        }

        return false;
    }

    public static long evaluateTTL(String dataFutura) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate futureDate = LocalDate.parse(dataFutura, formatter);

        LocalDateTime futureDateTime = futureDate.atStartOfDay();

        System.out.println("Data convertita: " + futureDateTime);

        long currentTimestamp = Instant.now().getEpochSecond();

        long futureTimestamp = futureDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();

        return Math.max(0, futureTimestamp - currentTimestamp);
    }

   public void saveDraftReview(String baseKey, Review review, Long TTL){
        try {
            // Ottieni ValueOperations
            ObjectMapper objectMapper = new ObjectMapper();

            // Serializza l'oggetto Accommodation in JSON
            String json = objectMapper.writeValueAsString(review);

            // Salva in Redis
            redisTemplate.opsForValue().set(baseKey, json, TTL);
        }
        catch (Exception redisException) {
            throw new RuntimeException("Error during draft review!");
        }
   }






}

