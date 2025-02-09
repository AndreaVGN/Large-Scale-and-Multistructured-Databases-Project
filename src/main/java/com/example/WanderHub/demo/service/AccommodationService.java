package com.example.WanderHub.demo.service;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.ArchivedReviewRepository;
import com.example.WanderHub.demo.repository.BookRepository;
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import com.example.WanderHub.demo.utility.RedisUtility;
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
    private RedisUtility redisUtility;

    private static final long accommodationTTL = 86400;

    @Autowired
    public AccommodationService(AccommodationRepository accommodationRepository, RegisteredUserRepository registeredUserRepository) {
        this.registeredUserRepository = registeredUserRepository;
    }

    // Insert new accommodation into Redis
    public void createAccommodation(Accommodation accommodation) {
        try {

            Validator.validateAccommodation(accommodation);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String accommodationKey = "accommodation:" + timestamp;

            redisUtility.saveAccommodation(accommodation,accommodationKey,accommodationTTL);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while saving accommodation to the database: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating accommodation: " + e.getMessage(), e);
        }
    }

    // Return the correspondent accommodation if exists
    public AccommodationDTO getAccommodationById(ObjectId accommodationId) {
        try {
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found with id: " + accommodationId));

            return AccommodationDTO.fromFullDetails(accommodation);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: " + e.getMessage(), e);
        }
    }


    // Return the accommodations which fullfill the parameters
    public List<AccommodationDTO> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate, int pageNumber) {
        try {
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

            List<Accommodation> accommodations = accommodationRepository.findAvailableAccommodations(place, minGuests, start, end, pageable);

            return accommodations.stream()
                    .map(AccommodationDTO::fromLimitedInfo)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD format", e);
        }catch (DataAccessException e) {
            throw new RuntimeException("Error while getting available accommodations from database: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while searching for available accommodations.", e);
        }
    }


    // Return the accommodations possessed by a host
    public List<AccommodationDTO> findOwnAccommodations(String hostUsername) {
        try {
            List<Accommodation> accommodations = accommodationRepository.findOwnAccommodations(hostUsername);

            return accommodations.stream()
                    .map(AccommodationDTO::idDescription)
                    .collect(Collectors.toList());
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }

    // Return the corresponding books given an accommodation ID e a host username
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


    // Analytic: For each facility, return the average rate of accommodations which have that facility
    public List<FacilityRatingDTO> getAverageRatingByFacility(String city) {
        try {
            return accommodationRepository.getAverageRatingByFacilityInCity(city);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching average rating by facility for city: " + city, e);
        }
    }


    // Analytic: Groups accommodations in a given city based on their guest capacity
    //           and returns the average cost per night for each accommodation type.
    public List<AverageCostDTO> viewAvgCostPerNight(String city) {
        try {
            return accommodationRepository.findAverageCostPerNightByCityAndGuests(city);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching average cost per night for city: " + city, e);
        }
    }


}

