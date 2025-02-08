package com.example.WanderHub.demo.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Review {
    private String reviewText;
    private double rating;
    private String username;
    private LocalDate date;
}
