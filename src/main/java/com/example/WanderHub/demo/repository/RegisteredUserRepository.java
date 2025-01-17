package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends MongoRepository<RegisteredUser, String> {
    // Metodi per interrogare il database, se necessari
    // Puoi aggiungere metodi di ricerca personalizzati, se necessario
    @Query("{ 'username': ?0 }")
    Optional<RegisteredUser> findByUsername(String username);

    boolean existsByUsername(String username);
    void deleteByUsername(String Username);

    @Query("{ 'username': ?0, 'books.transactionState': false }")
    List<Book> findPendingBookingsByUsername(String username);

}
