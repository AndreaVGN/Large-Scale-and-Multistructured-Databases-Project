package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accomodation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccomodationRepository extends MongoRepository<Accomodation, Integer> {
    // Metodi per interrogare il database, se necessari
  @Query("{'accomodationId' : ?0}")
  Optional<Accomodation> findByAccomodationId(int accomodationId);

  boolean existsByAccomodationId(int accomodationId);
  void deleteByAccomodationId(int accomodationId);
}
