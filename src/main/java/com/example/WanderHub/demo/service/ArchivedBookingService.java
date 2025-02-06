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
}
