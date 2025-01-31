package com.example.WanderHub.demo.repository;
import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.BookDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
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
    @Query("{ '_id': ?0 }")
    Optional<Accommodation> findByAccommodationId(int _id);

    boolean existsByAccommodationId(int accommodationId);
    void deleteByAccommodationId(int accommodationId);

    // Ricerca sistemazioni disponibili in base a parametri
   /* @Query(value = "{ 'city': ?0, 'maxGuestSize': { $gte: ?1 }, 'occupiedDates': { $not: { $elemMatch: { $or: [ { 'start': { $lte: ?3 }, 'end': { $gte: ?2 } } ] } } } }", fields = "{ 'books': 0, 'reviews': 0, '_id': 0, 'latitude': 0, 'longitude': 0, 'maxGuestSize': 0, 'occupiedDates':0, 'address': 0,'place': 0, 'facilities': 0,'photos': 0  }")

    List<Accommodation> findAvailableAccommodations(String city, int minGuests, String startDate, String endDate);*/

    @Query(value = "{ 'city': ?0, 'maxGuestSize': { $gte: ?1 }, 'occupiedDates': { $not: { $elemMatch: { $or: [ { 'start': { $lte: ?3 }, 'end': { $gte: ?2 } } ] } } } }",
            fields = "{ 'description': 1, 'type': 1, 'city': 1, 'hostUsername': 1, 'costPerNight': 1, 'averageRate': 1, 'photos': { $slice: [0, 1] }, '_id': 0 }")
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
            "{ $project: { 'reviews': { $filter: { input: '$reviews', as: 'review', cond: { $eq: ['$$review.username', ?0] } } } } }"
    })
    List<ReviewDTO> findReviewsByUsername(String username);


    @Aggregation(pipeline = {
            "{ $match: { 'books': { $elemMatch: { 'username': ?0, 'occupiedDates.start': { $exists: true, $not: { $size: 0 }, $gt: new Date() } } } } }",
            "{ $project: { books: { $filter: { input: '$books', as: 'book', cond: { $and: [ " +
                    "{ $eq: ['$$book.username', ?0] }, " +
                    "{ $gt: [ { $toDate: { $arrayElemAt: ['$$book.occupiedDates.start', 0] } }, new Date() ] } " +
                    "] } } } } }"
    })
    List<BookDTO> findPendingBookingsByUsername(String username);

    @Query(value = "{'hostUsername': ?0}", fields = "{'_id': 1, 'description': 1}")
    List<Accommodation> findOwnAccommodations(String username);

    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'books': 1}")
    List<BookDTO> viewAccommodationBooks(String username,int id);

    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'reviews': 1}")
    List<ReviewDTO> viewAccommodationReviews(String username,int id);


}

