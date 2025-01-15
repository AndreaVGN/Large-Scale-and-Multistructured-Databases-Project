package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    // Creazione di un nuovo utente
    public RegisteredUser createRegisteredUser(RegisteredUser user) {
        return registeredUserRepository.save(user);
    }

    // Altri metodi per gestire gli utenti
}
