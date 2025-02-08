package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AccommodationAverageRateDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.repository.ArchivedReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AverageRateEvaluationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ArchivedReviewRepository archivedReviewRepository;

    @Scheduled(cron = "0 0 3 * * ?") // Ogni giorno alle 03:00 AM
    //@PostConstruct
    public void updateAverageRates() {
        try {
            List<AccommodationAverageRateDTO> averages = archivedReviewRepository.calculateAverageRatesForAllAccommodations();

            if (averages == null || averages.isEmpty()) {
                throw new Exception("Problema nella query: nessun dato restituito per i tassi medi.");
            }

            for (AccommodationAverageRateDTO avg : averages) {
                try {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("accommodationId").is(avg.get_id()));

                    Update update = new Update().set("averageRate", avg.getAverageRate());

                    mongoTemplate.updateFirst(query, update, Accommodation.class);
                } catch (Exception e) {
                    System.err.println("Errore nell'aggiornamento della casa con ID " + avg.get_id() + ": " + e.getMessage());
                }
            }

            System.out.println("Aggiornamento completato.");

        } catch (Exception e) {
            System.err.println("Errore nell'aggiornamento dei tassi medi: " + e.getMessage());
        }
    }
}
