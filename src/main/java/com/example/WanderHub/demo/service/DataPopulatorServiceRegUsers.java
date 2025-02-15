
package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.RegisteredUser;  // Modifica il nome del modello
import com.example.WanderHub.demo.repository.RegisteredUserRepository;  // Modifica il nome del repository
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;  // Importa il modulo per il supporto delle date Java 8

import java.util.ArrayList;

@Service
public class DataPopulatorServiceRegUsers {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    //@PostConstruct
    public void populateData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);

            System.out.println("Inizio popolamento dei dati nella collection RegisteredUser.");

            // Carica il file JSON e mappa correttamente in una lista di oggetti RegisteredUser
            List<RegisteredUser> users = objectMapper.readValue(
                    new FileReader("C:/Users/andre/Downloads/popolamentoUsernamesFinale.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RegisteredUser.class));

            // Salva i dati nel database
            registeredUserRepository.saveAll(users);

            System.out.println("Popolamento dei dati completato nella collection RegisteredUser.");
        } catch (Exception e) {
            System.out.println("Errore durante la lettura o popolamento dei dati.");
            e.printStackTrace();  // Log dettagliato dell'errore
        }
    }
}


