package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService BookService;

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return BookService.createBook(book);
    }



    @GetMapping("/filter")
    public List<Book> getBooksByCityAndPeriod(
            @RequestParam String city,
            @RequestParam String period) {
        return BookService.getBooksByCityAndPeriod(city, period);
    }



}
