package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.utility.RedisUtility;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

import java.nio.charset.StandardCharsets;
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
    //@Scheduled(cron = "0 0 3 * * ?") // Every night at 03:00
    @PostConstruct
    @Transactional

    public void insertAccommodationsToMongoAtMidnight() {
        try {
            System.out.println("Start house transfer");

            Set<String> keys = redisTemplate.keys("wanderhub:{newAccDetails}:*");
            if (keys == null || keys.isEmpty()) {
                System.out.println("No new accommodations found in Redis.");
                return;
            }

            List<Accommodation> accommodations = new ArrayList<>();

            for (String key : keys) {
                // Usa redisTemplate per ottenere il valore direttamente
                String json = (String) redisTemplate.opsForValue().get(key);
                if (json == null) continue;

                // Usando ObjectMapper solo per deserializzare
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                Accommodation accommodation = objectMapper.readValue(json, Accommodation.class);

                accommodations.add(accommodation);
            }

            if (!accommodations.isEmpty()) {
                accommodationRepository.saveAll(accommodations);
                System.out.println("Accommodations successfully saved.");
            } else {
                System.out.println("No accommodation to save");
            }

            // Elimina le chiavi processate
            redisTemplate.delete(keys);

            System.out.println("End house transfer");
        } catch (Exception e) {
            System.err.println("Error during accommodations transfer.");
            e.printStackTrace();
        }
    }

}
