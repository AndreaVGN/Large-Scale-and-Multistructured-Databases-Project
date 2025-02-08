package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.DTO.AccommodationAverageRateDTO;
import com.example.WanderHub.demo.model.ArchivedReview;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivedReviewRepository extends MongoRepository<ArchivedReview, String> {
    @Aggregation(pipeline = {
            "{ $group: { _id: '$accommodationId', averageRate: { $avg: '$rating' } } }"
    })
    List<AccommodationAverageRateDTO> calculateAverageRatesForAllAccommodations();
}
