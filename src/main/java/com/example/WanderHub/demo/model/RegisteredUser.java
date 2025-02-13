package com.example.WanderHub.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "registeredUsers")
public class RegisteredUser {

    @Id
    private String username;
    private String password;
    private String name;
    private String surname;
    private String birthPlace;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String birthDate; // Campo per la data di nascita
    private String address;
    private int addressNumber;

    private List<PendingBook> books;

    public RegisteredUser() {
    }

    public RegisteredUser(String username) {
        this.username = username;
    }

    public RegisteredUser(String username, String password, String name, String surname, String birthPlace, String email,
                          String birthDate, String address, int addressNumber, List<PendingBook> books) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.birthPlace = birthPlace;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.addressNumber = addressNumber;
        this.books = books;
    }

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public List<PendingBook> getBooks() {
        return books;
    }

    public void setBooks(List<PendingBook> books) {
        this.books = books;
    }

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
                ", books=" + books +
                '}';
    }
}
