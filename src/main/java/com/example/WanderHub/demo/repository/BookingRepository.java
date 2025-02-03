package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Book;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Repository
public class BookingRepository {

    private static final long TTL = 300; // 300 secondi (5 minuti)
    private static final String LOCK_KEY = "booking_lock:"; // Prefix per il lock

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient; // Redisson client per il lock distribuito

    public boolean lockHouse(int houseId, String start, String end) {
        String lockKey = LOCK_KEY + houseId;  // Utilizza la chiave unica per ogni casa (houseId)
        RLock lock = redissonClient.getLock(lockKey); // Crea un lock distribuito basato sulla chiave

        try {
            // Acquisisci il lock in modo sicuro con timeout
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS); // Tentativo di lock per 100ms, scadenza di 10 secondi
            if (!isLocked) {
                return false; // Se il lock non è acquisibile, significa che qualcun altro lo sta già usando
            }

            // Procedi con la logica di booking solo se il lock è stato acquisito
            String keyStart = "booking:" + start;
            String keyEnd = "booking:" + end;
            String accommodation = String.valueOf(houseId);
            String timeStamp = "booking_timestamp";

            // Verifica se la casa è già prenotata
            if (Boolean.TRUE.equals(redisTemplate.hasKey(keyEnd)) && Boolean.TRUE.equals(redisTemplate.hasKey(keyStart))
                    && Boolean.TRUE.equals(redisTemplate.hasKey(accommodation)) && Boolean.TRUE.equals(redisTemplate.hasKey(timeStamp))) {
                return false; // Casa già prenotata temporaneamente
            }

            // Se non è già prenotata, procedi a mettere le chiavi in Redis
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            redisTemplate.opsForValue().set(keyStart, start, TTL, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(keyEnd, end, TTL, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(accommodation, houseId, TTL, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(timeStamp, now.format(formatter), TTL, TimeUnit.SECONDS);

            return true;

        } catch (InterruptedException | RedisConnectionException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Rilascia il lock una volta terminata l'operazione
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public boolean lockHouseReg(int houseId, String username, String start, String end) {
        String lockKey = LOCK_KEY + houseId + ":" + username; // Lock basato su houseId e username
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Acquisisci il lock in modo sicuro con timeout
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return false; // Se il lock non è acquisibile, significa che qualcun altro lo sta già usando
            }

            // Procedi con la logica di booking solo se il lock è stato acquisito
            String keyStart = "booking:" + start;
            String keyEnd = "booking:" + end;
            String accommodation = String.valueOf(houseId);
            String utente = username;
            String timeStamp = "booking_timestamp";

            if (Boolean.TRUE.equals(redisTemplate.hasKey(keyEnd)) && Boolean.TRUE.equals(redisTemplate.hasKey(keyStart))
                    && Boolean.TRUE.equals(redisTemplate.hasKey(accommodation)) && Boolean.TRUE.equals(redisTemplate.hasKey(timeStamp))
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

        } catch (InterruptedException | RedisConnectionException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Rilascia il lock una volta terminata l'operazione
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
