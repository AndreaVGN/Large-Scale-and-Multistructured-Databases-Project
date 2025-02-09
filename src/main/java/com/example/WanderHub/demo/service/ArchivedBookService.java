package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.repository.ArchivedBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ArchivedBookService {

    @Autowired
    private ArchivedBookingRepository archivedBookRepository;

    // Analytic: Return the 10 most visited cities and the number of books for each cities
    public List<CityBookingRankingDTO> getTopCities() {
        try {
            LocalDate lastYearStart = LocalDate.now().minusYears(1).withDayOfYear(1);

            return archivedBookRepository.findTopCitiesByBookings(lastYearStart);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching top cities: " + e.getMessage(), e);
        }
    }

    // Analytic: Return the average vacation duration (in terms of days) given a city
    public AverageBookingResultDTO findAverageBookingDurationByCity(String city) {
        try {
            return archivedBookRepository.findAverageBookingDurationByCity(city);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching average booking duration by city: " + e.getMessage(), e);
        }
    }

    // Analytic: Return the most common birthplace of tourists given a city
    public BirthPlaceFrequencyDTO findMostCommonBirthPlaceByCity(String city) {
        try {
            return archivedBookRepository.findMostCommonBirthPlaceByCity(city);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching most common birth place by city: " + e.getMessage(), e);
        }
    }

    // Analytic: Return the average age of tourists in the last year given a city
    public List<CityAverageAgeDTO> getAverageAgeByCity(String city) {
        try {
            int currentYear = LocalDate.now().getYear();
            return archivedBookRepository.findAverageAgeByCity(city, currentYear);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching average age by city: " + e.getMessage(), e);
        }
    }

    // Analytic: Return the 10 most visited cities with cost in the range given as input (min and max)
    public List<CityBookingRankingDTO> getTopCitiesByPriceRange(double minPrice, double maxPrice) {
        try {
            return archivedBookRepository.findTopCitiesByPriceRange(minPrice, maxPrice);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching top cities by price range: " + e.getMessage(), e);
        }
    }

    // Analytic: Return for each month of the year the total books of a city given as input
    public List<CityMonthlyVisitDTO> getMonthlyVisitsByCity(String city) {
        try {
            return archivedBookRepository.findMonthlyVisitsByCity(city);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching monthly visits by city: " + e.getMessage(), e);
        }
    }



}
