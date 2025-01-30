package com.example.WanderHub.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class RegisteredUser {
    @Id
    private String username;
    private String password;
    private String name;
    private String surname;
    private String birthPlace;
    private String email;
    private String birthDate; // Campo per la data di nascita
    private String address;
    private int addressNumber;
    private String paymentToken;
    /*private List<Integer> accommodations;

    @Field("books") // Embedding the books array inside RegisteredUser
    private List<Book> books;*/

    // Costruttore vuoto
    public RegisteredUser() {
    }

    // Costruttore con un parametro (esempio)
    public RegisteredUser(String username) {
        this.username = username;
    }

    // Costruttore completo
    public RegisteredUser(String username, String password,String name, String surname, String birthPlace, String email, String birthDate,
                          String address, int addressNumber, String paymentToken) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.birthPlace = birthPlace;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.addressNumber = addressNumber;
        this.paymentToken = paymentToken;
        //this.accommodations = accommodations;
        //this.books = books;
    }

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    /*public List<Integer> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(List<Integer> accommodations) {
        this.accommodations = accommodations;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }*/

    // Override del metodo toString
    @Override
    public String toString() {
        return "RegisteredUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", addressNumber=" + addressNumber +
                ", paymentToken='" + paymentToken +
                //", accommodations=" + accommodations +
                //", books=" + books +
                '}';
    }
}
