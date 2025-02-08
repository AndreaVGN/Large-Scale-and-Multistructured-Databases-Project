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

    public List<CityBookingRankingDTO> getTopCities() {
        try {
            // Calcoliamo la data di inizio dell'anno scorso
            LocalDate lastYearStart = LocalDate.now().minusYears(1).withDayOfYear(1);

            // Recuperiamo la classifica delle città più visitate
            return archivedBookRepository.findTopCitiesByBookings(lastYearStart);
        } catch (Exception e) {
            // Gestione dell'errore
            throw new RuntimeException("Error occurred while fetching top cities: " + e.getMessage(), e);
        }
    }

    public AverageBookingResultDTO findAverageBookingDurationByCity(String city) {
        try {
            return archivedBookRepository.findAverageBookingDurationByCity(city);
        } catch (Exception e) {
            // Gestione dell'errore
            throw new RuntimeException("Error occurred while fetching average booking duration by city: " + e.getMessage(), e);
        }
    }

    public BirthPlaceFrequencyDTO findMostCommonBirthPlaceByCity(String city) {
        try {
            return archivedBookRepository.findMostCommonBirthPlaceByCity(city);
        } catch (Exception e) {
            // Gestione dell'errore
            throw new RuntimeException("Error occurred while fetching most common birth place by city: " + e.getMessage(), e);
        }
    }

    public List<CityAverageAgeDTO> getAverageAgeByCity(String city) {
        try {
            int currentYear = LocalDate.now().getYear();
            return archivedBookRepository.findAverageAgeByCity(city, currentYear);
        } catch (Exception e) {
            // Gestione dell'errore
            throw new RuntimeException("Error occurred while fetching average age by city: " + e.getMessage(), e);
        }
    }

    public List<CityBookingRankingDTO> getTopCitiesByPriceRange(double minPrice, double maxPrice) {
        try {
            // Chiamata al repository per ottenere le città più visitate in base alla fascia di prezzo
            return archivedBookRepository.findTopCitiesByPriceRange(minPrice, maxPrice);
        } catch (Exception e) {
            // Gestione dell'errore
            throw new RuntimeException("Error occurred while fetching top cities by price range: " + e.getMessage(), e);
        }
    }

    public List<CityMonthlyVisitDTO> getMonthlyVisitsByCity(String city) {
        try {
            return archivedBookRepository.findMonthlyVisitsByCity(city);
        } catch (Exception e) {
            // Gestione dell'errore
            throw new RuntimeException("Error occurred while fetching monthly visits by city: " + e.getMessage(), e);
        }
    }



}
