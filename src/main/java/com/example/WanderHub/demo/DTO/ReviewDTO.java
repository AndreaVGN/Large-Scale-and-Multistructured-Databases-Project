package com.example.WanderHub.demo.DTO;

import java.util.List;
import com.example.WanderHub.demo.model.Review;  // Assumendo che Review sia un modello esistente
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDTO {

    private List<Review> reviews;  // Lista di recensioni
}

