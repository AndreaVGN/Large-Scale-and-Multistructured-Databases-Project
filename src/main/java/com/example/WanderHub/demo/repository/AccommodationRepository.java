package com.example.WanderHub.demo.repository;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.model.Accommodation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Aggregation;

import java.util.Optional;
import java.util.List;

@Repository
public interface AccommodationRepository extends MongoRepository<Accommodation, Integer> {
    // Trova una sistemazione per ID
    @Query("{ 'accommodationId': ?0 }")
    Optional<Accommodation> findByAccommodationId(int _id);

    boolean existsByAccommodationId(int accommodationId);
    void deleteByAccommodationId(int accommodationId);

    // Ricerca sistemazioni disponibili in base a parametri
    @Query("{ 'city': ?0, 'maxGuestSize': { $gte: ?1 }, 'occupiedDates': { $not: { $elemMatch: { $or: [ { 'startDate': { $lte: ?3 }, 'endDate': { $gte: ?2 } } ] } } } }")
    List<Accommodation> findAvailableAccommodations(String city, int minGuests, String startDate, String endDate);

    // Recupera tutte le recensioni dell'accommodation dato un accommodationId
    @Query("{ 'accommodationId': ?0 }")
    Accommodation findReviewsByAccommodationId(int accommodationId);

    @Query("{'city':  ?0}")
    List<Accommodation> findAccommodationsByCity(String city);

    @Query("{'hostUsername':  ?0}")
    List<Accommodation> findByHostUsername(String hostUsername);

    @Aggregation(pipeline = {
            "{ $match: { 'reviews.username': ?0 } }",
            "{ $project: { _id: 0, accommodationId: 0, type:0,description:0,facilities:0,place:0,city:0,address:0,hostUsername:0,latidude:0,longitude:0,occupiedDates:0,maxGuestsSize:0,costPerNight:0,averageRate:0,photos:0,books:0, reviews: { $filter: { input: '$reviews', as: 'review', cond: { $eq: ['$$review.username', ?0] } } } } }"
    })
    List<Review> findReviewsByUsername(String username);

    @Aggregation(pipeline = {
            "{ $project: { _id:0, accommodationId:0, description:0, facilities:0,place:0,city:0,address:0,hostUsername:0,latitude:0,longitude:0,occupiedDates:0,maxGuestsSize:0,costPerNight:0,averageRate:0,photos:0,reviews:0,books:{$filter: { input:'$books', as: 'book', cond: { $gt: [ { $toDate: { $arrayElemAt: ['$$book.occupiedDates.start', 0] } },new Date() ] } } } } }{ $match: { 'books': { $ne: [] } } }"

    })
    List<Book> findPendingBookingsByUsername(String username);

}

