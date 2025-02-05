/*package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;  // Importa il modulo per il supporto delle date Java 8

@Service
public class DataPopulatorService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @PostConstruct
    public void populateData() {
        try {
            // Crea l'ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Registrazione del modulo per il supporto delle date Java 8
            objectMapper.registerModule(new JavaTimeModule());

            // Abilita il supporto per numeri come NaN
            objectMapper.enable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);

            System.out.println("Dati popolati correiiiiiiiiiiiiiiiiittamente nel database.");

            // Carica il file JSON e mappa correttamente in una lista di oggetti Accommodation
            List<Accommodation> accommodations = objectMapper.readValue(new FileReader("C:/Users/franc/Downloads/popolamentoAccommodationDefinitivo.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Accommodation.class));

            // Salva i dati nel database
            accommodationRepository.saveAll(accommodations);

            System.out.println("Dati popolati correttamente nel database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/