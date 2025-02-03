package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public boolean bookHouse(int houseId, String start, String end) {
        return bookingRepository.lockHouse(houseId, start, end);
    }

    public boolean bookHouseReg(int houseId,String username, String start, String end) {
        return bookingRepository.lockHouseReg(houseId,username, start, end);
    }
}
