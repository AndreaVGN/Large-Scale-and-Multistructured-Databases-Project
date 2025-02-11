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

    public Boolean lock(String pattern, long TTL) {

        return redisTemplate.opsForValue().setIfAbsent(pattern, "locked", TTL, TimeUnit.SECONDS);

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

        ops.set(baseKey + ":description", accommodation.getDescription(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":type", accommodation.getType(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":place", accommodation.getPlace(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":city", accommodation.getCity(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":address", accommodation.getAddress(), TTL, TimeUnit.SECONDS);
        //ops.set(baseKey + ":hostUsername", accommodation.getHostUsername(), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":latitude", String.valueOf(accommodation.getLatitude()), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":longitude", String.valueOf(accommodation.getLongitude()), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":maxGuestSize", String.valueOf(accommodation.getMaxGuestSize()), TTL, TimeUnit.SECONDS);
        ops.set(baseKey + ":costPerNight", String.valueOf(accommodation.getCostPerNight()), TTL, TimeUnit.SECONDS);

        String[] photos = accommodation.getPhotos();
        for (int i = 0; i < photos.length; i++) {
            ops.set(baseKey + ":photo:" + i, photos[i], TTL, TimeUnit.SECONDS);
        }

        Map<String, Integer> facilities = accommodation.getFacilities();
        for (Map.Entry<String, Integer> entry : facilities.entrySet()) {
            ops.set(baseKey + ":facility:" + entry.getKey(), String.valueOf(entry.getValue()), TTL, TimeUnit.SECONDS);
        }
    }

    public boolean isOverlappingBooking(ObjectId accommodationId, String newStart, String newEnd) {

        Set<String> existingKeys = getKeys("bookinglock:accId:" + accommodationId + ":*");

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
}

