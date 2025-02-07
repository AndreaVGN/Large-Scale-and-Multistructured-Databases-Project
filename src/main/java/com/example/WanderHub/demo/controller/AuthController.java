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




}
