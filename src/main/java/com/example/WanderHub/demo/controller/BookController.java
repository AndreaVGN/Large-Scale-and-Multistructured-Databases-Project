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

    @GetMapping("/{hostUsername}/viewAccommodationBooks/{id}")
    public ResponseEntity<?> viewAccommodationBooks(@PathVariable String hostUsername, @PathVariable String id, HttpSession session) {
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
    @PutMapping("/{username}/{accommodationId}/addBook")
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Accommodation already booked from another user or time period not valid");
        }
    }


    @DeleteMapping("/{accommodationId}/unlock")
    public ResponseEntity<String> unlockHouseUnregistered(
            @PathVariable ObjectId accommodationId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @CookieValue(value = "bookingTimestamp", required = false) String timestampCookie) {

        if (timestampCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No accommodation timestamp found.");
        }

        boolean unlocked = bookService.unlockHouse(accommodationId, startDate, endDate, timestampCookie);

        if (unlocked) {
            return ResponseEntity.ok("Unlocked accommodation successfully");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error during unlocking of the accommodation");
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
            return ResponseEntity.ok("Unlocked accommodation successfully");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error unlocking of the accommodation or the accommodation does not exist.");
        }
    }

    @GetMapping("/{username}/top-cities")
    public ResponseEntity<?> getTopCities(@PathVariable String username, HttpSession session) {
        // Controllo dell'autenticazione e dei permessi
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Chiamata al service per ottenere la classifica
        List<CityBookingRankingDTO> topCities = archivedBookService.getTopCities();
        return ResponseEntity.ok(topCities);
    }

    @GetMapping("/{username}/average-age/{city}")
    public ResponseEntity<?> getAverageAgeByCity(@PathVariable String username, @PathVariable String city, HttpSession session) {
        // Controllo dell'autenticazione e dei permessi
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Chiamata al service per ottenere l'età media
        List<CityAverageAgeDTO> avgAge = archivedBookService.getAverageAgeByCity(city);
        return ResponseEntity.ok(avgAge);
    }

    @GetMapping("/{username}/top-cities-price-range")
    public ResponseEntity<?> getTopCitiesByPriceRange(@PathVariable String username, @RequestParam double minPrice, @RequestParam double maxPrice, HttpSession session) {
        // Controllo dell'autenticazione e dei permessi
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Chiamata al service per ottenere la classifica delle città per range di prezzo
        List<CityBookingRankingDTO> topCitiesByPriceRange = archivedBookService.getTopCitiesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(topCitiesByPriceRange);
    }

    @GetMapping("/{username}/city/{city}/monthly-visits")
    public ResponseEntity<?> getMonthlyVisits(@PathVariable String username, @PathVariable String city, HttpSession session) {
        // Controllo dell'autenticazione e dei permessi
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Chiamata al service per ottenere le visite mensili per città
        List<CityMonthlyVisitDTO> monthlyVisits = archivedBookService.getMonthlyVisitsByCity(city);
        return ResponseEntity.ok(monthlyVisits);
    }

    @GetMapping("/{username}/{city}/avgHolidayDuration")
    public ResponseEntity<?> findAverageBookingDurationByCity(@PathVariable String username, @PathVariable String city, HttpSession session) {
        // Controllo dell'autenticazione e dei permessi
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Controllo che la città non sia vuota
        if (city == null || city.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("City must not be null or empty");
        }

        // Chiamata al service per ottenere la durata media della prenotazione per città
        AverageBookingResultDTO avgHolidayDuration = archivedBookService.findAverageBookingDurationByCity(city);
        return ResponseEntity.ok(avgHolidayDuration);
    }

    @GetMapping("/{username}/{city}/mostCommonBirthPlace")
    public ResponseEntity<?> getMostCommonBirthPlaceByCity(@PathVariable String username, @PathVariable String city, HttpSession session) {
        // Controllo dell'autenticazione e dei permessi
        if (!SessionUtilility.isLogged(session, username) || !SessionUtilility.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
        }

        // Chiamata al service per ottenere il luogo di nascita più comune per città
        BirthPlaceFrequencyDTO mostCommonBirthPlace = archivedBookService.findMostCommonBirthPlaceByCity(city);
        return ResponseEntity.ok(mostCommonBirthPlace);
    }




}
