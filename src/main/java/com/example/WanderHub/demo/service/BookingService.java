package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.BookingRepository;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private AccommodationRepository accommodationRepository;

/*
    public boolean bookHouse(int houseId, String start, String end) {
        return bookingRepository.lockHouse(houseId, start, end);
    }*/

    public String getBookingTimestamp(ObjectId houseId, String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate inizio = LocalDate.parse(start, formatter);
        if (inizio.isBefore(today)) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        LocalDate fine = LocalDate.parse(end, formatter);
        if (fine.isAfter(today.plusYears(1))) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        if(inizio.isAfter(fine)){
            throw new RuntimeException("periodo di tempo non valido.");
        }

        return bookingRepository.lockHouse(houseId, start, end); // Restituisce il timestamp
    }


    public boolean bookHouseReg(ObjectId houseId,String username, String start, String end) {
        System.out.println("DEBUG: Start date received -> " + start);
        System.out.println("DEBUG: End date received -> " + end);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate inizio = LocalDate.parse(start, formatter);
        if (inizio.isBefore(today)) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        LocalDate fine = LocalDate.parse(end, formatter);
        if (fine.isAfter(today.plusYears(1))) {
            throw new RuntimeException("periodo di tempo non valido.");
        }
        if(inizio.isAfter(fine)){
            throw new RuntimeException("periodo di tempo non valido.");
        }
        Accommodation accommodation = accommodationRepository.findByAccommodationId(houseId)
                .orElseThrow(() -> new RuntimeException("Accommodation not found"));

        // Recupera l'utente cliente che sta facendo la prenotazione
           /* RegisteredUser customer = registeredUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));*/

        return bookingRepository.lockHouseReg(houseId,username, start, end);
    }
    public boolean unlockHouse(ObjectId houseId, String start, String end, String timestampCookie) {
        return bookingRepository.unlockHouse(houseId, start, end, timestampCookie);
    }
    public boolean unlockHouseReg(ObjectId houseId, String username, String start, String end) {
        return bookingRepository.unlockHouseReg(houseId, username, start, end);
    }
}
