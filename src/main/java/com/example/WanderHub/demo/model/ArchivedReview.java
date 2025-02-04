package com.example.WanderHub.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "archivedReviews") // Nome della collection in MongoDB
public class ArchivedReview {

    @Id
    private String reviewId;
    private String accommodationId; // Aggiunto per mantenere il riferimento all'alloggio
    private String username;
    private int rate;
    private String reviewText;
    private Date date; // Data della recensione archiviata

}
