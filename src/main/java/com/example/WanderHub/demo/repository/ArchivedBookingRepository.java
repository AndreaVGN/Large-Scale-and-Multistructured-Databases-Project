package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.DTO.AverageBookingResult;
import com.example.WanderHub.demo.DTO.BirthPlaceFrequency;
import com.example.WanderHub.demo.DTO.CityBookingRankingDTO;
import com.example.WanderHub.demo.model.ArchivedBooking;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ArchivedBookingRepository extends MongoRepository<ArchivedBooking, String> {
    // Metodo per salvare in batch (viene già gestito da `saveAll()`)

    @Aggregation(pipeline = {
            "{ $unwind: '$occupiedDates' }", // Unwind per esplodere i periodi di occupazione
            "{ $match: { 'city': ?0 } }", // Filtro per la città passata come parametro (ad esempio, 'Paris')
            "{ $project: { " +
                    "'accommodationId': 1, " +
                    "'hostUsername': 1, " +
                    "'city': 1, " +
                    "'startDate': '$occupiedDates.start', " +
                    "'endDate': '$occupiedDates.end', " +
                    "'durationInDays': { $divide: [ { $subtract: [ '$occupiedDates.end', '$occupiedDates.start' ] }, 86400000 ] } " + // Calcolo della durata in giorni (86400000 ms = 1 giorno)
                    "} }",
            "{ $group: { _id: '$city', averageDays: { $avg: '$durationInDays' } } }", // Raggruppa per città e calcola la media dei giorni
            "{ $project: { 'city': '$_id', 'averageDays': 1, '_id': 0 } }" // Ripristina il campo 'city' e rimuovi '_id'
    })
    AverageBookingResult findAverageBookingDurationByCity(String city);


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


    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0 } }", // Filtra per la città
            "{ $group: { '_id': '$birthPlace', 'count': { $sum: 1 } } }", // Raggruppa per 'birthPlace' e conta
            "{ $sort: { 'count': -1 } }", // Ordina per il conteggio
            "{ $limit: 1 }", // Limita a 1 il risultato
            "{ $project: { 'birthPlace': '$_id', 'count': 1, '_id': 0 } }" // Proietta i campi per la mappatura
    })
    BirthPlaceFrequency findMostCommonBirthPlaceByCity(String city);



}
