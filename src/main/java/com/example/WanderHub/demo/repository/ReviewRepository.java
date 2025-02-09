package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends MongoRepository<Review, Integer> {
}

