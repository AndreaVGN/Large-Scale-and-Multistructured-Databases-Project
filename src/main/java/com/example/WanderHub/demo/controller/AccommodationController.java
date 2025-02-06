package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.*;
import com.example.WanderHub.demo.service.ArchivedBookingService;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.BookingService;
import com.example.WanderHub.demo.utility.SessionUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/accommodations")
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ArchivedBookingService ArchivedBookingService;

    @PostMapping("/{username}")
    public ResponseEntity<?> createAccommodation(@PathVariable String username, @RequestBody Accommodation accommodation, HttpSession session) {
        // Controlla se l'utente nella sessione è lo stesso che è nel path
        String usernam = (String) session.getAttribute("user");

        if (usernam == null || !usernam.equals(username)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Logica per creare l'accommodation
        accommodationService.createAccommodation(accommodation);
        return ResponseEntity.ok("Accommodation created successfully");
    }


    @GetMapping("/{id}")
    public AccommodationDTO getAccommodationById(@PathVariable ObjectId id) {
        return accommodationService.getAccommodationById(id);
   }


    @GetMapping("/findAccommodations")
    public List<AccommodationDTO> findAccommodations(
            @RequestParam("city") String place,
            @RequestParam("guestSize") int minGuests,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {


        return accommodationService.findAvailableAccommodations(place, minGuests, startDate, endDate);
    }


    @GetMapping("/{hostUsername}/viewOwnAccommodations")
    public ResponseEntity<?> viewOwnAccommodations(@PathVariable String hostUsername, HttpSession session) {

        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");

        if (loggedInUser == null || !loggedInUser.getUsername().equals(hostUsername)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }
        List<AccommodationDTO> accommodations = accommodationService.findOwnAccommodations(hostUsername);
        if (accommodations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna accommodation trovata per questo host.");
        }

        return ResponseEntity.ok(accommodations);
    }

    @GetMapping("/{hostUsername}/viewAccommodationBooks/{id}")
    public ResponseEntity<?> viewAccommodationBooks(@PathVariable String hostUsername, @PathVariable int id, HttpSession session) {
        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");


        if (loggedInUser == null || !loggedInUser.getUsername().equals(hostUsername)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }
        List<Book> books = accommodationService.viewAccommodationBooks(hostUsername,id);
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna book trovata per questo host.");
        }

        return ResponseEntity.ok(books);
    }

    @GetMapping("/{hostUsername}/viewAccommodationReviews/{id}")
    public ResponseEntity<?> viewAccommodationReviews(@PathVariable String hostUsername, @PathVariable int id, HttpSession session) {
        RegisteredUser loggedInUser = (RegisteredUser) session.getAttribute("user");


        if (loggedInUser == null || !loggedInUser.getUsername().equals(hostUsername)) {
            // Se l'utente non è loggato o non corrisponde, restituisci un errore
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato");
        }

        List<ReviewDTO> reviews = accommodationService.viewAccommodationReviews(hostUsername, id);
        System.out.println(reviews);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nessuna reviews trovata per questo host.");
        }
        return ResponseEntity.ok(reviews);
    }

    /*
    // Endpoint per aggiungere una prenotazione a un'accommodation scelta dal cliente
    @PutMapping("/{accommodationId}/addBook")
    public ResponseEntity<Accommodation> addBookToAccommodation(
            @PathVariable int accommodationId,
            @RequestBody Book newBook) {

        String username = "Unregistered User";
        // Aggiungi la nuova prenotazione alla casa selezionata dall'utente
        Accommodation updatedAccommodation = accommodationService.addBookToAccommodation(username, accommodationId, newBook);

        // Restituisci l'accommodation aggiornata
        return new ResponseEntity<>(updatedAccommodation, HttpStatus.OK);
    }*/


    @PutMapping("/{accommodationId}/addBook")
    public ResponseEntity<?> addBookToAccommodation(
            @PathVariable ObjectId accommodationId,
            @RequestBody Book newBook,
            HttpServletRequest request) {

        // Recupera i cookie dalla richiesta
        Cookie[] cookies = request.getCookies();
        String bookingTimestamp = null;

        // Cerca il cookie con il nome "bookingTimestamp"
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("bookingTimestamp".equals(cookie.getName())) {
                    bookingTimestamp = cookie.getValue(); // Ottieni il valore del bookingTimestamp dal cookie
                    break;
                }
            }
        }
        System.out.println(bookingTimestamp);

        // Verifica se il cookie è stato trovato
        if (bookingTimestamp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Se non trovato, restituisci un errore
        }

        // Aggiungi la nuova prenotazione alla casa selezionata dall'utente
        Accommodation updatedAccommodation = accommodationService.addBookToAccommodation(bookingTimestamp, accommodationId, newBook);


        return new ResponseEntity<>("Prenotazione avvenuta con successo!", HttpStatus.OK);
    }


    @GetMapping("/average-rating/{city}")
    public List<FacilityRatingDTO> getAverageRatingByFacility(@PathVariable String city) {
        return accommodationService.getAverageRatingByFacility(city);
    }
    @GetMapping("/{city}/viewAvgCostPerNight")
    public ResponseEntity<List<AverageCostDTO>> viewAvgCostPerNight(@PathVariable String city){
        return new ResponseEntity<>(accommodationService.viewAvgCostPerNight(city),HttpStatus.OK);
    }

    /*
    @PostMapping("/{accommodationId}/lock")
    public ResponseEntity<String> lockHouse(@PathVariable int accommodationId, @RequestParam String startDate, @RequestParam String endDate) {
        boolean success = bookingService.bookHouse(accommodationId, startDate,endDate);
        if (success) {
            return ResponseEntity.ok("Casa prenotata temporaneamente!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Casa già prenotata da un altro utente.");
        }
    }*/

    @PostMapping("/{accommodationId}/lock")
    public ResponseEntity<String> lockHouse(@PathVariable ObjectId accommodationId, @RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response) {
        String timestamp = bookingService.getBookingTimestamp(accommodationId, startDate, endDate);

        if (timestamp != null) {
            // Crea il cookie con il timestamp
            Cookie timestampCookie = new Cookie("bookingTimestamp", timestamp);
            timestampCookie.setMaxAge(3600); // Imposta un tempo di vita del cookie (modifica a seconda delle necessità)
            timestampCookie.setHttpOnly(true); // Sicurezza per evitare l'accesso tramite JavaScript
            timestampCookie.setPath("/"); // Può essere modificato a seconda delle necessità

            // Aggiungi il cookie alla risposta
            response.addCookie(timestampCookie);

            return ResponseEntity.ok("Casa prenotata temporaneamente!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Casa già prenotata da un altro utente.");
        }
    }

    @DeleteMapping("/{accommodationId}/unlock")
    public ResponseEntity<String> unlockHouse(
            @PathVariable ObjectId accommodationId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @CookieValue(value = "bookingTimestamp", required = false) String timestampCookie) {

        if (timestampCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nessun timestamp di prenotazione trovato.");
        }

        boolean unlocked = bookingService.unlockHouse(accommodationId, startDate, endDate, timestampCookie);

        if (unlocked) {
            return ResponseEntity.ok("Casa sbloccata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Errore nello sblocco della casa.");
        }
    }

    @GetMapping("/top-cities")
    public List<CityBookingRankingDTO> getTopCities() {
        return ArchivedBookingService.getTopCities();
    }

    @GetMapping("/average-age/{city}")
    public List<CityAverageAgeDTO> getAverageAgeByCity(@PathVariable String city) {
        return ArchivedBookingService.getAverageAgeByCity(city);
    }

    @GetMapping("/top-cities-price-range")
    public List<CityBookingRankingDTO> getTopCitiesByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        // Chiamata al service per ottenere la classifica
        return ArchivedBookingService.getTopCitiesByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/city/{city}/monthly-visits")
    public List<CityMonthlyVisitDTO> getMonthlyVisits(@PathVariable String city) {
        return ArchivedBookingService.getMonthlyVisitsByCity(city);
    }

}

