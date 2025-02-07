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
        // Calcoliamo la data di inizio dell'anno scorso
        LocalDate lastYearStart = LocalDate.now().minusYears(1).withDayOfYear(1);

        // Recuperiamo la classifica delle città più visitate
        return archivedBookRepository.findTopCitiesByBookings(lastYearStart);
    }
    public AverageBookingResult findAverageBookingDurationByCity(String city){
        System.out.println("ci sono");
        return archivedBookRepository.findAverageBookingDurationByCity(city);
    }
    public BirthPlaceFrequency findMostCommonBirthPlaceByCity(String city){
        return archivedBookRepository.findMostCommonBirthPlaceByCity(city);
    }

    public List<CityAverageAgeDTO> getAverageAgeByCity(String city) {
        int currentYear = LocalDate.now().getYear();
        return archivedBookRepository.findAverageAgeByCity(city, currentYear);
    }

    // Funzione per ottenere la classifica delle città in base alla fascia di prezzo
    public List<CityBookingRankingDTO> getTopCitiesByPriceRange(double minPrice, double maxPrice) {
        // Chiamata al repository per ottenere le città più visitate in base alla fascia di prezzo
        return archivedBookRepository.findTopCitiesByPriceRange(minPrice, maxPrice);
    }

    public List<CityMonthlyVisitDTO> getMonthlyVisitsByCity(String city) {
        return archivedBookRepository.findMonthlyVisitsByCity(city);
    }


}
