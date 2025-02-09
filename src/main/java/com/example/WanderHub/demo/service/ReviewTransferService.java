package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.ArchivedReview;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.ArchivedReviewRepository;
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

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewTransferService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewTransferService.class);

    @Autowired
    private ArchivedReviewRepository archivedReviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Scheduled(cron = "0 0 3 1 */3 ?")// Esegue ogni 3 mesi alle 3 di notte
    //@PostConstruct
    @Transactional
    public void archiveOldReviews() {
        logger.info("Inizio archiviazione recensioni scadute...");
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(3);

        try {
            // Estrarre le recensioni più vecchie di un mese
            List<ArchivedReview> oldReviews = accommodationRepository.findOldReviews(oneMonthAgo);
            if (oldReviews.isEmpty()) {
                logger.info("Nessuna recensione da archiviare.");
                return;
            }

            // Salvare le recensioni archiviate in batch
            int batchSize = 500;
            for (int i = 0; i < oldReviews.size(); i += batchSize) {
                int end = Math.min(i + batchSize, oldReviews.size());
                archivedReviewRepository.saveAll(oldReviews.subList(i, end));
                logger.info("Batch recensioni archiviato: {} - {}", i, end);
            }

            // Rimuovere le recensioni archiviate da 'Accommodation'
            removeArchivedReviews(oneMonthAgo);

            logger.info("Transazione completata con successo!");
        } catch (Exception e) {
            logger.error("Errore durante l'archiviazione delle recensioni: ", e);
            throw e; // La transazione verrà annullata automaticamente in caso di errore
        }
    }

    private void removeArchivedReviews(LocalDate oneMonthAgo) {
        Query query = new Query();
        Update update = new Update().pull("reviews", Query.query(Criteria.where("date").lt(oneMonthAgo)));

        // Esegue la rimozione delle recensioni scadute
        mongoTemplate.updateMulti(query, update, Accommodation.class);

        // Se una accommodation non ha più recensioni, assicuriamo che rimanga un array vuoto
        Update setEmptyArray = new Update().set("reviews", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("reviews").exists(false)), setEmptyArray, Accommodation.class);
    }
}
