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

import javax.annotation.PostConstruct;
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

    // Move books with endDate older than 3 months from accommodations collection
    // to archivedBooks collection
    @Scheduled(cron = "0 0 3 1 */3 ?")
    //@PostConstruct
    @Transactional
    public void archiveOldBooks() {
        logger.info("Start transfer of old books");
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(3);

        try {
            List<ArchivedBook> expiredBookings = accommodationRepository.findOldBookings(oneMonthAgo);
            if (expiredBookings.isEmpty()) {
                logger.info("No old books to archive");
                return;
            }

            int batchSize = 500;
            for (int i = 0; i < expiredBookings.size(); i += batchSize) {
                int end = Math.min(i + batchSize, expiredBookings.size());
                archivedBookingRepository.saveAll(expiredBookings.subList(i, end));
                logger.info("Batch archived books: {} - {}", i, end);
            }

            removeArchivedBooks(oneMonthAgo);

            logger.info("Transfer successfully completed!");
        } catch (Exception e) {
            logger.error("Errore during the transfer of old books", e);
            throw e;
        }
    }

    public void removeArchivedBooks(LocalDate oneMonthAgo) {
        Query query = new Query();
        Update update = new Update()
                .pull("books", Query.query(Criteria.where("occupiedDates.end").lt(oneMonthAgo)))
                .pull("occupiedDates", Query.query(Criteria.where("end").lt(oneMonthAgo)));

        mongoTemplate.updateMulti(query, update, Accommodation.class);

        Update setEmptyArray = new Update().set("books", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("books").size(0)), setEmptyArray, Accommodation.class);

        Update setEmptyOccupiedDates = new Update().set("occupiedDates", new ArrayList<>());
        mongoTemplate.updateMulti(new Query(Criteria.where("occupiedDates").size(0)), setEmptyOccupiedDates, Accommodation.class);
    }
}
