package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accomodation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccomodationRepository extends MongoRepository<Accomodation, Integer> {
    // Metodi per interrogare il database, se necessari

}
