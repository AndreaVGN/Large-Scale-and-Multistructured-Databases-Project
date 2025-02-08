package com.example.WanderHub.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AverageCostDTO {
    private String city;
    private int guestCount;
    private double averageCostPerNight;


}

