package com.example.WanderHub.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FacilityRatingDTO {
    private String facility;
    private String city;
    private double averageRating;
}
