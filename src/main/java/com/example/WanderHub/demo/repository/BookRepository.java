package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, Long> {
    // Metodi per interrogare il database, se necessari
    // Metodi per interrogare il database, se necessari
    @Query("{ 'bookId': ?0 }")
    Optional<Book> findByBookId(int bookId);

    boolean existsByBookId(int bookId);
    void deleteByBookId(int bookId);
}
