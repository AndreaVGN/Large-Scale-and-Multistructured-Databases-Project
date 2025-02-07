package com.example.WanderHub.demo.service;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.repository.ArchivedReviewRepository;
import com.example.WanderHub.demo.utility.OccupiedPeriod;
import org.bson.types.ObjectId;
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
import com.example.WanderHub.demo.repository.BookingRepository;
import com.example.WanderHub.demo.repository.RegisteredUserRepository;
import com.example.WanderHub.demo.utility.Validator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
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
    private RedissonClient redissonClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private BookingService bookingService;

    @Autowired
    private ArchivedReviewRepository archivedReviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    public AccommodationService(AccommodationRepository accommodationRepository, RegisteredUserRepository registeredUserRepository) {
        this.registeredUserRepository = registeredUserRepository;
    }

    public boolean createAccommodation(Accommodation accommodation) {
        try {

            Validator.validateAccommodation(accommodation);

            // If all checks pass, save the accommodation in the database
            //return accommodationRepository.save(accommodation);
            return bookingRepository.insertAccommodation(accommodation);

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

    // Eliminazione di una sistemazione
    public boolean deleteAccommodationByDescription(ObjectId accommodationId) {
        if (accommodationRepository.existsByDescription(accommodationId)) {
            accommodationRepository.deleteByDescription(accommodationId);
            return true;
        }
        return false;
    }


    public List<AccommodationDTO> findAvailableAccommodations(String place, int minGuests, String startDate, String endDate) {
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

            // Esegui la query che restituisce le Accommodation
            List<Accommodation> accommodations = accommodationRepository.findAvailableAccommodations(place, minGuests, start, end);

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

    public List<AccommodationDTO> getReviewsByUsername(String username) {
        try {
            List<Accommodation> reviews = accommodationRepository.findReviewsByUsername(username);
            return reviews.stream()
                    .map(AccommodationDTO::withReviews)
                    .collect(Collectors.toList());
        }
        catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving reviews from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving reviews from the database: ", e);
        }


    }
    
    public List<AccommodationDTO> getPendingBookings(String username) {
        try {
            // Ottieni BookDTO dal repository
            //return accommodationRepository.findPendingBookingsByUsername(username);
            List<Accommodation> books = accommodationRepository.findPendingBookingsByUsername(username);
            return books.stream()
                    .map(AccommodationDTO::fromSomeInfo)
                    .collect(Collectors.toList());
        }
        catch (DataAccessException e) {
            throw new RuntimeException("Error while retrieving pending bookings from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving pending bookings from the database: ", e);
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


    public List<Book> viewAccommodationBooks(String hostUsername, int id){
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


    public List<ReviewDTO> viewAccommodationReviews(String hostUsername, int id) {
        try {
            // Recupera la lista di Accommodation corrispondente alla query
            return accommodationRepository.viewAccommodationReviews(hostUsername, id);
        }
        catch(DataAccessException e){
            throw new RuntimeException("Error while retrieving accommodation from the database: " + e.getMessage(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error while retrieving accommodation: ", e);
        }
    }

    public Accommodation addBookToAccommodation(String username, ObjectId accommodationId, Book newBook) {
        try {
            LocalDate start = newBook.getStartDate();
            LocalDate end = newBook.getEndDate();

            int aux = accommodationRepository.checkAvailability(accommodationId, start, end);
            if (aux > 0) {
                throw new IllegalArgumentException("Accommodation " + accommodationId + " is not available for the selected period.");
            }

            Validator.validateBook(newBook);

            // Recupera l'accommodation dal database
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            // Controlla che l'utente non sia il proprietario dell'alloggio
            if (accommodation.getHostUsername().equals(username)) {
                throw new RuntimeException("Host cannot book their own accommodation.");
            }

            // Controlla se l'utente ha già una prenotazione nello stesso periodo
            String bookingKey = "booking:accId:" + accommodationId + ":start:" + start + ":end:" + end;
            String existingBooking = (String) redisTemplate.opsForValue().get(bookingKey);
            System.out.println(existingBooking);
            if (!username.equals(existingBooking)) {
                throw new RuntimeException("pERIODO DIVERSO PER QUESTO UTENTE.");
            }

            // Imposta il nome utente sulla prenotazione
            newBook.setUsername(username);

            // **1. Aggiungi la prenotazione alla lista delle books dell'accommodation**
            accommodation.getBooks().add(newBook);

            // **2. Aggiorna il campo occupiedDates dell'accommodation con il nuovo periodo occupato**
            if (accommodation.getOccupiedDates() == null) {
                accommodation.setOccupiedDates(new ArrayList<>());
            }
            accommodation.getOccupiedDates().add(new OccupiedPeriod(start, end));

            // Salva l'accommodation aggiornata nel database
            return accommodationRepository.save(accommodation);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }



    public boolean deleteBook(String username, ObjectId accommodationId, LocalDate startDate, LocalDate endDate) {
        try {
            // Retrieve the accommodation by its ID
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            // Retrieve the customer user who is performing the cancellation
            RegisteredUser customer = registeredUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Find the booking to be deleted
            Book bookToDelete = accommodation.getBooks().stream()
                    .filter(book -> book.getStartDate().equals(startDate) &&
                            book.getEndDate().equals(endDate) &&
                            book.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Booking not found"));


            // Calculate the difference in days between the booking start date and today
            long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), bookToDelete.getStartDate());
            System.out.println(daysUntilStart);

            if (daysUntilStart >= 2) {
                // Remove the booking from the list and update the accommodation
                accommodation.getBooks().remove(bookToDelete);
                accommodationRepository.save(accommodation);
                return true;
            } else {
                throw new IllegalArgumentException("Booking cannot be canceled as it exceeds the allowed cancellation period.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting the booking: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Validation error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while deleting the booking: " + e.getMessage(), e);
        }
    }

    public List<FacilityRatingDTO> getAverageRatingByFacility(String city) {
        return accommodationRepository.getAverageRatingByFacilityInCity(city);
    }

    public List<AverageCostDTO> viewAvgCostPerNight(String city) {
           return  accommodationRepository.findAverageCostPerNightByCityAndGuests(city);
    }
    public Accommodation addReviewToAccommodation(String username, ObjectId accommodationId, Review review) {
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new RuntimeException("Accommodation not found"));

        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3); // Sottrarre 3 giorni da oggi
        LocalDate date = calendar.getTime();
        System.out.println(date);*/
        String text = bookingRepository.existsBozzaText(username,accommodationId);
        String rating = bookingRepository.existsBozzaRating(username,accommodationId);
        if(text!=null && rating!=null && review.getReviewText()==null) {
            review.setReviewText(text);
            review.setRating(Double.parseDouble(rating));
        }
        review.setDate(LocalDate.now());
        /*LocalDate aux = review.getDate();
        if(aux.isBefore(LocalDate.now())) {
            throw new RuntimeException("you cannot review an accommodation before the end of the stay");
        }*/
        LocalDate date = review.getDate().minusDays(3);
        System.out.println(date);
        LocalDate today = LocalDate.now();
        if (!accommodationRepository.existsBookingForUser(accommodationId, username, date, today)) {
            throw new RuntimeException("User has not booked this accommodation within 3 days before");
        }

        accommodation.getReviews().add(review);
        return accommodationRepository.save(accommodation);
    }
    public boolean addBozzaToAccommodation(String username, ObjectId accommodationId, Review review) {
        Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                .orElseThrow(() -> new RuntimeException("Accommodation not found"));

        /*LocalDate aux = review.getDate();
        if(aux.isBefore(LocalDate.now())) {
            throw new RuntimeException("you cannot review an accommodation before the end of the stay");
        }*/
        review.setDate(LocalDate.now());
        LocalDate date = review.getDate().minusDays(3);
        System.out.println(date);
        LocalDate today = LocalDate.now();
        if (!accommodationRepository.existsBookingForUser(accommodationId, username, date, today)) {
            throw new RuntimeException("User has not booked this accommodation within 3 days before");
        }
        return bookingRepository.addBozza(username,accommodationId,review);
    }



    //@Scheduled(cron = "0 0 3 * * ?") // Ogni giorno alle 03:00 AM
   // @PostConstruct
    public void updateAverageRates() {
        List<AccommodationAverageRate> averages = archivedReviewRepository.calculateAverageRatesForAllAccommodations();

        for (AccommodationAverageRate avg : averages) {

            Query query = new Query();
            query.addCriteria(Criteria.where("accommodationId").is(avg.get_id()));

            Update update = new Update().set("averageRate", avg.getAverageRate());

            mongoTemplate.updateFirst(query, update, Accommodation.class);
        }

        System.out.println("Aggiornamento completato.");
    }


}

