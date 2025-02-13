package com.example.WanderHub.demo.model;

import com.example.WanderHub.demo.model.Book;

public class PendingBook extends Book {
    private String accommodationId;

    public PendingBook() {}

    public PendingBook(String accommodationId, Book book) {
        super(book.getOccupiedDates(), book.getUsername(), book.getEmail(), book.getBirthPlace(), book.getAddress(),
                book.getAddressNumber(), book.getBirthDate(), book.getGuestFirstNames(), book.getGuestLastNames(),
                book.getBookDate());
        this.accommodationId = accommodationId;
    }

    public String getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(String accommodationId) {
        this.accommodationId = accommodationId;
    }
}
