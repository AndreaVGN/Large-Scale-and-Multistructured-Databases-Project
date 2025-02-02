package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AuthRequest;
import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import com.example.WanderHub.demo.repository.AccommodationRepository;

import com.example.WanderHub.demo.utility.Password;
import com.example.WanderHub.demo.utility.Validator;
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

    @Autowired
    public RegisteredUserService(RegisteredUserRepository registeredUserRepository) {
        this.registeredUserRepository = registeredUserRepository;
    }

    public RegisteredUser createRegisteredUser(RegisteredUser registerUser) {
        if (registeredUserRepository.findByUsername(registerUser.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Validator.validateUser(registerUser);

        String hashedPassword = Password.hashPassword(registerUser.getPassword());

        registerUser.setPassword(hashedPassword);

        return registeredUserRepository.save(registerUser);
    }

    public RegisteredUser getRegisteredUserByUsername(String username) {
        return registeredUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found with username: " + username));
    }

    public boolean deleteRegisteredUserByUsername(String username) {
        if (registeredUserRepository.existsByUsername(username)) {
            registeredUserRepository.deleteByUsername(username);
            return true;
        }
        return false;
    }

    // Autenticazione dell'utente (login)
    public boolean authenticate(AuthRequest loginRequest, HttpSession session) {
        Optional<RegisteredUser> userOptional = registeredUserRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            RegisteredUser user = userOptional.get();

            // Hash della password inserita per il confronto
            String hashedInputPassword = Password.hashPassword(loginRequest.getPassword());

            // Confronta l'hash della password
            if (user.getPassword().equals(hashedInputPassword)) {
                // Salva l'utente nella sessione
                session.setAttribute("user", user.getUsername());
                session.setAttribute("email",user.getEmail());
                session.setAttribute("birthDate",user.getBirthDate());
                session.setAttribute("birthPlace",user.getBirthPlace());
                session.setAttribute("address",user.getAddress());
                session.setAttribute("addressNumber",user.getAddressNumber());
                System.out.println(session.getAttribute("user"));
                return true;
            }
            return false;
        }

        return false;
    }
}