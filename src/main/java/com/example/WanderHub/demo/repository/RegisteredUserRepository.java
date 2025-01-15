package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepository extends MongoRepository<RegisteredUser, String> {
    // Metodi per interrogare il database, se necessari
}
