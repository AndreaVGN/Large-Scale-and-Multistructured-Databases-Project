package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.Accommodation;
import com.example.WanderHub.demo.model.Book;
import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.service.RegisteredUserService;
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
        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{username}/addBook")
    public ResponseEntity<RegisteredUser> addBookToAccommodation(
            @PathVariable String username,
            @RequestBody Book newBook) {

        // Aggiungi la nuova book alla sistemazione
        RegisteredUser updatedRegisteredUser = registeredUserService.addBookToRegisteredUser(username, newBook);


        return new ResponseEntity<>(updatedRegisteredUser, HttpStatus.OK);
    }
}
