package com.example.WanderHub.demo.utility;

import com.example.WanderHub.demo.model.Accommodation;
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
    @Qualifier("redisTemplateNearest")
    private RedisTemplate<String, Object> redisTemplate;
    private static final long lock_TTL = 1200;
    @Qualifier("redisTemplateNearest")
    @Autowired
    private RedisTemplate redisTemplateNearest;
    @Qualifier("redisTemplateMasterPreferred")
    @Autowired
    private RedisTemplate redisTemplateMasterPreferred;
    @Qualifier("redisTemplateReplica")
    @Autowired
    private RedisTemplate redisTemplateReplica;


    public void setKey(String key, String value, Long ttl) {
        // Scrive il valore con TTL usando RedisTemplate
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);

    }

    public Set<String> getKeys(String pattern) {

        if(pattern.startsWith("wanderhub:lock") || pattern.startsWith("wanderhub:booking")){
            return redisTemplate.keys(pattern);
        } else if (pattern.startsWith("wanderhub:newAcc")) {
            return redisTemplateMasterPreferred.keys(pattern);
        } else if (pattern.startsWith("wanderhub:sessions")) {
            return redisTemplate.keys(pattern);
        } else {
            return redisTemplateReplica.keys(pattern);
        }

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

        if(key.startsWith("wanderhub:lock") || key.startsWith("wanderhub:booking")){
            return (String) redisTemplate.opsForValue().get(key);
        } else if (key.startsWith("wanderhub:newAcc")) {
            return (String) redisTemplateMasterPreferred.opsForValue().get(key);
        } else if (key.startsWith("wanderhub:sessions")) {
            return (String) redisTemplate.opsForValue().get(key);
        } else {
            return (String) redisTemplateReplica.opsForValue().get(key);
        }
    }

    public void saveAccommodation(Accommodation accommodation, String accommodationKey, String username, String baseKey, Long TTL) {
        try {
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) {
                    // Avvia la transazione
                    operations.multi();

                    // Ottieni ValueOperations
                    ValueOperations<String, Object> ops = operations.opsForValue();

                    // Aggiungi i valori alla transazione
                    ops.set(accommodationKey, username, TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":description", accommodation.getDescription(), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":type", accommodation.getType(), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":place", accommodation.getPlace(), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":city", accommodation.getCity(), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":address", accommodation.getAddress(), TTL, TimeUnit.SECONDS);
                    // ops.set(baseKey + ":hostUsername", accommodation.getHostUsername(), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":latitude", String.valueOf(accommodation.getLatitude()), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":longitude", String.valueOf(accommodation.getLongitude()), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":maxGuestSize", String.valueOf(accommodation.getMaxGuestSize()), TTL, TimeUnit.SECONDS);
                    ops.set(baseKey + ":costPerNight", String.valueOf(accommodation.getCostPerNight()), TTL, TimeUnit.SECONDS);

                    // Gestisci le foto
                    String[] photos = accommodation.getPhotos();
                    for (int i = 0; i < photos.length; i++) {
                        ops.set(baseKey + ":photo:" + i, photos[i], TTL, TimeUnit.SECONDS);
                    }

                    // Gestisci le facilities
                    Map<String, Integer> facilities = accommodation.getFacilities();
                    for (Map.Entry<String, Integer> entry : facilities.entrySet()) {
                        ops.set(baseKey + ":facility:" + entry.getKey(), String.valueOf(entry.getValue()), TTL, TimeUnit.SECONDS);
                    }

                    // Esegui la transazione (commit)
                    return operations.exec(); // Tutte le operazioni vengono eseguite come una transazione
                }
            });
        } catch (Exception redisException) {
            // Rollback: elimina tutte le chiavi impostate
            deleteAccommodationKeys(accommodation, accommodationKey, baseKey, TTL);
            // Logga l'eccezione o lancia una RuntimeException
            throw new RuntimeException("Error while saving accommodation to Redis, rollback performed: " + redisException.getMessage(), redisException);
        }
    }

    private void deleteAccommodationKeys(Accommodation accommodation, String accommodationKey, String baseKey, Long TTL) {
        redisTemplate.delete(accommodationKey);
        redisTemplate.delete( baseKey + ":description");
        redisTemplate.delete( baseKey + ":type");
        redisTemplate.delete(baseKey + ":place");
        redisTemplate.delete(baseKey + ":city");
        redisTemplate.delete(baseKey + ":address");
        redisTemplate.delete(baseKey + ":latitude");
        redisTemplate.delete(baseKey + ":longitude");
        redisTemplate.delete(baseKey + ":maxGuestSize");
        redisTemplate.delete(baseKey + ":costPerNight");

        // Elimina foto
        String[] photos = accommodation.getPhotos();
        for (int i = 0; i < photos.length; i++) {
            redisTemplate.delete(baseKey + ":photo:" + i);
        }

        // Elimina facilities
        Map<String, Integer> facilities = accommodation.getFacilities();
        for (Map.Entry<String, Integer> entry : facilities.entrySet()) {
            redisTemplate.delete(baseKey + ":facility:" + entry.getKey());
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

    public boolean setKeyWithTransaction(String draftTextKey, String text, String draftRatingKey, String rating, long reviewTTL) {
        try {
            // Esegui la transazione con SessionCallback
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) {
                    // Avvia la transazione
                    operations.multi();

                    // Esegui le operazioni di setKey dentro la transazione
                    ValueOperations<String, Object> valueOps = operations.opsForValue();
                    valueOps.set(draftTextKey, text, reviewTTL, TimeUnit.SECONDS);
                    valueOps.set(draftRatingKey, rating, reviewTTL, TimeUnit.SECONDS);

                    // Esegui la transazione
                    return operations.exec(); // Commit della transazione
                }
            });
            return true;
        } catch (Exception e) {
            // Gestisci l'errore se qualcosa va storto
            delete(draftTextKey);
            delete(draftRatingKey);
            return false;
        }
    }






}

