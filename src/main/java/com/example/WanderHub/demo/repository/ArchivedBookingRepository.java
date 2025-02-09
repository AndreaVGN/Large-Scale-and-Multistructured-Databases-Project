package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.ArchivedBook;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArchivedBookingRepository extends MongoRepository<ArchivedBook, String> {

    @Aggregation(pipeline = {
            "{ $unwind: '$occupiedDates' }",
            "{ $match: { 'city': ?0 } }",
            "{ $project: { " +
                    "'accommodationId': 1, " +
                    "'hostUsername': 1, " +
                    "'city': 1, " +
                    "'startDate': '$occupiedDates.start', " +
                    "'endDate': '$occupiedDates.end', " +
                    "'durationInDays': { $divide: [ { $subtract: [ '$occupiedDates.end', '$occupiedDates.start' ] }, 86400000 ] } " +
                    "} }",
            "{ $group: { _id: '$city', averageDays: { $avg: '$durationInDays' } } }",
            "{ $project: { 'city': '$_id', 'averageDays': 1, '_id': 0 } }"
    })
    AverageBookingResultDTO findAverageBookingDurationByCity(String city);


    @Aggregation(pipeline = {
            "{ $match: { 'occupiedDates.end': { $gte: ?0 } } }",

            "{ $unwind: '$occupiedDates' }",

            "{ $group: { '_id': '$city', 'bookingCount': { $sum: 1 } } }",

            "{ $sort: { 'bookingCount': -1 } }",

            "{ $limit: 10 }"
    })
    List<CityBookingRankingDTO> findTopCitiesByBookings(LocalDate lastYearStart);

    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0 } }",

            "{ $unwind: '$occupiedDates' }",

            "{ $project: { " +
                    "birthDate: '$birthDate', " +
                    "age: { " +
                    "$subtract: [ " +
                    "{ $literal: ?1 }, " +
                    "{ $year: { $dateFromString: { 'dateString': '$birthDate' } } }" +
                    "] " +
                    "}, " +
                    "city: 1 " +
                    "} }",

            "{ $group: { _id: '$city', averageAge: { $avg: '$age' } } } ",

            "{ $project: { city: '$_id', averageAge: 1, _id: 0 } }"
    })
    List<CityAverageAgeDTO> findAverageAgeByCity(String city, int currentYear);

    @Aggregation(pipeline = {
            "{ $match: { 'costPerNight': { $gte: ?0, $lte: ?1 } } }",

            "{ $group: { _id: '$city', bookingCount: { $sum: 1 } } }",

            "{ $sort: { bookingCount: -1 } }",

            "{ $limit: 10 }",

            "{ $project: { city: '$_id', bookingCount: 1, _id: 0 } }"
    })
    List<CityBookingRankingDTO> findTopCitiesByPriceRange(double minPrice, double maxPrice);

    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0 } }",
            "{ $unwind: '$occupiedDates' }",
            "{ $project: { " +
                    "month: { $month: '$occupiedDates.start' }, " +
                    "city: 1 " +
                    "} }",
            "{ $group: { " +
                    "_id: { city: '$city', month: '$month' }, " +
                    "visitCount: { $sum: 1 } " +
                    "} }",
            "{ $project: { city: '$_id.city', month: '$_id.month', visitCount: 1, _id: 0 } }",
            "{ $sort: { 'month': 1 } }"
    })
    List<CityMonthlyVisitDTO> findMonthlyVisitsByCity(String city);


    @Aggregation(pipeline = {
            "{ $match: { 'city': ?0 } }",
            "{ $group: { '_id': '$birthPlace', 'count': { $sum: 1 } } }",
            "{ $sort: { 'count': -1 } }",
            "{ $limit: 1 }",
            "{ $project: { 'birthPlace': '$_id', 'count': 1, '_id': 0 } }"
    })
    BirthPlaceFrequencyDTO findMostCommonBirthPlaceByCity(String city);



}
