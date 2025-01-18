package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.model.Book;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RegisteredUserRepository extends MongoRepository<RegisteredUser, String> {
    // Metodi per interrogare il database, se necessari
    // Puoi aggiungere metodi di ricerca personalizzati, se necessario
    @Query("{ 'username': ?0 }")
    Optional<RegisteredUser> findByUsername(String username);

    boolean existsByUsername(String username);
    void deleteByUsername(String Username);

    @Aggregation(pipeline = {
            "{ '$match': { 'username': ?0 } }", // Filtra per username
            "{ '$unwind': '$books' }", // Scompatta l'array books
            "{ '$match': { 'books.transactionState': false } }", // Filtra i libri con transactionState: false
            "{ '$replaceRoot': { 'newRoot': '$books' } }" // Ritorna solo il contenuto del libro
    })
    List<Book> findPendingBooksByUsername(String username);



    @Aggregation(pipeline = {
            "{ '$match': { 'username': ?0 } }",  // Filtra per username
            "{ '$unwind': '$accommodations' }",  // Scomponi l'array accommodations
            "{ '$project': { 'accommodations': 1 , '_id' : 0} }"
    })
    List<Integer> findAccommodationByUsername(String username);

    @Query("{ 'username': ?0 }")
    boolean removeAccommodationFromUser(String username, int accommodationId);
}
