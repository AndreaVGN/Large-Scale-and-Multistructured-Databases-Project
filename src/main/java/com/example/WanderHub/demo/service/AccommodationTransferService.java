package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.AccommodationRepository;
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

    public AccommodationTransferService(RedisTemplate<String, Object> redisTemplate, AccommodationRepository accommodationRepository) {
        this.redisTemplate = redisTemplate;
        this.accommodationRepository = accommodationRepository;
    }

    // Insert the new accommodations from Redis to MongoDB (every nigth at 03:00)
    @Scheduled(cron = "0 0 3 * * ?") // Every night at 03:00
    //@PostConstruct
    @Transactional
    public void insertAccommodationsToMongoAtMidnight() {

        Set<String> keys = redisTemplate.keys("accommodation:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        Map<String, Accommodation> accommodationsMap = new HashMap<>();

        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length < 3) continue;

            String timestamp = parts[1];
            String field = parts[2];     // Specific field (description, type, etc.)

            Accommodation accommodation = accommodationsMap.computeIfAbsent(timestamp, k -> new Accommodation());

            Object value = redisTemplate.opsForValue().get(key);

            if (value != null) {
                switch (field) {
                    case "description":
                        if (value instanceof String) {
                            accommodation.setDescription((String) value);
                        }
                        break;
                    case "type":
                        if (value instanceof String) {
                            accommodation.setType((String) value);
                        }
                        break;
                    case "place":
                        if (value instanceof String) {
                            accommodation.setPlace((String) value);
                        }
                        break;
                    case "city":
                        if (value instanceof String) {
                            accommodation.setCity((String) value);
                        }
                        break;
                    case "address":
                        if (value instanceof String) {
                            accommodation.setAddress((String) value);
                        }
                        break;
                    case "hostUsername":
                        if (value instanceof String) {
                            accommodation.setHostUsername((String) value);
                        }
                        break;
                    case "latitude":
                        if (value instanceof String) {
                            accommodation.setLatitude(Double.parseDouble((String) value));
                        }
                        break;
                    case "longitude":
                        if (value instanceof String) {
                            accommodation.setLongitude(Double.parseDouble((String) value));
                        }
                        break;
                    case "maxGuestSize":
                        if (value instanceof String) {
                            accommodation.setMaxGuestSize(Integer.parseInt((String) value));
                        }
                        break;
                    case "costPerNight":
                        if (value instanceof String) {
                            accommodation.setCostPerNight(Integer.parseInt((String) value));
                        }
                        break;
                    case "photos":
                        if (value instanceof String) {
                            accommodation.setPhotos(new String[]{(String) value});
                        } else if (value instanceof String[]) {
                            accommodation.setPhotos((String[]) value);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        accommodationsMap.forEach((timestamp, accommodation) -> {
            Set<String> photoKeys = redisTemplate.keys("accommodation:" + timestamp + ":photo:*");
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
        });

        accommodationsMap.forEach((timestamp, accommodation) -> {
            Set<String> facilityKeys = redisTemplate.keys("accommodation:" + timestamp + ":facility:*");
            if (facilityKeys != null) {
                Map<String, Integer> facilities = new HashMap<>();
                for (String facilityKey : facilityKeys) {
                    String facilityName = facilityKey.split(":")[3]; // Estrai il nome della facility
                    String value = (String) redisTemplate.opsForValue().get(facilityKey);
                    if (value != null) {
                        // Salva il valore delle facilities come integer
                        facilities.put(facilityName, Integer.parseInt(value));
                    }
                }
                accommodation.setFacilities(facilities);
            }
        });

        accommodationsMap.values().forEach(accommodation -> {
            System.out.println("Accommodation loaded: " + accommodation.getDescription());
            System.out.println("Facilities: " + accommodation.getFacilities());
            System.out.println("Photos: " + Arrays.toString(accommodation.getPhotos()));
        });

        List<Accommodation> accommodations = new ArrayList<>(accommodationsMap.values());

        accommodationRepository.saveAll(accommodations);

        redisTemplate.delete(keys);
    }
}
