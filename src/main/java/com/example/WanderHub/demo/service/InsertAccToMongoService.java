/*package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class InsertAccToMongoService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper;

    public InsertAccToMongoService(RedisTemplate<String, Object> redisTemplate, AccommodationRepository accommodationRepository, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.accommodationRepository = accommodationRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void insertAccommodationsToMongoAtMidnight() {
        // Prendi tutte le chiavi che corrispondono al pattern "accommodation:*"
        Set<String> keys = redisTemplate.keys("accommodation:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        Map<String, Accommodation> accommodationsMap = new HashMap<>();

        // Itera su tutte le chiavi di Redis
        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length < 3) continue;

            String timestamp = parts[1]; // L'elemento temporale
            String field = parts[2];     // Il campo specifico (description, type, etc.)

            // Se la mappa non contiene già una entry per il timestamp, creala
            Accommodation accommodation = accommodationsMap.computeIfAbsent(timestamp, k -> new Accommodation());

            // Ottieni il valore da Redis per la chiave
            Object value = redisTemplate.opsForValue().get(key);

            if (value != null) {
                // Gestisci ciascun campo separatamente, assicurandosi che il tipo sia trattato correttamente
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
                    case "facilities":
                        if (value instanceof String) {
                            try {
                                // Deserializza facilities come mappa da stringa
                                Map<String, Integer> facilities = objectMapper.readValue((String) value, Map.class);
                                accommodation.setFacilities(facilities);
                            } catch (Exception e) {
                                // Gestione errore deserializzazione
                                e.printStackTrace();
                            }
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
                            // Converte da stringa a double
                            accommodation.setLatitude(Double.parseDouble((String) value));
                        }
                        break;
                    case "longitude":
                        if (value instanceof String) {
                            // Converte da stringa a double
                            accommodation.setLongitude(Double.parseDouble((String) value));
                        }
                        break;
                    case "maxGuestSize":
                        if (value instanceof String) {
                            // Converte da stringa a int
                            accommodation.setMaxGuestSize(Integer.parseInt((String) value));
                        }
                        break;
                    case "costPerNight":
                        if (value instanceof String) {
                            // Converte da stringa a int
                            accommodation.setCostPerNight(Integer.parseInt((String) value));
                        }
                        break;
                    case "photos":
                        if (value instanceof String) {
                            // Se il valore è un URL, trattalo come una stringa
                            accommodation.setPhotos(new String[]{(String) value});
                        } else if (value instanceof String[]) {
                            // Se è un array di URL, trattalo come un array di stringhe
                            accommodation.setPhotos((String[]) value);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        // Converti la mappa in una lista di oggetti da salvare
        List<Accommodation> accommodations = new ArrayList<>(accommodationsMap.values());
        // Salva tutte le informazioni in MongoDB
        accommodationRepository.saveAll(accommodations);

        // Elimina le chiavi da Redis dopo averle salvate in MongoDB
        redisTemplate.delete(keys);
    }
}*/
package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class InsertAccToMongoService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper;

    public InsertAccToMongoService(RedisTemplate<String, Object> redisTemplate, AccommodationRepository accommodationRepository, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.accommodationRepository = accommodationRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void insertAccommodationsToMongoAtMidnight() {
        // Prendi tutte le chiavi che corrispondono al pattern "accommodation:*"
        Set<String> keys = redisTemplate.keys("accommodation:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        Map<String, Accommodation> accommodationsMap = new HashMap<>();

        // Itera su tutte le chiavi di Redis
        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length < 3) continue;

            String timestamp = parts[1]; // L'elemento temporale
            String field = parts[2];     // Il campo specifico (description, type, etc.)

            // Se la mappa non contiene già una entry per il timestamp, creala
            Accommodation accommodation = accommodationsMap.computeIfAbsent(timestamp, k -> new Accommodation());

            // Ottieni il valore da Redis per la chiave
            Object value = redisTemplate.opsForValue().get(key);

            if (value != null) {
                // Gestisci ciascun campo separatamente, assicurandosi che il tipo sia trattato correttamente
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
                            // Se il valore è un URL, trattalo come una stringa
                            accommodation.setPhotos(new String[]{(String) value});
                        } else if (value instanceof String[]) {
                            // Se è un array di URL, trattalo come un array di stringhe
                            accommodation.setPhotos((String[]) value);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        // Carica le foto da Redis
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

        // Carica le facilities da Redis
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

        // Verifica se le informazioni vengono caricate correttamente
        accommodationsMap.values().forEach(accommodation -> {
            System.out.println("Accommodation loaded: " + accommodation.getDescription());
            System.out.println("Facilities: " + accommodation.getFacilities());
            System.out.println("Photos: " + Arrays.toString(accommodation.getPhotos()));
        });

        // Converti la mappa in una lista di oggetti da salvare
        List<Accommodation> accommodations = new ArrayList<>(accommodationsMap.values());

        // Salva tutte le informazioni in MongoDB
        accommodationRepository.saveAll(accommodations);

        // Elimina le chiavi da Redis dopo averle salvate in MongoDB
        redisTemplate.delete(keys);
    }
}





