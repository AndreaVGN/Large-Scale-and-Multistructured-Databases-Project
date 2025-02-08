package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.ArchivedBook;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.ArchivedBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArchivedBookTransferService {

    private static final Logger logger = LoggerFactory.getLogger(ArchivedBookTransferService.class);

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private ArchivedBookingRepository archivedBookingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Scheduled(cron = "0 0 3 1 */3 ?") // Esegui ogni 3 mesi alle 3 di notte
    public void archiveOldBooks() {
        logger.info("Inizio archiviazione prenotazioni scadute...");
        LocalDate oneMonthAgo = LocalDate.now().plusMonths(10);

        try {
            // 1️⃣ Estrai le prenotazioni scadute
            List<ArchivedBook> expiredBookings = accommodationRepository.findOldBookings(oneMonthAgo);
            if (expiredBookings.isEmpty()) {
                logger.info("Nessuna prenotazione da archiviare.");
                return;
            }

            // 2️⃣ Salvataggio batch delle prenotazioni archiviate
            int batchSize = 500;
            for (int i = 0; i < expiredBookings.size(); i += batchSize) {
                int end = Math.min(i + batchSize, expiredBookings.size());
                archivedBookingRepository.saveAll(expiredBookings.subList(i, end));
                logger.info("Batch prenotazioni archiviato: {} - {}", i, end);
            }

            // Procediamo alla rimozione delle prenotazioni archiviate
            removeArchivedBooks(oneMonthAgo);

            logger.info("Archiviazione completata con successo!");
        } catch (Exception e) {
            logger.error("Errore durante l'archiviazione delle prenotazioni: ", e);
            throw e; // La transazione verrà annullata automaticamente in caso di errore
        }
    }

    @Transactional
    public void removeArchivedBooks(LocalDate oneMonthAgo) {
        // 3️⃣ Rimuovere le prenotazioni archiviate da 'Accommodation'
        Query query = new Query();
        Update update = new Update()
                .pull("books", Query.query(Criteria.where("occupiedDates.end").lt(oneMonthAgo)))
                .pull("occupiedDates", Query.query(Criteria.where("end").lt(oneMonthAgo)));

        mongoTemplate.updateMulti(query, update, Accommodation.class);

        // 4️⃣ Svuotare i campi "books" se sono vuoti
        Update setEmptyArray = new Update().set("books", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("books").size(0)), setEmptyArray, Accommodation.class);

        // 5️⃣ Svuotare i campi "occupiedDates" se sono vuoti
        Update setEmptyOccupiedDates = new Update().set("occupiedDates", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("occupiedDates").size(0)), setEmptyOccupiedDates, Accommodation.class);
    }
}
