package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Book;
import jakarta.servlet.http.Cookie;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class BookingRepository {

    private static final long TTL = 600; // 300 secondi (5 minuti)
    private static final String LOCK_KEY = "booking_lock:"; // Prefix per il lock

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient; // Redisson client per il lock distribuito

    /*public boolean lockHouse(int houseId, String start, String end) {
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
            String accommodation = "booking"+ String.valueOf(houseId);
            String timeStamp = "booking" + "timestamp";

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
    }*/
    /*public boolean lockHouse(int houseId, String start, String end) {
        String lockKey = LOCK_KEY + houseId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return false;
            }

            // Chiave base per le prenotazioni di questa casa
            String houseBookingPattern = "booking:" + houseId + ":*";

            // Cerca tutte le chiavi corrispondenti in Redis
            Set<String> existingKeys = redisTemplate.keys(houseBookingPattern);

            if (existingKeys != null) {
                LocalDate newStart = LocalDate.parse(start);
                LocalDate newEnd = LocalDate.parse(end);

                for (String key : existingKeys) {
                    // Estrarre la data dalla chiave esistente
                    String[] parts = key.split(":");
                    if (parts.length < 3) continue;

                    LocalDate existingStart = LocalDate.parse(parts[2]);
                    LocalDate existingEnd = LocalDate.parse(parts[3]);

                    // Controllo della sovrapposizione delle date
                    boolean isOverlapping = !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));

                    if (isOverlapping) {
                        return false;  // Sovrapposizione trovata, prenotazione rifiutata
                    }
                }
            }

            // Se non ci sono sovrapposizioni, salva la nuova prenotazione in Redis
            String keyStart = "booking:" + houseId + ":" + start + ":" + end;

            redisTemplate.opsForValue().set(keyStart, "reserved", TTL, TimeUnit.SECONDS);
            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }*/


    /*public boolean lockHouseReg(int houseId, String username, String start, String end) {
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
    }*/
    /*public boolean lockHouseReg(int houseId, String username, String start, String end) {
        String lockKey = "lock:house:" + houseId + ":" + username;  // Lock specifico per houseId + username
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return false;
            }

            // Chiave base per le prenotazioni della casa
            String houseBookingPattern = "booking:" + houseId + ":*";

            // Cerca tutte le chiavi esistenti per la casa
            Set<String> existingKeys = redisTemplate.keys(houseBookingPattern);

            if (existingKeys != null) {
                LocalDate newStart = LocalDate.parse(start);
                LocalDate newEnd = LocalDate.parse(end);

                for (String key : existingKeys) {
                    // Estrarre la data e l'utente dalla chiave esistente
                    String[] parts = key.split(":");
                    if (parts.length < 5) continue;  // La chiave deve contenere houseId, start, end, username

                    LocalDate existingStart = LocalDate.parse(parts[2]);
                    LocalDate existingEnd = LocalDate.parse(parts[3]);
                    String existingUser = parts[4];

                    // Controllo della sovrapposizione delle date
                    boolean isOverlapping = !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));

                    if (isOverlapping) {
                        return false;  // Prenotazione sovrapposta, rifiutata
                    }
                }
            }

            // Se non ci sono sovrapposizioni, salva la nuova prenotazione in Redis
            String bookingKey = "booking:" + houseId + ":" + start + ":" + end + ":" + username;

            redisTemplate.opsForValue().set(bookingKey, "reserved", TTL, TimeUnit.SECONDS);
            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }*/
    private boolean isOverlappingBooking(int houseId, String start, String end) {
        String houseBookingPattern = "booking:" + houseId + ":*";  // Cerca tutte le prenotazioni per una casa
        Set<String> existingKeys = redisTemplate.keys(houseBookingPattern);

        if (existingKeys != null) {
            LocalDate newStart = LocalDate.parse(start);
            LocalDate newEnd = LocalDate.parse(end);

            for (String key : existingKeys) {
                String[] parts = key.split(":");
                if (parts.length < 4) continue; // Deve contenere houseId, start, end e timestamp/username

                LocalDate existingStart = LocalDate.parse(parts[2]);
                LocalDate existingEnd = LocalDate.parse(parts[3]);

                boolean isOverlapping = !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));

                if (isOverlapping) {
                    return true;  // Sovrapposizione trovata
                }
            }
        }
        return false;
    }

    /*
    public boolean lockHouse(int houseId, String start, String end) {
        String lockKey = "lock:house:" + houseId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (!isLocked) return false;

            if (isOverlappingBooking(houseId, start, end)) return false;  // Controlla sovrapposizioni

            String timestamp = String.valueOf(System.currentTimeMillis());
            String bookingKey = "booking:" + houseId + ":" + start + ":" + end;


            redisTemplate.opsForValue().set(bookingKey, timestamp, TTL, TimeUnit.SECONDS);
            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }*/

    public String lockHouse(int houseId, String start, String end) {
        String lockKey = "lock:house:" + houseId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (!isLocked) return null; // Indica che non è stato possibile acquisire il lock

            if (isOverlappingBooking(houseId, start, end)) return null;  // Controlla sovrapposizioni

            String timestamp = String.valueOf(System.currentTimeMillis());
            String bookingKey = "booking:" + houseId + ":" + start + ":" + end;

            // Imposta il timestamp in Redis
            redisTemplate.opsForValue().set(bookingKey, timestamp, TTL, TimeUnit.SECONDS);

            return timestamp; // Restituisce il timestamp generato

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null; // Indica errore
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }


    public boolean lockHouseReg(int houseId, String username, String start, String end) {
        String lockKey = "lock:house:" + houseId + ":" + username;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (!isLocked) return false;

            if (isOverlappingBooking(houseId, start, end)) return false;  // Controlla sovrapposizioni

            String bookingKey = "booking:" + houseId + ":" + start + ":" + end;

            redisTemplate.opsForValue().set(bookingKey, username, TTL, TimeUnit.SECONDS);
            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

}
