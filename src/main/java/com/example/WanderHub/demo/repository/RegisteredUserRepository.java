package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends MongoRepository<RegisteredUser, String> {
    // Metodi per interrogare il database, se necessari
    @Query("{ 'username': ?0 }")
    Optional<Book> findByRegisteredUserId(String username);

    boolean existsByRegisteredUserId(String username);
    void deleteByRegisteredUserId(String username);
}
