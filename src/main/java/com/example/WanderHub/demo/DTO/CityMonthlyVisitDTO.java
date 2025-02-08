package com.example.WanderHub.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CityMonthlyVisitDTO {
    private String city;
    private int month;
    private long visitCount;


}

