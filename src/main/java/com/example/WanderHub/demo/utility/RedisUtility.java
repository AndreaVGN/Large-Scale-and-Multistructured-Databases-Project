package com.example.WanderHub.demo.utility;

import com.example.WanderHub.demo.model.Accommodation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    public Set<String> getKeys(String pattern) {

        return redisTemplate.keys(pattern);

    }

    public Boolean lock(String pattern) {

        return redisTemplate.opsForValue().setIfAbsent(pattern, "locked", lock_TTL, TimeUnit.SECONDS);

    }

    public Boolean delete(String pattern) {

        return redisTemplate.delete(pattern);

    }

    public void setKey(String key, String value, Long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }


    public String getValue(String key){

        return (String) redisTemplate.opsForValue().get(key);

    }
    public void saveAccommodation(Accommodation accommodation, String baseKey, Long TTL) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        // Serializzazione dei campi di tipo String
        ops.set(baseKey + ":description", accommodation.getDescription(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":type", accommodation.getType(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":place", accommodation.getPlace(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":city", accommodation.getCity(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":address", accommodation.getAddress(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":hostUsername", accommodation.getHostUsername(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":latitude", String.valueOf(accommodation.getLatitude()), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":longitude", String.valueOf(accommodation.getLongitude()), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":maxGuestSize", String.valueOf(accommodation.getMaxGuestSize()), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":costPerNight", String.valueOf(accommodation.getCostPerNight()), TTL, TimeUnit.SECONDS);

        // Salvataggio delle foto come chiavi separate
        String[] photos = accommodation.getPhotos();
        for (int i = 0; i < photos.length; i++) {
            ops.set(baseKey + ":photo:" + i, photos[i], TTL, TimeUnit.SECONDS);
        }

        // Salvataggio delle facilities come chiavi separate
        Map<String, Integer> facilities = accommodation.getFacilities();
        for (Map.Entry<String, Integer> entry : facilities.entrySet()) {
            ops.set(baseKey + ":facility:" + entry.getKey(), String.valueOf(entry.getValue()), TTL, TimeUnit.SECONDS);
        }
    }

    public boolean isOverlappingBooking(ObjectId accommodationId, String start, String end) {

        Set<String> existingKeys = getKeys("booking:accId:" + accommodationId + ":*");

        if (existingKeys != null) {
            LocalDate newStart = LocalDate.parse(start);
            LocalDate newEnd = LocalDate.parse(end);

            for (String key : existingKeys) {

                String[] parts = key.split(":");

                if (parts.length < 7) continue;

                LocalDate existingStart = LocalDate.parse(parts[4]);
                LocalDate existingEnd = LocalDate.parse(parts[6]);

                boolean isOverlapping = !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));
                boolean isNotTheSame = !(newStart.isEqual(existingStart) && newEnd.isEqual(existingEnd));

                if (isOverlapping && isNotTheSame) {
                    return true;  // Sovrapposizione trovata
                }

            }

        }

        return false;
    }



    public static long evaluateTTL(String dataFutura) {
        System.out.println(dataFutura);
        // Definiamo il formatter per il formato "yyyy-MM-dd"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Converti la stringa in LocalDate
        LocalDate futureDate = LocalDate.parse(dataFutura, formatter);

        // Converti LocalDate in LocalDateTime impostando l'orario a mezzanotte
        LocalDateTime futureDateTime = futureDate.atStartOfDay();

        System.out.println("Data convertita: " + futureDateTime); // Debug

        // Ottieni il timestamp attuale in secondi
        long currentTimestamp = Instant.now().getEpochSecond();

        // Converte la data futura in timestamp in secondi
        long futureTimestamp = futureDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();

        // Calcola il TTL (tempo rimanente in secondi)
        return Math.max(0, futureTimestamp - currentTimestamp);
    }
}

