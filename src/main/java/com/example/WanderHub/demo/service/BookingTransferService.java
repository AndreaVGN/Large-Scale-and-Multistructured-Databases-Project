/*package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.ArchivedBooking;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.ArchivedBookingRepository;
import com.example.WanderHub.demo.repository.BookRepository;
import com.example.WanderHub.demo.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingTransferService {

    private static final Logger logger = LoggerFactory.getLogger(BookingTransferService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ArchivedBookingRepository archivedBookingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;



    public void removeArchivedBookings(LocalDate today) {
        LocalDate todayDate = LocalDate.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Query query = new Query();
        Update update = new Update().pull("books", Query.query(Criteria.where("occupiedDates.end").lt(todayDate)));

        mongoTemplate.updateMulti(query, update, Accommodation.class);
    }

    // Esegui ogni notte alle 03:00 con Batch Processing
   //@Scheduled(cron = "0 0 3 * * ?")

   @Scheduled(cron = "0 15 15 * * ?")

   public void archiveOldBookings() {
       System.out.println("Partititiiiiii");
        LocalDate today = LocalDate.now();
        try {
            // 1️⃣ Estrarre le prenotazioni concluse
            List<ArchivedBooking> completedBookings = bookRepository.findCompletedBookings(today);
            if (completedBookings.isEmpty()) {
                logger.info("Nessuna prenotazione da archiviare.");
                return;
            }

            // 3️⃣ Salvare in `ArchivedBookings` usando BATCH
            int batchSize = 500; // Numero di documenti per batch
            for (int i = 0; i < completedBookings.size(); i += batchSize) {
                int end = Math.min(i + batchSize, completedBookings.size());
                archivedBookingRepository.saveAll(completedBookings.subList(i, end));
                logger.info("Batch archiviato: {} - {}", i, end);
            }

            // 4️⃣ Rimuovere da accommodations con Batch
            removeArchivedBookings(today);
            logger.info("Prenotazioni archiviate rimosse da accommodations.");

        } catch (Exception e) {
            logger.error("Errore durante l'archiviazione delle prenotazioni: ", e);
        }
    }
}*/

package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.ArchivedBooking;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.ArchivedBookingRepository;
import com.example.WanderHub.demo.repository.BookRepository;
import com.example.WanderHub.demo.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingTransferService {

    private static final Logger logger = LoggerFactory.getLogger(BookingTransferService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ArchivedBookingRepository archivedBookingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccommodationRepository accommodationRepository;

    public void removeArchivedBookings(Date today) {
        Query query = new Query();

        Update update = new Update()
                // Rimuove interi "books" se il loro occupiedDates.end è passato
                .pull("books", Query.query(Criteria.where("occupiedDates.end").lt(today)))

                // Rimuove solo le date specifiche all'interno di "occupiedDates"
                .pull("occupiedDates", Query.query(Criteria.where("end").lt(today)));

        // Esegue la pull delle date scadute
        mongoTemplate.updateMulti(query, update, Accommodation.class);

        // Assicura che "books" e "occupiedDates" rimangano come array vuoti se sono stati svuotati
        Update setEmptyArrays = new Update()
                .set("books", new ArrayList<>()) // Imposta un array vuoto se necessario
                .set("occupiedDates", new ArrayList<>()); // Imposta un array vuoto se necessario

        mongoTemplate.updateMulti(new Query(Criteria.where("books").exists(false)), setEmptyArrays, Accommodation.class);
        mongoTemplate.updateMulti(new Query(Criteria.where("occupiedDates").exists(false)), setEmptyArrays, Accommodation.class);
    }



    // Metodo eseguito subito dopo l'inizializzazione del bean
    @PostConstruct
    public void archiveOldBookings() {
        System.out.println("Partititiiiiii");
        LocalDate todayLocalDate = LocalDate.now();
        Date today = Date.from(todayLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        try {
            // 1️⃣ Estrarre le prenotazioni concluse
            List<ArchivedBooking> completedBookings = accommodationRepository.findCompletedBookings(today);
            System.out.println(completedBookings);
            if (completedBookings.isEmpty()) {
                logger.info("Nessuna prenotazione da archiviare.");
                return;
            }

            // 3️⃣ Salvare in `ArchivedBookings` usando BATCH
            int batchSize = 500; // Numero di documenti per batch
            for (int i = 0; i < completedBookings.size(); i += batchSize) {
                int end = Math.min(i + batchSize, completedBookings.size());
                archivedBookingRepository.saveAll(completedBookings.subList(i, end));
                logger.info("Batch archiviato: {} - {}", i, end);
            }

            // 4️⃣ Rimuovere da accommodations con Batch
            removeArchivedBookings(today);
            logger.info("Prenotazioni archiviate rimosse da accommodations.");

        } catch (Exception e) {
            logger.error("Errore durante l'archiviazione delle prenotazioni: ", e);
        }
    }
}


