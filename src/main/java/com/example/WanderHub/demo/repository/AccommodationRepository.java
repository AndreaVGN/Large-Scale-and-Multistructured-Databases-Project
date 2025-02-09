package com.example.WanderHub.demo.repository;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.*;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Aggregation;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface AccommodationRepository extends MongoRepository<Accommodation, Integer> {
    @Query("{ '_id': ?0 }")
    Optional<Accommodation> findByAccommodationId(ObjectId _id);

    @Query(value = "{ 'city': ?0, 'maxGuestSize': { $gte: ?1 }, 'occupiedDates': { $not: { $elemMatch: { $or: [ { 'start': { $lte: ?3 }, 'end': { $gte: ?2 } } ] } } } }",
            fields = "{ '_id': 1, 'description': 1, 'type': 1, 'city': 1, 'hostUsername': 1, 'costPerNight': 1, 'averageRate': 1, 'photos': { $slice: [0, 1] } }")
    List<Accommodation> findAvailableAccommodations(String city, int minGuests, LocalDate startDate, LocalDate endDate, Pageable pageable);



    @Query(value = "{ '_id': ?2, 'books': { '$elemMatch': { 'username': ?0, 'occupiedDates.start': ?1 } } }",
            fields = "{ '_id': 1, 'description': 1, 'books.$': 1 }")
    Accommodation findPendingBookingByUsername(String username, LocalDate startDate, String accommodationId);

    @Query(value = "{'hostUsername': ?0}", fields = "{'_id': 1, 'description': 1}")
    List<Accommodation> findOwnAccommodations(String username);

    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'books': 1}")
    List<BookDTO> viewAccommodationBooks(String username, String id);

    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'reviews': 1}")
    List<ReviewDTO> viewAccommodationReviews(String username,int id);

    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0, 'averageRate': { $gt: 0 } } }",
            "{ $project: { " +
                    "city: 1, " +
                    "facilities: { $objectToArray: '$facilities' }, " +
                    "averageRate: 1 " +
                    "} }",

            "{ $unwind: '$facilities' }",
            "{ $match: { 'facilities.v': 1 } }",
            "{ $group: { " +
                    "_id: { 'facility': '$facilities.k', 'city': '$city' }, " +
                    "averageRating: { $avg: '$averageRate' } " +
                    "} }",

            "{ $project: { " +
                    "facility: '$_id.facility', " +
                    "city: '$_id.city', " +
                    "averageRating: 1, " +
                    "_id: 0 " +
                    "} }"
    })
    List<FacilityRatingDTO> getAverageRatingByFacilityInCity(String city);

    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0 } }",
            "{ $group: { _id: '$maxGuestSize', averageCostPerNight: { $avg: '$costPerNight' }, city: { $first: '$city' } } }",
            "{ $project: { _id: 0, guestCount: '$_id', averageCostPerNight: 1, city: 1 } }"
    })
    List<AverageCostDTO> findAverageCostPerNightByCityAndGuests(String city);

    @Query(value = "{ '_id': ?0, 'books.occupiedDates': { $elemMatch: { 'start': { $lte: ?2 }, 'end': { $gte: ?1 } } } }", count = true)
    int checkAvailability(ObjectId accommodationId, LocalDate startDate, LocalDate endDate);




    @Aggregation(pipeline = {
        "{ $unwind: '$reviews' }",
        "{ $match: { 'reviews.date': { $lt: ?0 } } }",
        "{ $replaceRoot: { newRoot: { $mergeObjects: [ '$reviews', { accommodationId: '$_id' } ] } } }"
    })
    List<ArchivedReview> findOldReviews(LocalDate oneMonthAgo);


    @Aggregation(pipeline = {
            "{ $unwind: '$books' }",
            "{ $match: { 'books.occupiedDates.end': { $lt: ?0 } } }",
            "{ $replaceRoot: { newRoot: { $mergeObjects: [ '$books', { accommodationId: '$_id' }, {city: '$city'}, {hostUsername: '$hostUsername'}, {costPerNight:  '$costPerNight'} ] } } }"
    })

    List<ArchivedBook> findOldBookings(LocalDate oneMonthAgo);


    @Query(value = "{ '_id': ?0, 'books.username': ?1, 'books.occupiedDates.end': { $gte: ?2, $lte: ?3 } }", exists = true)
    boolean existsBookingForUser(ObjectId accommodationId, String username, LocalDate maxEndDate, LocalDate today);



}

