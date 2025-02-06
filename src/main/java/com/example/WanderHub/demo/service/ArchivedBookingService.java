package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.CityBookingRankingDTO;
import com.example.WanderHub.demo.repository.ArchivedBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ArchivedBookingService {

    @Autowired
    private ArchivedBookingRepository ArchivedBookingRepository;

    public List<CityBookingRankingDTO> getTopCities() {
        // Calcoliamo la data di inizio dell'anno scorso
        LocalDate lastYearStart = LocalDate.now().minusYears(1).withDayOfYear(1);

        // Recuperiamo la classifica delle città più visitate
        return ArchivedBookingRepository.findTopCitiesByBookings(lastYearStart);
    }
    public AverageBookingResult findAverageBookingDurationByCity(String city){
        System.out.println("ci sono");
        return ArchivedBookingRepository.findAverageBookingDurationByCity(city);
    }
    public BirthPlaceFrequency findMostCommonBirthPlaceByCity(String city){
        return ArchivedBookingRepository.findMostCommonBirthPlaceByCity(city);
    }

    public List<CityAverageAgeDTO> getAverageAgeByCity(String city) {
        int currentYear = LocalDate.now().getYear();
        return ArchivedBookingRepository.findAverageAgeByCity(city, currentYear);
    }

    // Funzione per ottenere la classifica delle città in base alla fascia di prezzo
    public List<CityBookingRankingDTO> getTopCitiesByPriceRange(double minPrice, double maxPrice) {
        // Chiamata al repository per ottenere le città più visitate in base alla fascia di prezzo
        return ArchivedBookingRepository.findTopCitiesByPriceRange(minPrice, maxPrice);
    }

    public List<CityMonthlyVisitDTO> getMonthlyVisitsByCity(String city) {
        return ArchivedBookingRepository.findMonthlyVisitsByCity(city);
    }


}
