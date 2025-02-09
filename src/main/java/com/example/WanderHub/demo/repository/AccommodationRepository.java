package com.example.WanderHub.demo.repository;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.*;
import jakarta.transaction.Transactional;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
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




    @Query(value = "{ 'city': ?0, 'maxGuestSize': { $gte: ?1 }, 'occupiedDates': { $not: { $elemMatch: { $or: [ { 'start': { $lte: ?3 }, 'end': { $gte: ?2 } } ] } } } }",
            fields = "{ '_id': 1, 'description': 1, 'type': 1, 'city': 1, 'hostUsername': 1, 'costPerNight': 1, 'averageRate': 1, 'photos': { $slice: [0, 1] } }")
    List<Accommodation> findAvailableAccommodations(String city, int minGuests, LocalDate startDate, LocalDate endDate, Pageable pageable);





    // Recupera tutte le recensioni dell'accommodation dato un accommodationId
    @Query("{ '_id': ?0 }")
    Accommodation findReviewsByAccommodationId(String description);

    @Query("{'city':  ?0}")
    List<Accommodation> findAccommodationsByCity(String city);

    @Query("{'hostUsername':  ?0}")
    List<Accommodation> findByHostUsername(String hostUsername);



    @Query(value = "{ '_id': ?2, 'books': { '$elemMatch': { 'username': ?0, 'occupiedDates.start': ?1 } } }",
            fields = "{ '_id': 1, 'description': 1, 'books.$': 1 }")
    Accommodation findPendingBookingByUsername(String username, LocalDate startDate, String accommodationId);


    @Query(value = "{'hostUsername': ?0}", fields = "{'_id': 1, 'description': 1}")
    List<Accommodation> findOwnAccommodations(String username);

    /*@Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'books.bookId': 1,'books.occupiedDates': 1,'books.username': 1,'books.email': 1,'books.birthPlace': 1,'books.address': 1,'books.addressNumber': 1,'books.birthDate': 1,'books.guestFirstNames': 1, 'books.guestLastNames': 1}")
    List<BookDTO> viewAccommodationBooks(String username,int id);*/
    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'books': 1}")
    List<BookDTO> viewAccommodationBooks(String username, String id);

    @Query(value="{'hostUsername':  ?0, '_id': ?1}",fields="{'reviews': 1}")
    List<ReviewDTO> viewAccommodationReviews(String username,int id);

    @Aggregation(pipeline = {
            // Filtro per città e per valutazione maggiore di 0
            "{ $match: { 'city': ?0, 'averageRate': { $gt: 0 } } }",
            // Converte l'oggetto facilities in un array di key-value
            "{ $project: { " +
                    "city: 1, " +  // Mantieni la città
                    "facilities: { $objectToArray: '$facilities' }, " +  // Converte l'oggetto 'facilities' in un array
                    "averageRate: 1 " +  // Mantieni il campo averageRate
                    "} }",
            // Filtro per facilitazioni con valore uguale a 1 (attivo)
            "{ $unwind: '$facilities' }",  // Scompone l'array di facilitazioni
            "{ $match: { 'facilities.v': 1 } }",  // Filtra solo le facilitazioni con valore uguale a 1
            // Raggruppa per facilità e città, calcolando la valutazione media
            "{ $group: { " +
                    "_id: { 'facility': '$facilities.k', 'city': '$city' }, " +  // Raggruppa per nome facilità (facilities.k) e città
                    "averageRating: { $avg: '$averageRate' } " +  // Calcola la valutazione media
                    "} }",
            // Proietta il risultato finale
            "{ $project: { " +
                    "facility: '$_id.facility', " +  // Estrai il nome della facilità
                    "city: '$_id.city', " +  // Estrai la città
                    "averageRating: 1, " +  // La valutazione media
                    "_id: 0 " +  // Rimuove il campo _id
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
            "{ $unwind: '$books' }",  // Scompatta l'array 'books'
            "{ $unwind: '$books.occupiedDates' }",  // Scompatta l'array 'occupiedDates'
            "{ $match: { 'books.occupiedDates.end': { $lt: ?0 } } }",  // Filtra le prenotazioni concluse
            "{ $replaceRoot: { newRoot: { $mergeObjects: [ '$books', { accommodationId: '$_id' } ] } } }",  // Combina 'books' con 'accommodationId'
            "{ $project: { " +
                    "accommodationId: 1, " +  // Aggiungi l'accommodationId che è già presente in 'books'
                    "hostUsername: '$hostUsername', " +
                    "city: '$city', " +
                    "country: '$place', " +
                    "startDate: '$books.occupiedDates.start', " +
                    "endDate: '$books.occupiedDates.end', " +
                    "nights: { $dateDiff: { startDate: '$books.occupiedDates.start', endDate: '$books.occupiedDates.end', unit: 'day' } }, " +
                    "totalCost: { $multiply: [ { $dateDiff: { startDate: '$books.occupiedDates.start', endDate: '$books.occupiedDates.end', unit: 'day' } }, '$costPerNight'] }, " +
                    "username: '$books.username', " +
                    "guestCount: { $size: { $ifNull: [ '$books.guestFirstNames', [] ] } } " +  // Se 'guestFirstNames' è mancante o null, usa un array vuoto
                    "} }"
    })
    List<ArchivedBook> findCompletedBookings(LocalDate today);


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

