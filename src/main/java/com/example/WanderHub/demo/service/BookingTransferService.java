/*package com.example.WanderHub.demo.service;

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

    // Idea: Un utente registrato ha tempo 3 giorni dopo la end date per scrivere la review,
    // Una volta a settimana avviene il batch che sposta tutte le book fino a TODAY - 3 giorni nel passato

    // Metodo eseguito subito dopo l'inizializzazione del bean
    @PostConstruct
    public void archiveOldBookings() {
        System.out.println("Partititiiiiii");
        LocalDate todayLocalDate = LocalDate.now();
        //Date today = Date.from(todayLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate today = LocalDate.now();


        try {
            // 1️⃣ Estrarre le prenotazioni concluse
            List<ArchivedBooking> completedBookings = accommodationRepository.findCompletedBookings(today);
            System.out.println(completedBookings);
            if (completedBookings.isEmpty()) {
                logger.info("Nessuna prenotazione da archiviare.");
                return;
            }


            for (ArchivedBooking booking : completedBookings) {
                logger.info("AccommodationId: {}", booking.getAccommodationId());
                logger.info("Host Username: {}", booking.getHostUsername());
                logger.info("City: {}", booking.getCity());
                logger.info("Country: {}", booking.getCountry());
                logger.info("Start Date: {}", booking.getStartDate());
                logger.info("End Date: {}", booking.getEndDate());
                logger.info("Nights: {}", booking.getNights());
                logger.info("Total Cost: {}", booking.getTotalCost());
                logger.info("Username: {}", booking.getUsername());
                logger.info("Guest Count: {}", booking.getGuestCount());
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookingTransferService {

    private static final Logger logger = LoggerFactory.getLogger(BookingTransferService.class);

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private ArchivedBookingRepository archivedBookingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void removeArchivedBookings(LocalDate thresholdDate) {

        // Query per rimuovere i booking dalla collection che sono precedenti alla thresholdDate
        Query query = new Query();

        // Aggiornamento per rimuovere i booking che hanno una data di fine (in occupiedDates) prima della thresholdDate
        Update update = new Update()
                // Rimuove i booking con una data di fine in "books.occupiedDates" precedente alla threshold
                .pull("books", Query.query(Criteria.where("occupiedDates.end").lt(thresholdDate)))
                // Rimuove le date di fine precedenti alla threshold dalla proprietà "occupiedDates"
                .pull("occupiedDates", Query.query(Criteria.where("end").lt(thresholdDate)));

        // Applichiamo l'update a tutti gli accommodation
        mongoTemplate.updateMulti(query, update, Accommodation.class);

        // Svuotiamo i campi "books" se sono vuoti
        Update setEmptyArray = new Update().set("books", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("books").size(0)), setEmptyArray, Accommodation.class);

        // Svuotiamo i campi "occupiedDates" se sono vuoti
        Update setEmptyOccupiedDates = new Update().set("occupiedDates", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("occupiedDates").size(0)), setEmptyOccupiedDates, Accommodation.class);
    }

    @PostConstruct
    public void archiveOldBookings() {
        logger.info("Inizio archiviazione prenotazioni scadute...");
        LocalDate oneMonthAgo = LocalDate.now().plusMonths(10);

        try {
            List<ArchivedBooking> expiredBookings = accommodationRepository.findOldBookings(oneMonthAgo);
            if (expiredBookings.isEmpty()) {
                logger.info("Nessuna prenotazione da archiviare.");
                return;
            }

            for (ArchivedBooking booking : expiredBookings) {
                logger.info("Archiving Booking - AccommodationId: {}, Username: {}, EndDate: {}",
                        booking.getAccommodationId(), booking.getUsername(), booking.getEndDate());
            }

            int batchSize = 500;
            for (int i = 0; i < expiredBookings.size(); i += batchSize) {
                int end = Math.min(i + batchSize, expiredBookings.size());
                archivedBookingRepository.saveAll(expiredBookings.subList(i, end));
                logger.info("Batch prenotazioni archiviato: {} - {}", i, end);
            }

            removeArchivedBookings(oneMonthAgo);
            logger.info("Prenotazioni archiviate rimosse da accommodations.");
        } catch (Exception e) {
            logger.error("Errore durante l'archiviazione delle prenotazioni: ", e);
        }
    }
}


