package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService BookService;

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return BookService.createBook(book);
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable int id) {
        return BookService.getBookById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        boolean isDeleted = BookService.deleteBookById(id);

        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
