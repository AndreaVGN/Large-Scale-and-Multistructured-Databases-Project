package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository RegisteredUserRepository;

    // Creazione di una nuova sistemazione
    public RegisteredUser createRegisteredUser(RegisteredUser registeredUser) {
        return RegisteredUserRepository.save(registeredUser);
    }

    public RegisteredUser getRegisteredUserById(String username) {
        return RegisteredUserRepository.findByRegisteredUserId(username)
                .orElseThrow(() -> new ResourceNotFoundException("RegisteredUser not found with id: " + username));
    }

    public boolean deleteRegisteredUserById(String username) {
        if(RegisteredUserRepository.existsByRegisteredUserId(username)) {
            RegisteredUserRepository.deleteByRegisteredUserId(username);
            return true;
        }
        return false;
    }

    // Altri metodi per gestire le sistemazioni

}
