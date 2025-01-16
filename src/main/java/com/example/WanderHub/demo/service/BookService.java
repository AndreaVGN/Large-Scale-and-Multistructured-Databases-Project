package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BookService {

    @Autowired
    private BookRepository BookRepository;

    // Creazione di una nuova sistemazione
    public Book createBook(Book book) {
        return BookRepository.save(book);
    }

    public Book getBookById(int bookId) {
        return BookRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
    }

    public boolean deleteBookById(int bookId) {
        if(BookRepository.existsByBookId(bookId)) {
            BookRepository.deleteByBookId(bookId);
            return true;
        }
        return false;
    }

    // Altri metodi per gestire le sistemazioni

}
