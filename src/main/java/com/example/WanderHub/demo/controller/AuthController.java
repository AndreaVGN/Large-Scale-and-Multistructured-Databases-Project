package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.LoginRequest;
import com.example.WanderHub.demo.DTO.RegisteredUserDTO;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        System.out.println("Ci \n\n\n\n\n\n\nerhbgeriubiuguerbg");
        boolean isAuthenticated = registeredUserService.authenticate(loginRequest, session);
        System.out.println("Ci erhbenjgrkjbgekjrbgkjebkjgrberkbgbgkgeriubiuguerbg");
        if (isAuthenticated) {
            System.out.println("Ci erhbgeriubiuguerbg");
            return ResponseEntity.ok("Login successful.");

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    /*
    // Endpoint per la registrazione (Signup)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest registerRequest) {
        boolean isUserCreated = registeredUserService.register(registerRequest);

        if (isUserCreated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists.");
        }
    }*/

    // Endpoint per il logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Invalidiamo la sessione per disconnettere l'utente
        return ResponseEntity.ok("Logged out successfully.");
    }
}
