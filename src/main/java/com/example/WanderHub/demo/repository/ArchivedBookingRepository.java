package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.ArchivedBooking;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ArchivedBookingRepository extends MongoRepository<ArchivedBooking, String> {
    // Metodo per salvare in batch (viene già gestito da `saveAll()`)


}
