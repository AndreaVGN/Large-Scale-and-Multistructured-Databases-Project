package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.AuthRequest;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/auth") // Percorso per tutte le operazioni di autenticazione
public class AuthController {

    @Autowired
    private RegisteredUserService registeredUserService; // Servizio che gestisce la logica di login, logout e registrazione

    // Endpoint per il login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest, HttpSession session) {
        boolean isAuthenticated = registeredUserService.authenticate(loginRequest, session);
        if (isAuthenticated) {
            return ResponseEntity.status(HttpStatus.OK).body("Login successful");

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }


    // Endpoint for user signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisteredUser registerUser) {
        try {
            registeredUserService.createRegisteredUser(registerUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Endpoint per il logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Invalidiamo la sessione per disconnettere l'utente
        return ResponseEntity.ok("Logged out successfully.");
    }
}
