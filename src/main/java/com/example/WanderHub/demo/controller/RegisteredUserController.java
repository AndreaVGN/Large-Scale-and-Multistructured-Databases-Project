package com.example.WanderHub.demo.controller;
import com.example.WanderHub.demo.DTO.AuthRequestDTO;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.service.AccommodationService;
import com.example.WanderHub.demo.service.BookService;
import com.example.WanderHub.demo.service.RegisteredUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class RegisteredUserController {

    // Iniezione del servizio tramite @Autowired
    @Autowired
    private RegisteredUserService registeredUserService;
    @Autowired
    private AccommodationService accommodationService;
    @Autowired
    private BookService bookService;

    @PostMapping
    public RegisteredUser createRegisteredUser(@RequestBody RegisteredUser registeredUser) {

        return registeredUserService.createRegisteredUser(registeredUser);
    }

    @GetMapping("/{user}")
    public RegisteredUser getRegisteredUser(@PathVariable String user) {

        return registeredUserService.getRegisteredUserByUsername(user);
    }

    @DeleteMapping("/{user}")
    public ResponseEntity<Void> deleteRegisteredUser(@PathVariable String user) {

        boolean isDeleted = registeredUserService.deleteRegisteredUserByUsername(user);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




    // Endpoint per il login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO loginRequest, HttpSession session) {

        boolean isAuthenticated = registeredUserService.authenticate(loginRequest, session);

        if (isAuthenticated) {

            return ResponseEntity.status(HttpStatus.OK).body("Login successful");

        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();

        return ResponseEntity.ok("Logged out successfully.");
    }
}
