package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.ArchivedReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivedReviewRepository extends MongoRepository<ArchivedReview, String> {
}
