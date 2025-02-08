package com.example.WanderHub.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CityBookingRankingDTO {
    private String city;
    private long bookingCount;

}
