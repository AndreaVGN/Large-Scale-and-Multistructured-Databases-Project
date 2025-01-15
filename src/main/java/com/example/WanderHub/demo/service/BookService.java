package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Creazione di una nuova prenotazione
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    // Recupero di tutte le prenotazioni
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Altri metodi come aggiornamento e cancellazione
}
