package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import jdk.jfr.Registered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    // Creazione di una nuova sistemazione
    public RegisteredUser createRegisteredUser(RegisteredUser registeredUser) {
        return registeredUserRepository.save(registeredUser);
    }

    public RegisteredUser getRegisteredUserByUsername(String username) {
        return registeredUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found with username: " + username));
    }

    public boolean deleteRegisteredUserByUsername(String username) {
        if(registeredUserRepository.existsByUsername(username)) {
            registeredUserRepository.deleteByUsername(username);
            return true;
        }
        return false;
    }

    public RegisteredUser addBookToRegisteredUser(String username, Book newBook) {
        // Trova la sistemazione esistente
        RegisteredUser registeredUser = registeredUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with user: " + username));

        // Aggiungi la nuova prenotazione (Book) all'array di books
        List<Book> booksList = registeredUser.getBooks();

        booksList.add(newBook);  // Aggiungi il nuovo oggetto Book

        // Salva la sistemazione aggiornata con la nuova prenotazione
        registeredUser.setBooks(booksList);

        return registeredUserRepository.save(registeredUser);  // Salva l'accommodation aggiornata
    }

    public RegisteredUser addAccommodationToRegisteredUser(String username, Accommodation accommodation) {
        // Trova la sistemazione esistente
        RegisteredUser registeredUser = registeredUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with user: " + username));

        // Aggiungi la nuova prenotazione (Book) all'array di books
        List <Integer> accommodationList = registeredUser.getAccommodations();

        accommodationList.add(accommodation.getAccommodationId());  // Aggiungi il nuovo oggetto Book

        // Salva la sistemazione aggiornata con la nuova prenotazione
        registeredUser.setAccommodations(accommodationList);

        return registeredUserRepository.save(registeredUser);  // Salva l'accommodation aggiornata
    }

    public List<Book> getPendingBookings(String username) {
        // Otteniamo direttamente le prenotazioni pendenti dell'utente
        return registeredUserRepository.findPendingBooksByUsername(username);
    }
    public List<Integer> getAccommodationByUsername(String username){
        return registeredUserRepository.findAccommodationByUsername(username);
    }



}
