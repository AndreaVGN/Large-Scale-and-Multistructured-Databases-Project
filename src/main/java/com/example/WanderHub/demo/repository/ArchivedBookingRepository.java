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
            // Filtro per la città
            "{ $match: { 'city': ?0 } }",

            // Decomponiamo l'array 'occupiedDates' per ogni prenotazione
            "{ $unwind: '$occupiedDates' }",

            // Proiettiamo i dati necessari per il calcolo dell'età (conversione del birthDate da stringa)
            "{ $project: { " +
                    "birthDate: '$birthDate', " +  // Visualizziamo la data di nascita
                    "age: { " +
                    "$subtract: [ " +
                    "{ $literal: ?1 }, " +  // Usa l'anno passato come parametro (CurrentYear)
                    "{ $year: { $dateFromString: { 'dateString': '$birthDate' } } }" +  // Anno di nascita
                    "] " +
                    "}, " +
                    "city: 1 " +  // Manteniamo il campo city
                    "} }",

            // Raggruppa per città e calcola l'età media
            "{ $group: { _id: '$city', averageAge: { $avg: '$age' } } } ",

            // Proiettiamo il risultato finale
            "{ $project: { city: '$_id', averageAge: 1, _id: 0 } }"
    })
    List<CityAverageAgeDTO> findAverageAgeByCity(String city, int currentYear);

    @Aggregation(pipeline = {
            // Filtro per la fascia di prezzo
            "{ $match: { 'costPerNight': { $gte: ?0, $lte: ?1 } } }",  // Minimo e massimo prezzo

            // Raggruppa per città e calcola il numero di prenotazioni per città
            "{ $group: { _id: '$city', bookingCount: { $sum: 1 } } }",

            // Ordiniamo per numero di prenotazioni (bookingCount) in modo decrescente
            "{ $sort: { bookingCount: -1 } }",

            // Limitiamo il risultato alle prime 10 città
            "{ $limit: 10 }",

            // Proiettiamo il risultato finale
            "{ $project: { city: '$_id', bookingCount: 1, _id: 0 } }"
    })
    List<CityBookingRankingDTO> findTopCitiesByPriceRange(double minPrice, double maxPrice);

    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0 } }",  // Filtro per la città
            "{ $unwind: '$occupiedDates' }",  // Decomponiamo l'array 'occupiedDates'
            "{ $project: { " +
                    "month: { $month: '$occupiedDates.start' }, " +  // Estraiamo il mese
                    "city: 1 " +  // Manteniamo il campo city
                    "} }",
            "{ $group: { " +
                    "_id: { city: '$city', month: '$month' }, " +
                    "visitCount: { $sum: 1 } " +  // Conta le visite
                    "} }",
            "{ $project: { city: '$_id.city', month: '$_id.month', visitCount: 1, _id: 0 } }",
            "{ $sort: { 'month': 1 } }"  // Ordina per mese
    })
    List<CityMonthlyVisitDTO> findMonthlyVisitsByCity(String city);









    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0 } }", // Filtra per la città
            "{ $group: { '_id': '$birthPlace', 'count': { $sum: 1 } } }", // Raggruppa per 'birthPlace' e conta
            "{ $sort: { 'count': -1 } }", // Ordina per il conteggio
            "{ $limit: 1 }", // Limita a 1 il risultato
            "{ $project: { 'birthPlace': '$_id', 'count': 1, '_id': 0 } }" // Proietta i campi per la mappatura
    })
    BirthPlaceFrequency findMostCommonBirthPlaceByCity(String city);



}
