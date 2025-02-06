package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.DTO.CityBookingRankingDTO;
import com.example.WanderHub.demo.model.ArchivedBooking;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ArchivedBookingRepository extends MongoRepository<ArchivedBooking, String> {
    // Metodo per salvare in batch (viene già gestito da `saveAll()`)

    @Aggregation(pipeline = {
            // Filtro per le prenotazioni negli ultimi 12 mesi
            "{ $match: { 'occupiedDates.end': { $gte: ?0 } } }",
            // Decomponiamo l'array di occupiedDates
            "{ $unwind: '$occupiedDates' }",
            // Raggruppiamo per città e contiamo il numero di prenotazioni per ogni città
            "{ $group: { '_id': '$city', 'bookingCount': { $sum: 1 } } }",
            // Ordiniamo in base al numero di prenotazioni (bookingCount) in modo decrescente
            "{ $sort: { 'bookingCount': -1 } }",
            // Limitiamo il risultato alle prime 10 città
            "{ $limit: 10 }"
    })
    List<CityBookingRankingDTO> findTopCitiesByBookings(LocalDate lastYearStart);


}
