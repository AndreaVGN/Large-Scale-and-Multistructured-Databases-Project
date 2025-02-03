package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class BookingRepository {

    private static final long TTL = 300; // 300 secondi (5 minuti)

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean lockHouse(int houseId, String start, String end) {
        String keyStart = "booking:" + houseId +"inizio";
        String keyEnd = "booking:" + houseId +"fine";
        /*if (Boolean.TRUE.equals(redisTemplate.hasKey(keyStart))) {
            return false; // Casa già prenotata temporaneamente
        }*/
        if (Boolean.TRUE.equals(redisTemplate.hasKey(keyEnd)) && Boolean.TRUE.equals(redisTemplate.hasKey(keyStart))) {
            return false; // Casa già prenotata temporaneamente
        }
        redisTemplate.opsForValue().set(keyStart, start, TTL, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(keyEnd, end, TTL, TimeUnit.SECONDS);
        return true;
    }
}
