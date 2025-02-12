package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.utility.RedisUtility;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AccommodationTransferService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AccommodationRepository accommodationRepository;
    private final RedisUtility redisUtility;

    public AccommodationTransferService(RedisTemplate<String, Object> redisTemplate, AccommodationRepository accommodationRepository, RedisUtility redisUtility) {
        this.redisTemplate = redisTemplate;
        this.accommodationRepository = accommodationRepository;
        this.redisUtility = redisUtility;
    }

    // Insert the new accommodations from Redis to MongoDB (every nigth at 03:00)
    @Scheduled(cron = "0 0 3 * * ?") // Every night at 03:00
    //@PostConstruct
    @Transactional
    public void insertAccommodationsToMongoAtMidnight() {

        System.out.println("Start house transfer");

        Set<String> keys = redisTemplate.keys("newAcc:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<Accommodation> accommodations = new ArrayList<>();

        for (String key : keys) {
            String username = redisUtility.getValue(key);
            if (username == null) {
                continue;
            }
            Accommodation accommodation = new Accommodation();
            String description = redisUtility.getValue("newAccDetails:user:" + username + ":description");
            accommodation.setDescription((description));
            String type = redisUtility.getValue("newAccDetails:user:" + username + ":type");
            accommodation.setType(type);
            String place = redisUtility.getValue("newAccDetails:user:" + username + ":place");
            accommodation.setPlace(place);
            String city = redisUtility.getValue("newAccDetails:user:" + username + ":city");
            accommodation.setCity(city);
            String address = redisUtility.getValue("newAccDetails:user:" + username + ":address");
            accommodation.setAddress(address);
            accommodation.setHostUsername(username);
            double latitude = Double.parseDouble(redisUtility.getValue("newAccDetails:user:" + username + ":latitude"));
            accommodation.setLatitude(latitude);
            double longitude = Double.parseDouble(redisUtility.getValue("newAccDetails:user:" + username + ":longitude"));
            accommodation.setLongitude(longitude);
            int maxGuestSize = Integer.parseInt(redisUtility.getValue("newAccDetails:user:" + username + ":maxGuestSize"));
            accommodation.setMaxGuestSize(maxGuestSize);
            int costPerNight = Integer.parseInt(redisUtility.getValue("newAccDetails:user:" + username + ":costPerNight"));
            accommodation.setCostPerNight(costPerNight);

            Set<String> photoKeys = redisTemplate.keys("newAccDetails:user:" + username + ":photo:*");
            if (photoKeys != null) {
                List<String> photos = new ArrayList<>();
                for (String photoKey : photoKeys) {
                    String photo = (String) redisTemplate.opsForValue().get(photoKey);
                    if (photo != null) {
                        photos.add(photo);
                    }
                }
                accommodation.setPhotos(photos.toArray(new String[0]));
            }

            Set<String> facilityKeys = redisTemplate.keys("newAccDetails:user:" + username +  ":facility:*");
            if (facilityKeys != null) {
                Map<String, Integer> facilities = new HashMap<>();
                for (String facilityKey : facilityKeys) {
                    String facilityName = facilityKey.split(":")[3]; // Estrai il nome della facility
                    String value = (String) redisTemplate.opsForValue().get(facilityKey);
                    if (value != null) {
                        facilities.put(facilityName, Integer.parseInt(value));
                    }
                }
                accommodation.setFacilities(facilities);
            }

            accommodations.add(accommodation);
        }

        accommodationRepository.saveAll(accommodations);

        redisTemplate.delete(keys);
        Set<String> keysNewAcc = redisTemplate.keys("newAccDetails:*");
        redisTemplate.delete(keysNewAcc);

        System.out.println("End house transfer");
    }
}
