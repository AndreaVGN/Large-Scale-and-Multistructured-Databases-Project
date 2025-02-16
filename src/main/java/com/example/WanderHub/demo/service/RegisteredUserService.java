package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.AuthRequestDTO;
import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;

import com.example.WanderHub.demo.utility.Password;
import com.example.WanderHub.demo.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;


    @Autowired
    public RegisteredUserService(RegisteredUserRepository registeredUserRepository) {
        this.registeredUserRepository = registeredUserRepository;
    }

    // Users cannot use admin as their username!
    public RegisteredUser createRegisteredUser(RegisteredUser registerUser) {
        try {
            if (registerUser.getUsername().toLowerCase().contains("admin")) {
                throw new IllegalArgumentException("Forbidden username!");
            }

            if (registeredUserRepository.findByUsername(registerUser.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }

            Validator.validateUser(registerUser);

            String hashedPassword = Password.hashPassword(registerUser.getPassword());

            registerUser.setPassword(hashedPassword);

            return registeredUserRepository.save(registerUser);

        } catch (IllegalArgumentException e) {
            System.err.println("Error creating user: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error creating user: " + e.getMessage());
            throw new RuntimeException("Error creating user", e);
        }
    }


    public RegisteredUser getRegisteredUserByUsername(String username) {
        try {
            return registeredUserRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Username not found with username: " + username));
        } catch (ResourceNotFoundException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error finding user: " + e.getMessage());
            throw new RuntimeException("Error finding user", e);
        }
    }

    public boolean deleteRegisteredUserByUsername(String username) {
        try {
            if (registeredUserRepository.existsByUsername(username)) {
                registeredUserRepository.deleteByUsername(username);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting user by username: " + e.getMessage());
            throw new RuntimeException("Error deleting user", e);
        }
    }

    // Login
    public boolean authenticate(AuthRequestDTO loginRequest, HttpSession session) {
        try {
            Optional<RegisteredUser> userOptional = registeredUserRepository.findByUsername(loginRequest.getUsername());

            if (userOptional.isPresent()) {
                RegisteredUser user = userOptional.get();

                String hashedInputPassword = Password.hashPassword(loginRequest.getPassword());

                if (user.getPassword().equals(hashedInputPassword)) {
                    // Salva l'utente nella sessione
                    session.setAttribute("user", user.getUsername());
                    session.setAttribute("email", user.getEmail());
                    session.setAttribute("birthDate", user.getBirthDate());
                    session.setAttribute("birthPlace", user.getBirthPlace());
                    session.setAttribute("address", user.getAddress());
                    session.setAttribute("addressNumber", user.getAddressNumber());
                    System.out.println(session.getAttribute("user"));
                    return true;
                }
                return false;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            throw new RuntimeException("Error during authentication", e);
        }
    }
}
