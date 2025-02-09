package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private RegisteredUserService registeredUserService;

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
}
