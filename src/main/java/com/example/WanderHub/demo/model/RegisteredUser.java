package com.example.WanderHub.demo.model;

import java.time.LocalDate;

public class RegisteredUser {
    private String username;
    private String password;
    private String birthPlace;
    private String email;
    private LocalDate birthDate; // Campo per la data di nascita
    private String address;
    private int addressNumber;
    private String paymentToken;

    // Costruttore vuoto
    public RegisteredUser() {
    }

    // Costruttore con un parametro (esempio)
    public RegisteredUser(String username) {
        this.username = username;
    }

    // Costruttore completo
    public RegisteredUser(String username, String password, String birthPlace, String email, LocalDate birthDate,
                          String address, int addressNumber, String paymentToken) {
        this.username = username;
        this.password = password;
        this.birthPlace = birthPlace;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.addressNumber = addressNumber;
        this.paymentToken = paymentToken;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
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

    // Override del metodo toString
    @Override
    public String toString() {
        return "RegisteredUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", addressNumber=" + addressNumber +
                ", paymentToken='" + paymentToken + '\'' +
                '}';
    }
}