package com.example.WanderHub.demo.model;

import com.example.WanderHub.demo.utility.OccupiedPeriod;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Book {

    private int bookId;
    private List<OccupiedPeriod> occupiedDates; // Elenco dei periodi occupati
    private String username;
    private boolean transactionState;
    private String email;
    private String birthPlace;
    private String address;
    private int addressNumber;
    private String birthDate;
    private String cardNumber;
    @JsonProperty("CVV")
    private int CVV;
    private String expiryDate;
    private String[] guestFirstNames; // Array dei nomi degli ospiti
    private String[] guestLastNames; // Array dei cognomi degli ospiti

    // Costruttore senza parametri
    public Book() {}

    // Costruttore con parametri
    public Book(int bookId, List<OccupiedPeriod> occupiedDates, String username, boolean transactionState, String email,
                String birthPlace, String address, int addressNumber, String birthDate, String cardNumber,
                int CVV, String expiryDate, String[] guestFirstNames, String[] guestLastNames) {
        this.bookId = bookId;
        this.occupiedDates = occupiedDates;
        this.username = username;
        this.transactionState = transactionState;
        this.email = email;
        this.birthPlace = birthPlace;
        this.address = address;
        this.addressNumber = addressNumber;
        this.birthDate = birthDate;
        this.cardNumber = cardNumber;
        this.CVV = CVV;
        this.expiryDate = expiryDate;
        this.guestFirstNames = guestFirstNames;
        this.guestLastNames = guestLastNames;
    }

    // Getters and Setters

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public List<OccupiedPeriod> getOccupiedDates() {
        return occupiedDates;
    }

    public void setOccupiedDates(List<OccupiedPeriod> occupiedDates) {
        this.occupiedDates = occupiedDates;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isTransactionState() {
        return transactionState;
    }

    public void setTransactionState(boolean transactionState) {
        this.transactionState = transactionState;
    }

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCVV() {
        return CVV;
    }

    public void setCVV(int CVV) {
        this.CVV = CVV;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
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

    // Metodo toString
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", occupiedDates=" + occupiedDates +
                ", username='" + username + '\'' +
                ", transactionState=" + transactionState +
                ", email='" + email + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", address='" + address + '\'' +
                ", addressNumber=" + addressNumber +
                ", birthDate='" + birthDate + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", CVV=" + CVV +
                ", expiryDate='" + expiryDate + '\'' +
                ", guestFirstNames=" + Arrays.toString(guestFirstNames) +
                ", guestLastNames=" + Arrays.toString(guestLastNames) +
                '}';
    }
}
