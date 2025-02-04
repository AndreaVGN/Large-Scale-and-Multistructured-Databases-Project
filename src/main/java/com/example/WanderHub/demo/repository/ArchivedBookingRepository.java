package com.example.WanderHub.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArchivedBookingRepository extends MongoRepository<ArchivedBooking, String> {

    // 1️⃣ Query per ottenere le prenotazioni concluse
    @Query(value = "{ 'books.endDate': { $lt: ?0 } }",
            fields = "{ 'accommodationId': 1, 'hostUsername': 1, 'city': 1, 'country': 1, 'books.startDate': 1, 'books.endDate': 1, 'books.username': 1, 'books.guestFirstNames': 1, 'costPerNight': 1 }")
    List<ArchivedBookingProjection> findCompletedBookings(LocalDate today);

    // 2️⃣ Query per rimuovere le prenotazioni archiviate da accommodations
    @Query(value = "{ }", update = "{ $pull: { 'books': { 'endDate': { $lt: ?0 } } } }")
    void removeArchivedBookings(LocalDate today);
}
