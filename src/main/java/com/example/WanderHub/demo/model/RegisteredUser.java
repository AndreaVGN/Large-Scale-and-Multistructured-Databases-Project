package com.example.WanderHub.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "registeredUser")
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
}
