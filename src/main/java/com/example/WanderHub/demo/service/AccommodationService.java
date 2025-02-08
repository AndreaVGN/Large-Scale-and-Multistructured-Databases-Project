package com.example.WanderHub.demo.service;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.ArchivedReviewRepository;
import com.example.WanderHub.demo.repository.BookRepository;
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.data.redis.core.RedisTemplate;
import com.example.WanderHub.demo.model.Review;
import com.example.WanderHub.demo.exception.ResourceNotFoundException;
import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.repository.AccommodationRepository;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import com.example.WanderHub.demo.utility.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    private final RegisteredUserRepository registeredUserRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BookService bookService;

    @Autowired
    private ArchivedReviewRepository archivedReviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    public AccommodationService(AccommodationRepository accommodationRepository, RegisteredUserRepository registeredUserRepository) {
        this.registeredUserRepository = registeredUserRepository;
    }

    public void createAccommodation(Accommodation accommodation) {
        try {

            Validator.validateAccommodation(accommodation);

            // If all checks pass, save the accommodation in the database
            //return accommodationRepository.save(accommodation);
             bookService.insertAccommodation(accommodation);

        } catch (IllegalArgumentException e) {
            // Re-throw the exception to notify the controller
            throw e;
        } catch (DataAccessException e) {
            // Handle database-related errors (connection, query, etc.)
            throw new RuntimeException("Error while saving accommodation to the database: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle other generic errors
            throw new RuntimeException("Error while creating accommodation: " + e.getMessage(), e);
        }
    }

    public AccommodationDTO getAccommodationById(ObjectId accommodationId) {
        try {
            // Trova la sistemazione esistente
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));

            // Use the factory method to create a DTO with complete data (excluding books)
            return AccommodationDTO.fromFullDetails(accommodation);
        } catch (DataAccessException e) {
            // Handles database-related errors (connection, query, etc.)
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handles other generic errors
            throw new RuntimeException("Error while retrieving accommodation: " + e.getMessage(), e);
        }
    }



    public List<AccommodationDTO> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate, int pageNumber) {
        try {
            // Validazione dei parametri
            if (minGuests <= 0) {
                throw new IllegalArgumentException("guestSize must be greater than zero.");
            }
            if(place==null || place.trim().isEmpty()){
                throw new IllegalArgumentException("place cannot be null.");
            }

            if (startDate == null || startDate.trim().isEmpty()) {
                throw new IllegalArgumentException("startDate cannot be empty.");
            }
            if (endDate == null || endDate.trim().isEmpty()) {
                throw new IllegalArgumentException("endDate cannot be empty.");
            }

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (end.isBefore(start)) {
                throw new IllegalArgumentException("endDate cannot be before startDate.");
            }

            Pageable pageable = PageRequest.of(pageNumber, 100, Sort.by(Sort.Direction.DESC, "averageRate"));

            // Esegui la query che restituisce le Accommodation
            List<Accommodation> accommodations = accommodationRepository.findAvailableAccommodations(place, minGuests, start, end, pageable);

            // Mappa le Accommodation in DTO utilizzando il factory method
            return accommodations.stream()
                    .map(AccommodationDTO::fromLimitedInfo)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD format", e);
        }catch (DataAccessException e) {
            // Handle database-related errors (connection, query, etc.)
            throw new RuntimeException("Error while getting available accommodations from database: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while searching for available accommodations.", e);
        }
    }



    public List<AccommodationDTO> findOwnAccommodations(String hostUsername) {
        try {
            // Esegui la query per ottenere le accommodations del proprietario
            List<Accommodation> accommodations = accommodationRepository.findOwnAccommodations(hostUsername);

            // Mappa le accommodations in DTO utilizzando il factory method
            return accommodations.stream()
                    .map(AccommodationDTO::fromBasicInfo)
                    .collect(Collectors.toList());
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }


    public List<Book> viewAccommodationBooks(String hostUsername, String id){
        try {
            List<BookDTO> booksDTOList = accommodationRepository.viewAccommodationBooks(hostUsername, id);

            return booksDTOList.stream()
                    .flatMap(dto -> dto.getBooks().stream())
                    .collect(Collectors.toList());
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }






    public List<FacilityRatingDTO> getAverageRatingByFacility(String city) {
        return accommodationRepository.getAverageRatingByFacilityInCity(city);
    }

    public List<AverageCostDTO> viewAvgCostPerNight(String city) {
           return  accommodationRepository.findAverageCostPerNightByCityAndGuests(city);
    }





    //@Scheduled(cron = "0 0 3 * * ?") // Ogni giorno alle 03:00 AM
   // @PostConstruct
    public void updateAverageRates() {
        List<AccommodationAverageRateDTO> averages = archivedReviewRepository.calculateAverageRatesForAllAccommodations();

        for (AccommodationAverageRateDTO avg : averages) {

            Query query = new Query();
            query.addCriteria(Criteria.where("accommodationId").is(avg.get_id()));

            Update update = new Update().set("averageRate", avg.getAverageRate());

            mongoTemplate.updateFirst(query, update, Accommodation.class);
        }

        System.out.println("Aggiornamento completato.");
    }


}

