package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingTransferService {

    private static final Logger logger = LoggerFactory.getLogger(BookingTransferService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ArchivedBookingRepository archivedBookingRepository;

    // Esegui ogni notte alle 03:00 con Batch Processing
    @Scheduled(cron = "0 0 3 * * ?")
    public void archiveOldBookings() {
        LocalDate today = LocalDate.now();
        try {
            // 1️⃣ Estrarre le prenotazioni concluse
            List<ArchivedBookingProjection> completedBookings = bookingRepository.findCompletedBookings(today);
            if (completedBookings.isEmpty()) {
                logger.info("Nessuna prenotazione da archiviare.");
                return;
            }

            // 2️⃣ Convertire in `ArchivedBooking`
            List<ArchivedBooking> archivedBookings = completedBookings.stream()
                    .map(b -> new ArchivedBooking(
                            b.getAccommodationId(),
                            b.getHostUsername(),
                            b.getCity(),
                            b.getCountry(),
                            b.getStartDate(),
                            b.getEndDate(),
                            b.getEndDate().compareTo(b.getStartDate()), // Calcola le notti
                            b.getCostPerNight() * b.getEndDate().compareTo(b.getStartDate()), // Costo totale
                            b.getUsername(),
                            b.getBooks_GuestFirstNames().length // Numero di ospiti
                    ))
                    .collect(Collectors.toList());

            // 3️⃣ Salvare in `ArchivedBookings` usando BATCH
            int batchSize = 500; // Numero di documenti per batch
            for (int i = 0; i < archivedBookings.size(); i += batchSize) {
                int end = Math.min(i + batchSize, archivedBookings.size());
                archivedBookingRepository.saveAll(archivedBookings.subList(i, end));
                logger.info("Batch archiviato: {} - {}", i, end);
            }

            // 4️⃣ Rimuovere da accommodations con Batch
            bookingRepository.removeArchivedBookings(today);
            logger.info("Prenotazioni archiviate rimosse da accommodations.");

        } catch (Exception e) {
            logger.error("Errore durante l'archiviazione delle prenotazioni: ", e);
        }
    }
}

