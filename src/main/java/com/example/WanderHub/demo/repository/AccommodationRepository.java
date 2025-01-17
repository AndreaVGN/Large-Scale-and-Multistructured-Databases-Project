package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accommodation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AccommodationRepository extends MongoRepository<Accommodation, Integer> {
    // Trova una sistemazione per ID
    @Query("{ 'accommodationId': ?0 }")
    Optional<Accommodation> findByAccommodationId(int accommodationId);

    boolean existsByAccommodationId(int accommodationId);
    void deleteByAccommodationId(int accommodationId);

    // Ricerca sistemazioni disponibili in base a parametri
    @Query("{ 'place': ?0, 'maxGuestSize': { $gte: ?1 }, 'occupiedDates': { $not: { $elemMatch: { $or: [ { 'startDate': { $lte: ?3 }, 'endDate': { $gte: ?2 } } ] } } } }")
    List<Accommodation> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate);
}

