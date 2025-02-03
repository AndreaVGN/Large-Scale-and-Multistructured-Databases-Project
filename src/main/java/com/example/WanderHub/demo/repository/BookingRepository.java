package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Repository
public class BookingRepository {

    private static final long TTL = 300; // 300 secondi (5 minuti)

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean lockHouse(int houseId, String start, String end) {
        String keyStart = "booking:" + start;
        String keyEnd = "booking:" + end;
        String accommodation= String.valueOf(houseId);
        String timeStamp = "booking_timestamp";
        /*if (Boolean.TRUE.equals(redisTemplate.hasKey(keyStart))) {
            return false; // Casa già prenotata temporaneamente
        }*/
        if (Boolean.TRUE.equals(redisTemplate.hasKey(keyEnd)) && Boolean.TRUE.equals(redisTemplate.hasKey(keyStart)) && Boolean.TRUE.equals(redisTemplate.hasKey(accommodation)) && Boolean.TRUE.equals(redisTemplate.hasKey(timeStamp))) {
            return false; // Casa già prenotata temporaneamente
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        redisTemplate.opsForValue().set(keyStart, start, TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(keyEnd, end, TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(accommodation, houseId, TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(timeStamp, now.format(formatter), TTL, TimeUnit.SECONDS);
        return true;
    }

    public boolean lockHouseReg(int houseId, String username, String start, String end) {
        String keyStart = "booking:" + start;
        String keyEnd = "booking:" + end;
        String accommodation= String.valueOf(houseId);
        String utente = username;
        String timeStamp = "booking_timestamp";
        /*if (Boolean.TRUE.equals(redisTemplate.hasKey(keyStart))) {
            return false; // Casa già prenotata temporaneamente
        }*/
        if (Boolean.TRUE.equals(redisTemplate.hasKey(keyEnd)) && Boolean.TRUE.equals(redisTemplate.hasKey(keyStart)) && Boolean.TRUE.equals(redisTemplate.hasKey(accommodation)) && Boolean.TRUE.equals(redisTemplate.hasKey(timeStamp))
        && Boolean.TRUE.equals(redisTemplate.hasKey(utente))) {
            return false; // Casa già prenotata temporaneamente
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        redisTemplate.opsForValue().set(keyStart, start, TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(keyEnd, end, TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(accommodation, houseId, TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(timeStamp, now.format(formatter), TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(utente, username, TTL, TimeUnit.SECONDS);
        return true;
    }
}
