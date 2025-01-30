package com.example.WanderHub.demo.model;

import com.example.WanderHub.demo.utility.OccupiedPeriod;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Book {
    private Long bookId;
    //private int guestNumber;
    private List<OccupiedPeriod> occupiedDates; // Elenco dei periodi occupati
    //private int accommodationId;
    private String username;
    private String name;
    private String surname;
    private boolean transactionState;
    //private LocalDate paymentExpiration;
    private String email;
    private String birthPlace;
    private String address;
    private int addressNumber;
    private LocalDate birthDate;
    private String paymentToken;
    private String[] guestFirstNames; // Array dei nomi degli ospiti
    private String[] guestLastNames;  // Array dei cognomi degli ospiti

    // Costruttore vuoto
    public Book() {
    }

    // Costruttore con un parametro
    public Book(Long bookId) {
        this.bookId = bookId;
    }

    // Costruttore completo
    public Book(Long bookId, List<OccupiedPeriod> occupiedDates, String username, String name, String surname, boolean transactionState,
                LocalDate paymentExpiration, String email, String birthPlace, String address, int addressNumber,
                LocalDate birthDate, String paymentToken, String[] guestFirstNames, String[] guestLastNames) {
        this.bookId = bookId;
        //this.guestNumber = guestNumber;
        this.occupiedDates = occupiedDates;
        //this.accommodationId = accommodationId;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.transactionState = transactionState;
        //this.paymentExpiration = paymentExpiration;
        this.email = email;
        this.birthPlace = birthPlace;
        this.address = address;
        this.addressNumber = addressNumber;
        this.birthDate = birthDate;
        this.paymentToken = paymentToken;
        this.guestFirstNames = guestFirstNames;
        this.guestLastNames = guestLastNames;
    }

    // Getters e Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    /*public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }*/

    public List<OccupiedPeriod> getOccupiedDates() {
        return occupiedDates;
    }

    public void setOccupiedDates(List<OccupiedPeriod> occupiedDates) {
        this.occupiedDates = occupiedDates;
    }
    /*public int getAccommodationId() {
        return accommodationId;
    }
    public void setAccommodationId(int accommodationId) {
        this.accommodationId = accommodationId;
    }*/
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public boolean getTransactionState() {
        return transactionState;
    }

    public void setTransactionState(boolean transactionState) {
        this.transactionState = transactionState;
    }

    /*public LocalDate getPaymentExpiration() {
        return paymentExpiration;
    }

    public void setPaymentExpiration(LocalDate paymentExpiration) {
        this.paymentExpiration = paymentExpiration;
    }*/

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(int addressNumber) {
        this.addressNumber = addressNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public String[] getGuestFirstNames() {
        return guestFirstNames;
    }

    public void setGuestFirstNames(String[] guestFirstNames) {
        this.guestFirstNames = guestFirstNames;
    }

    public String[] getGuestLastNames() {
        return guestLastNames;
    }

    public void setGuestLastNames(String[] guestLastNames) {
        this.guestLastNames = guestLastNames;
    }

    // Override del metodo toString
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                //", guestNumber=" + guestNumber +
                ", occupiedDates=" + occupiedDates +
                ", username='" + username + '\'' +
                ", transactionState='" + transactionState + '\'' +
                //", paymentExpiration=" + paymentExpiration +
                ", email='" + email + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", address='" + address + '\'' +
                ", addressNumber=" + addressNumber +
                ", birthDate=" + birthDate +
                ", paymentToken='" + paymentToken + '\'' +
                ", guestFirstNames=" + Arrays.toString(guestFirstNames) +
                ", guestLastNames=" + Arrays.toString(guestLastNames) +
                '}';
    }
}
