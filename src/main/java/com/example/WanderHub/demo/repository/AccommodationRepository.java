package com.example.WanderHub.demo.repository;
import com.example.WanderHub.demo.DTO.AccommodationDTO;
import com.example.WanderHub.demo.DTO.AverageCostDTO;
import com.example.WanderHub.demo.DTO.BookDTO;
import com.example.WanderHub.demo.DTO.FacilityRatingDTO;
import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Aggregation;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.List;

@Repository
public interface AccommodationRepository extends MongoRepository<Accommodation, Integer> {
    // Trova una sistemazione per ID
    @Query("{ '_id': ?0 }")
    Optional<Accommodation> findByAccommodationId(ObjectId _id);

    boolean existsByDescription(ObjectId accommodationId);
    void deleteByDescription(ObjectId accommodationId);


    @Query(value = "{ 'city': ?0, 'maxGuestSize': { $gte: ?1 }, 'occupiedDates': { $not: { $elemMatch: { $or: [ { 'start': { $lte: ?3 }, 'end': { $gte: ?2 } } ] } } } }",
            fields = "{ '_id': 1, 'description': 1, 'type': 1, 'city': 1, 'hostUsername': 1, 'costPerNight': 1, 'averageRate': 1, 'photos': { $slice: [0, 1] } }")
    List<Accommodation> findAvailableAccommodations(String city, int minGuests, LocalDate startDate, LocalDate endDate);


    // Recupera tutte le recensioni dell'accommodation dato un accommodationId
    @Query("{ '_id': ?0 }")
    Accommodation findReviewsByAccommodationId(String description);

    @Query("{'city':  ?0}")
    List<Accommodation> findAccommodationsByCity(String city);

    @Query("{'hostUsername':  ?0}")
    List<Accommodation> findByHostUsername(String hostUsername);

    @Aggregation(pipeline = {
            "{ $match: { 'reviews.username': ?0 } }",
            "{ $project: { " +
                    "'accommodationId': 1, " +   // Includi accommodationId
                    "'description': 1, " +       // Includi description
                    "'reviews': { $filter: { " +
                    "input: '$reviews', " +
                    "as: 'review', " +
                    "cond: { $eq: ['$$review.username', ?0] } " +
                    "} } " +
                    "} }"
    })
    List<Accommodation> findReviewsByUsername(String username);



    @Aggregation(pipeline = {
            "{ $match: { 'books': { $elemMatch: { 'username': ?0, 'occupiedDates.start': { $exists: true, $not: { $size: 0 }, $gt: new Date() } } } } }",
            "{ $project: { " +
                    "'accommodationId': 1, " +  // Includi accommodationId
                    "'description': 1, " +      // Includi description
                    "'books': { $filter: { " +
                    "input: '$books', " +
                    "as: 'book', " +
                    "cond: { $and: [ " +
                    "{ $eq: ['$$book.username', ?0] }, " +
                    "{ $gt: [ { $toDate: { $arrayElemAt: ['$$book.occupiedDates.start', 0] } }, new Date() ] } " +
                    "] } " +
                    "} } " +
                    "} }"
    })
    List<Accommodation> findPendingBookingsByUsername(String username);

    @Query(value = "{'hostUsername': ?0}", fields = "{'_id': 1, 'description': 1}")
    List<Accommodation> findOwnAccommodations(String username);

    /*@Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'books.bookId': 1,'books.occupiedDates': 1,'books.username': 1,'books.email': 1,'books.birthPlace': 1,'books.address': 1,'books.addressNumber': 1,'books.birthDate': 1,'books.guestFirstNames': 1, 'books.guestLastNames': 1}")
    List<BookDTO> viewAccommodationBooks(String username,int id);*/
    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'books': 1}")
    List<BookDTO> viewAccommodationBooks(String username, int id);

    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'reviews': 1}")
    List<ReviewDTO> viewAccommodationReviews(String username,int id);

    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0, 'averageRate': { $gt: 0 } } }", // Filtra per città
            "{ $unwind: '$facilities' }",  // Scompone l'array facilities
            "{ $group: { " +
                    "_id: { 'facility': '$facilities', 'city': '$city' }, " + // Raggruppa per facility e città
                    "averageRating: { $avg: '$averageRate' } " + // Calcola il rating medio
                    "} }",
            "{ $project: { " +
                    "'facility': '$_id.facility', " +
                    "'city': '$_id.city', " +
                    "'averageRating': 1, " +
                    "_id: 0 " + // Rimuove il campo _id
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
    //@Query(value = "{ 'accommodationId': ?0, 'occupiedDates': { $elemMatch: { 'start': { $lte: ?2 }, 'end': { $gte: ?1 } } } }", count = true)
    int checkAvailability(ObjectId accommodationId, LocalDate startDate, LocalDate endDate);

    /*default boolean checkAvailability(String description, LocalDate startDate, LocalDate endDate) {
        long count =  countOverlappingReservations(accommodationId, startDate, endDate);
    }*/

    @Aggregation(pipeline = {
            "{ $unwind: '$books' }", // Scompatta l'array di books
            "{ $unwind: '$books.occupiedDates' }", // Scompatta l'array occupiedDates
            "{ $match: { 'books.occupiedDates.end': { $lt: ?0 } } }", // Filtra le prenotazioni concluse
            "{ $project: { " +
                    "accommodationId: '$_id', " +
                    "hostUsername: '$hostUsername', " +
                    "city: '$city', " +
                    "country: '$place', " +
                    "startDate: '$books.occupiedDates.start', " +
                    "endDate: '$books.occupiedDates.end', " +
                    "nights: { $dateDiff: { startDate: '$books.occupiedDates.start', endDate: '$books.occupiedDates.end', unit: 'day' } }, " +
                    "totalCost: { $multiply: [{ $dateDiff: { startDate: '$books.occupiedDates.start', endDate: '$books.occupiedDates.end', unit: 'day' } }, '$costPerNight'] }, " +
                    "username: '$books.username', " +
                    "guestCount: { $size: '$books.guestFirstNames' } " +
                    "} }"
    })
    List<ArchivedBooking> findCompletedBookings(Date today);

    @Aggregation(pipeline = {
            "{ $unwind: '$reviews' }", // Scompatta l'array di recensioni
            "{ $match: { 'reviews.date': { $lt: ?0 } } }", // Filtra le recensioni più vecchie di un mese
            "{ $project: { " +
                    "reviewId: '$reviews.reviewId', " +
                    "accommodationId: '$_id', " +  // Salva anche l'ID dell'alloggio
                    "username: '$reviews.username', " +
                    "rate: '$reviews.rate', " +
                    "reviewText: '$reviews.reviewText', " +
                    "date: '$reviews.date' " +
                    "} }"
    })
    List<ArchivedReview> findOldReviews(Date oneMonthAgo);


    @Query(value = "{ '_id': ?0, 'books.username': ?1, 'books.occupiedDates.end': { $gte: ?2, $lte: ?3 } }", exists = true)
    boolean existsBookingForUser(ObjectId accommodationId, String username, LocalDate maxEndDate, LocalDate today);

}

