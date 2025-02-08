package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.DTO.*;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.ArchivedBookService;
import com.example.WanderHub.demo.service.BookService;
import com.example.WanderHub.demo.utility.SessionUtilility;
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

    /*
    @GetMapping("/filter")
    public List<Book> getBooksByCityAndPeriod(
            @RequestParam String city,
            @RequestParam String period) {
        return bookService.getBooksByCityAndPeriod(city, period);
    }*/

    @GetMapping("/{hostUsername}/viewAccommodationBooks/{id}")
    public ResponseEntity<?> viewAccommodationBooks(@PathVariable String hostUsername, @PathVariable int id, HttpSession session) {
        if (!SessionUtilility.isLogged(session, hostUsername)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        List<Book> books = accommodationService.viewAccommodationBooks(hostUsername,id);

        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No book found for this host");
        }

        return ResponseEntity.ok(books);
    }

    // Endpoint per aggiungere una prenotazione a un'accommodation scelta dal cliente
    @PutMapping("/{username}/{accommodationId}/addBook/")
    public ResponseEntity<?> addBookToAccommodationRegistered(
            @PathVariable String username,
            @PathVariable ObjectId accommodationId,
            @RequestBody Book newBook,
            HttpSession session) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        newBook.setUsername((String) session.getAttribute("user"));
        newBook.setEmail((String) session.getAttribute("email"));
        newBook.setBirthPlace((String) session.getAttribute("birthPlace"));
        newBook.setAddress((String) session.getAttribute("address"));
        newBook.setAddressNumber((int) session.getAttribute("addressNumber"));
        newBook.setBirthDate((String) session.getAttribute("birthDate"));

        bookService.addBookToAccommodation(username, accommodationId, newBook);

        return new ResponseEntity<>("Booking successfully completed!", HttpStatus.OK);

    }

    @PutMapping("/{accommodationId}/addBook")
    public ResponseEntity<?> addBookToAccommodationUnregistered(
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

        // Verifica se il cookie è stato trovato
        if (bookingTimestamp == null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Se non trovato, restituisci un errore
        }

        // Aggiungi la nuova prenotazione alla casa selezionata dall'utente
        bookService.addBookToAccommodation(bookingTimestamp, accommodationId, newBook);

        return new ResponseEntity<>("Prenotazione avvenuta con successo!", HttpStatus.OK);
    }

    @PostMapping("/{accommodationId}/lock")
    public ResponseEntity<String> lockHouseUnregistered(@PathVariable ObjectId accommodationId, @RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response) {

        String timestamp = (String) bookService.lockHouse(accommodationId, startDate, endDate, null);

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

    @PostMapping("/{username}/{accommodationId}/lock")
    public ResponseEntity<String> lockHouseRegistered(@PathVariable ObjectId accommodationId, @PathVariable String username, @RequestParam String startDate, @RequestParam String endDate, HttpSession session) {
        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        boolean success = (boolean) bookService.lockHouse(accommodationId, startDate, endDate, username);
        if (success) {
            return ResponseEntity.ok("Accommodation temporarily booked");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Casa già prenotata da un altro utente. Oppure periodo di tempo non valido");
        }
    }


    @DeleteMapping("/{accommodationId}/unlock")
    public ResponseEntity<String> unlockHouseUnregistered(
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


    @GetMapping("/{username}/pendingBooks")
    public ResponseEntity<?> getPendingBooks(@PathVariable String username, HttpSession session) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        List<AccommodationDTO> pendingBookings = bookService.getPendingBookings(username);

        if (pendingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No pending books found for this username");
        }

        return ResponseEntity.ok(pendingBookings);
    }

    @DeleteMapping("/{username}/accommodation/{accommodationId}/deleteBook")
    public ResponseEntity<String> deleteBook(
            @PathVariable String username,
            @PathVariable ObjectId accommodationId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            HttpSession session
    ) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        boolean isDeleted = bookService.deleteBook(username, accommodationId, startDate, endDate);

        if (isDeleted) {
            return ResponseEntity.ok("Booking cancelled successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("It's not possibile to delete this booking. Check that it exists or that its date is not closer than two days to the start of the booking");
        }
    }


    @DeleteMapping("/{username}/{accommodationId}/unlock")
    public ResponseEntity<String> unlockHouseRegistered(
            @PathVariable ObjectId accommodationId,
            @PathVariable String username,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session) {

        if (!SessionUtilility.isLogged(session, username)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        boolean success = bookService.unlockHouse(accommodationId, startDate, endDate, username);

        if (success) {
            return ResponseEntity.ok("Casa sbloccata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Errore nello sblocco della casa o la prenotazione non esiste.");
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
    public AverageBookingResultDTO findAverageBookingDurationByCity(@PathVariable String city){
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("La città non può essere null o vuota");
        }

        return archivedBookService.findAverageBookingDurationByCity(city);
    }
    @GetMapping("/{city}/mostCommonBirthPlace")
    public BirthPlaceFrequencyDTO getMostCommonBirthPlaceByCity(@PathVariable String city){

        return archivedBookService.findMostCommonBirthPlaceByCity(city);
    }



}
