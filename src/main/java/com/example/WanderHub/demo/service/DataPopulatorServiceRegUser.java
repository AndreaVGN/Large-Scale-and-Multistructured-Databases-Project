/*
package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.RegisteredUser;  // Modifica il nome del modello
import com.example.WanderHub.demo.repository.RegisteredUserRepository;  // Modifica il nome del repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;  // Importa il modulo per il supporto delle date Java 8

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

@Service
public class DataPopulatorServiceRegUser {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @PostConstruct
    public void populateData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);

            System.out.println("Inizio popolamento dei dati nella collection RegisteredUser.");

            // Carica il file JSON e mappa correttamente in una lista di oggetti RegisteredUser
            List<RegisteredUser> users = objectMapper.readValue(
                    new FileReader("C:/Users/andre/Downloads/popolamentoUsernamesFinale_modificato.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RegisteredUser.class));

            System.out.println("Totale utenti letti dal file: " + users.size());

            // Definisci il batch size (es. 5000 documenti per ogni batch)
            int batchSize = 5000;
            List<RegisteredUser> batch = new ArrayList<>();

            for (int i = 0; i < users.size(); i++) {
                try {
                    RegisteredUser user = users.get(i);
                    batch.add(user);  // Aggiungi l'utente al batch

                    // Quando raggiungi la dimensione del batch, salvi i dati
                    if (batch.size() >= batchSize || i == users.size() - 1) {
                        registeredUserRepository.saveAll(batch);  // Salva il batch nel DB
                        long count = registeredUserRepository.count();
                        System.out.println("Documenti nel DB dopo il batch: " + count);
                        batch.clear();  // Pulisce il batch
                        System.out.println("Batch salvato: " + (i + 1) + " / " + users.size());
                    }
                } catch (Exception e) {
                    // Logga e ignora i documenti malformattati
                    System.out.println("Errore durante la deserializzazione del documento " + (i + 1));
                    e.printStackTrace();  // Mostra dettagli dell'errore
                }
            }

            System.out.println("Popolamento dei dati completato nella collection RegisteredUser.");
        } catch (Exception e) {
            System.out.println("Errore durante la lettura o popolamento dei dati.");
            e.printStackTrace();  // Log dettagliato dell'errore
        }
    }
}

*/
