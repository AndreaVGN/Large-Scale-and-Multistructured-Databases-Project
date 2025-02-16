package com.example.WanderHub.demo.DTO;

import java.util.List;
import com.example.WanderHub.demo.model.Book;  // Assumendo che Review sia un modello esistente
import lombok.*;


public class BookDTO {

    private List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public BookDTO(List<Book> books) {
        this.books = books;
    }
}



