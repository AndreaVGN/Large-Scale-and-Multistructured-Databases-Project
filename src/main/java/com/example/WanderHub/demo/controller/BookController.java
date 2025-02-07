package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.ArchivedBookService;
import com.example.WanderHub.demo.service.BookService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private ArchivedBookService archivedBookService;

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @GetMapping("/filter")
    public List<Book> getBooksByCityAndPeriod(
            @RequestParam String city,
            @RequestParam String period) {
        return bookService.getBooksByCityAndPeriod(city, period);
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
        accommodationService.addBookToAccommodation(bookingTimestamp, accommodationId, newBook);


        return new ResponseEntity<>("Prenotazione avvenuta con successo!", HttpStatus.OK);
    }


    @PostMapping("/{accommodationId}/lock")
    public ResponseEntity<String> lockHouse(@PathVariable ObjectId accommodationId, @RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response) {
        String timestamp = bookService.lockHouse(accommodationId, startDate, endDate);

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

        boolean unlocked = bookService.unlockHouse(accommodationId, startDate, endDate, timestampCookie);

        if (unlocked) {
            return ResponseEntity.ok("Casa sbloccata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Errore nello sblocco della casa.");
        }
    }

    @GetMapping("/top-cities")
    public List<CityBookingRankingDTO> getTopCities() {
        return archivedBookService.getTopCities();
    }

    @GetMapping("/average-age/{city}")
    public List<CityAverageAgeDTO> getAverageAgeByCity(@PathVariable String city) {
        return archivedBookService.getAverageAgeByCity(city);
    }

    @GetMapping("/top-cities-price-range")
    public List<CityBookingRankingDTO> getTopCitiesByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        // Chiamata al service per ottenere la classifica
        return archivedBookService.getTopCitiesByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/city/{city}/monthly-visits")
    public List<CityMonthlyVisitDTO> getMonthlyVisits(@PathVariable String city) {
        return archivedBookService.getMonthlyVisitsByCity(city);
    }

    @GetMapping("/{city}/avgHolidayDuration")
    public AverageBookingResult findAverageBookingDurationByCity(@PathVariable String city){
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("La città non può essere null o vuota");
        }
        return archivedBookService.findAverageBookingDurationByCity(city);
    }
    @GetMapping("/{city}/mostCommonBirthPlace")
    public BirthPlaceFrequency getMostCommonBirthPlaceByCity(@PathVariable String city){
        return archivedBookService.findMostCommonBirthPlaceByCity(city);
    }



}
