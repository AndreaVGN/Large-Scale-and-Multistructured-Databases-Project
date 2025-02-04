package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.ArchivedReview;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.ArchivedReviewRepository;
import com.example.WanderHub.demo.repository.ReviewRepository;
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

@Service
public class ReviewTransferService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewTransferService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ArchivedReviewRepository archivedReviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AccommodationRepository accommodationRepository;

    public void removeArchivedReviews(Date oneMonthAgo) {
        Query query = new Query();

        Update update = new Update()
                // Rimuove le recensioni più vecchie di un mese
                .pull("reviews", Query.query(Criteria.where("date").lt(oneMonthAgo)));

        // Esegue la pull delle recensioni scadute
        mongoTemplate.updateMulti(query, update, Accommodation.class);

        // Assicura che "reviews" rimanga come array vuoto se è stato svuotato
        Update setEmptyArray = new Update().set("reviews", new ArrayList<>());

        mongoTemplate.updateMulti(new Query(Criteria.where("reviews").exists(false)), setEmptyArray, Accommodation.class);
    }

    @PostConstruct
    public void archiveOldReviews() {
        logger.info("Inizio archiviazione recensioni scadute...");

        LocalDate todayLocalDate = LocalDate.now();
        LocalDate oneMonthAgoLocalDate = todayLocalDate.minusMonths(1);
        Date oneMonthAgo = Date.from(oneMonthAgoLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            // 1️⃣ Estrarre le recensioni più vecchie di un mese
            List<ArchivedReview> oldReviews = accommodationRepository.findOldReviews(oneMonthAgo);
            if (oldReviews.isEmpty()) {
                logger.info("Nessuna recensione da archiviare.");
                return;
            }

            // 2️⃣ Salvare in `ArchivedReview` in batch
            int batchSize = 500;
            for (int i = 0; i < oldReviews.size(); i += batchSize) {
                int end = Math.min(i + batchSize, oldReviews.size());
                archivedReviewRepository.saveAll(oldReviews.subList(i, end));
                logger.info("Batch recensioni archiviato: {} - {}", i, end);
            }

            // 3️⃣ Rimuovere da `Accommodation`
            removeArchivedReviews(oneMonthAgo);
            logger.info("Recensioni archiviate rimosse da accommodations.");

        } catch (Exception e) {
            logger.error("Errore durante l'archiviazione delle recensioni: ", e);
        }
    }
}

