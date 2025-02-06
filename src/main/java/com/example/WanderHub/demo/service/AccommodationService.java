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

    public Accommodation createAccommodation(Accommodation accommodation) {
        try {

            Validator.validateAccommodation(accommodation);

            // If all checks pass, save the accommodation in the database
            return accommodationRepository.save(accommodation);

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


    // Metodo per aggiungere una prenotazione alla casa scelta dal cliente
    /*public Accommodation addBookToAccommodation(String username, String description, Book newBook) {
        try {


            Validator.validateBook(newBook);

            // Recupera l'accommodation tramite il suo ID
            Accommodation accommodation = accommodationRepository.findByAccommodationId(description)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            // Recupera l'utente cliente che sta facendo la prenotazione
            RegisteredUser customer = registeredUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Definisci la chiave per il lock in Redis
            String lockKey = "booking_lock:" + description + ":" + newBook.getStartDate();  // Usa una chiave univoca (per esempio in base all'description e alla data di inizio)

            // Ottieni il lock distribuito con Redisson
            RLock lock = redissonClient.getLock(lockKey);

            // Prova ad acquisire il lock (per esempio per 30 secondi con una scadenza di 10 secondi)
            boolean isLockAcquired = lock.tryLock(30, 10, TimeUnit.SECONDS);

            if (!isLockAcquired) {
                throw new RuntimeException("The accommodation is already locked by another user or is in the process of booking.");
            }

            // Se il lock è acquisito, procedi con la prenotazione
            try {
                // Aggiungi il nome dell'utente (cliente) alla prenotazione
                newBook.setUsername(username);

                // Aggiungi la nuova prenotazione alla lista delle prenotazioni della casa
                accommodation.getBooks().add(newBook);

                // Salva l'accommodation aggiornata nel database
                return accommodationRepository.save(accommodation);
            } finally {
                // Rilascia il lock una volta completata l'operazione
                lock.unlock();
            }

        } catch (Exception e) {
            // Gestisci qualsiasi errore, inclusi lock non acquisiti e altre eccezioni
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }*/

    /*public Accommodation addBookToAccommodation(String username, ObjectId accommodationId, Book newBook) {
        try {

            LocalDate start = newBook.getStartDate();
            LocalDate end = newBook.getEndDate();
            System.out.println(start);
            System.out.println(end);
            int aux = accommodationRepository.checkAvailability(accommodationId,start,end);
            System.out.println(aux);
            if(aux>0){
                throw new IllegalArgumentException("description " + accommodationId + " is not available.");
            }
            Validator.validateBook(newBook);

            // Recupera l'accommodation tramite il suo ID
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));


            if (accommodation.getHostUsername().equals(username)) {
                throw new RuntimeException("Host cannot book their own accommodation.");
            }
            // Definisci la chiave di prenotazione in Redis
            String bookingKey = "booking:" + accommodationId + ":" + newBook.getStartDate() + ":" + newBook.getEndDate();

            System.out.println(username);
            // Controlla se esiste già una prenotazione con lo stesso periodo e username
            String existingBooking = (String) redisTemplate.opsForValue().get(bookingKey);
            System.out.println(existingBooking);
            if (!username.equals(existingBooking)) {
                throw new RuntimeException("User already has a booking for this accommodation in the selected period.");
            }

            // Aggiungi il nome dell'utente alla prenotazione
            newBook.setUsername(username);

            // Aggiungi la prenotazione alla lista delle prenotazioni dell'alloggio
            accommodation.getBooks().add(newBook);

            // Salva l'accommodation aggiornata nel database
            Accommodation savedAccommodation = accommodationRepository.save(accommodation);


            return savedAccommodation;

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while adding the booking: " + e.getMessage(), e);
        }
    }*/
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
                throw new RuntimeException("User already has a booking for this accommodation in the selected period.");
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


/*
    public boolean deleteBook(String username, ObjectId accommodationId, int bookId) {
        try {
            // Retrieve the accommodation by its ID
            Accommodation accommodation = accommodationRepository.findByAccommodationId(accommodationId)
                    .orElseThrow(() -> new RuntimeException("Accommodation not found"));

            // Retrieve the customer user who is performing the cancellation
            RegisteredUser customer = registeredUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Find the booking to be deleted
            Book bookToDelete = accommodation.getBooks().stream()
                    .filter(book -> book.getBookId() == bookId && book.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Calculate the difference in days between the booking start date and today
            long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), bookToDelete.getStartDate());

            if (daysUntilStart <= 2) {
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
    }*/

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
            //System.out.println(avg.get_id());
            //System.out.println(avg.getAverageRate());
            /*if(avg.get_id().equals("67a492d4acacc96805400d35")){
                System.out.println("trovato");
                System.out.println(avg.getAverageRate());
            }*/
            Query query = new Query();
            query.addCriteria(Criteria.where("accommodationId").is(avg.get_id()));

            Update update = new Update().set("averageRate", avg.getAverageRate());

            mongoTemplate.updateFirst(query, update, Accommodation.class);
        }

        System.out.println("Aggiornamento completato.");
    }


}

