package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.PendingBook;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookToUserTransferService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Scheduled(cron = "0 0 3 * * ?") // Esegue ogni giorno alle 3:00 AM
   // @PostConstruct
    public void transferBookingsToRegisteredUsers() {
        LocalDate yesterday = LocalDate.now(); //.minusDays(1);

        List<Accommodation> accommodations = accommodationRepository.findByBooksBookDate(yesterday);
        System.out.println(accommodations);

        for (Accommodation accommodation : accommodations) {
            List<Book> books = accommodation.getBooks();
            for (Book book : books) {
                if (book.getBookDate().equals(yesterday)) {
                    String username = book.getUsername();

                    RegisteredUser user = registeredUserRepository.findById(username).orElse(null);
                    if (user != null) {

                        PendingBook embeddedBooking = new PendingBook(
                                accommodation.getAccommodationId(),
                                book
                        );

                        user.getBooks().add(embeddedBooking);

                        registeredUserRepository.save(user);
                    }
                }
            }
        }
        System.out.println("Fine Trasferimento");
    }


}

