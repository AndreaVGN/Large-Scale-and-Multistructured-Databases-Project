package com.example.WanderHub.demo.repository;

import com.example.WanderHub.demo.model.Book;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;



@Repository
public interface BookRepository extends MongoRepository<Book, Long> {

}
