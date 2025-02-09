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

    // Move reviews with date older than 3 months from accommodations collection
    // to archivedReviews collection
    @Scheduled(cron = "0 0 3 1 */3 ?")
    //@PostConstruct
    @Transactional
    public void archiveOldReviews() {
        logger.info("Start archiving old reviews");
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(3);

        try {
            List<ArchivedReview> oldReviews = accommodationRepository.findOldReviews(oneMonthAgo);
            if (oldReviews.isEmpty()) {
                logger.info("No old review to archive.");
                return;
            }

            int batchSize = 500;
            for (int i = 0; i < oldReviews.size(); i += batchSize) {
                int end = Math.min(i + batchSize, oldReviews.size());
                archivedReviewRepository.saveAll(oldReviews.subList(i, end));
                logger.info("Batch archived review: {} - {}", i, end);
            }

            removeArchivedReviews(oneMonthAgo);

            logger.info("Transaction successful!");
        } catch (Exception e) {
            logger.error("Error during transfer of old reviews ", e);
            throw e;
        }
    }

    private void removeArchivedReviews(LocalDate oneMonthAgo) {
        Query query = new Query();
        Update update = new Update().pull("reviews", Query.query(Criteria.where("date").lt(oneMonthAgo)));

        mongoTemplate.updateMulti(query, update, Accommodation.class);

        Update setEmptyArray = new Update().set("reviews", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("reviews").exists(false)), setEmptyArray, Accommodation.class);
    }
}
