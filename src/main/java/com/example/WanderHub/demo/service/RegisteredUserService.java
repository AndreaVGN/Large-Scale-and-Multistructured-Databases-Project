package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.LoginRequest;
import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import com.example.WanderHub.demo.repository.AccommodationRepository;

import jdk.jfr.Registered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;
    @Autowired
    private AccommodationService accommodationService;

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

    // Autenticazione dell'utente (login)
    public boolean authenticate(LoginRequest loginRequest, HttpSession session) {
        Optional<RegisteredUser> userOptional = registeredUserRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            RegisteredUser user = userOptional.get();

            if (user.getPassword().equals(loginRequest.getPassword())) {
                session.setAttribute("user", user); // Salva l'utente nella sessione
                return true;
            }
        }
        return false;
    }
}
