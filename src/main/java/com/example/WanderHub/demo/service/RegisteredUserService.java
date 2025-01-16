package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import jdk.jfr.Registered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
