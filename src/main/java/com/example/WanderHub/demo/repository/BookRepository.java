package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.ArchivedBooking;
import com.example.WanderHub.demo.model.Book;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, Long> {

    @Query("{ 'birthPlace': ?0, 'bookPeriod': ?1 }")
    List<Book> findByCityAndPeriod(String city, String period);



}
