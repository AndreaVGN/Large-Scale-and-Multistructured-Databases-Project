package com.example.WanderHub.demo.service;

import com.example.WanderHub.demo.DTO.ReviewDTO;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.ReviewRepository;
import com.example.WanderHub.demo.utility.RedisUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;



@Service
public class ReviewService {

    @Autowired
    private ReviewRepository ReviewRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RedisUtility redisUtility;

    private static final long reviewTTL = 21600; // 6 hours

    public Review createReview(Review review) {
        return ReviewRepository.save(review);
    }

    // Return the accommodation reviews selected by the correspondent host username
    public List<ReviewDTO> viewAccommodationReviews(String hostUsername, int id) {
        try {
            return accommodationRepository.viewAccommodationReviews(hostUsername, id);
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }

    // Insert a new review to a specified accommodation: if a draft review were previously written it will be send as the review
    // Note: an username can write a review within 3 days after the finish of the book.
    public Accommodation addReviewToAccommodation(String username, String accommodationId, Review review) {
        try {
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            ObjectMapper objectMapper = new ObjectMapper();
            String json = redisUtility.getValue("wanderhub:review:accId:" + accommodationId + ":username:" + username);
            Review draftReview = objectMapper.readValue(json, Review.class);
            String text = draftReview.getReviewText();
            Double rate = draftReview.getRating();

            if (text != null && rate != null && review.getReviewText() == null) {
                review.setReviewText(text);
                review.setRating(rate);
            }
            review.setDate(LocalDate.now());
            LocalDate date = review.getDate().minusDays(3);
            LocalDate today = LocalDate.now();
            if (!accommodationRepository.existsBookingForUser(accommodationId, username, date, today)) {
                throw new RuntimeException("User has not booked this accommodation within 3 days before");
            }
            accommodation.getReviews().add(review);
            return accommodationRepository.save(accommodation);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save review", e); // Rilancia l'errore
        }
    }

    // Insert a draft review in Redis
    // Note: an username can write a draft review within 3 days after the finish of the book.
    // The draft review lasts 6 hours in Redis.
    public void addDraftReviewToAccommodation(String username, String accommodationId, Review review) {
        try {
              accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            review.setDate(LocalDate.now());
            LocalDate date = review.getDate().minusDays(3);
            LocalDate today = LocalDate.now();

            if (!accommodationRepository.existsBookingForUser(accommodationId, username, date, today)) {
                throw new RuntimeException("User has not booked this accommodation within 3 days before");
            }

            // Salva in Redis
            redisUtility.saveDraftReview("wanderhub:review:accId:" + accommodationId + ":username:" + username, review, reviewTTL);


        } catch (RuntimeException e) {

            throw e;
        }
    }


}
