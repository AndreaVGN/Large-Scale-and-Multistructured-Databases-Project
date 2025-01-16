package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accommodation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccommodationRepository extends MongoRepository<Accommodation, Integer> {
    // Metodi per interrogare il database, se necessari
    @Query("{ 'accommodationId': ?0 }")
    Optional<Accommodation> findByAccommodationId(int accommodationId);

    boolean existsByAccommodationId(int accommodationId);
    void deleteByAccommodationId(int accommodationId);


}
