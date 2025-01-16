package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registeredUsers")
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService RegisteredUserService;

    @PostMapping
    public RegisteredUser createRegisteredUser(@RequestBody RegisteredUser registeredUser) {
        return RegisteredUserService.createRegisteredUser(registeredUser);
    }

    @GetMapping("/{id}")
    public RegisteredUser getRegisteredUser(@PathVariable String username) {
        return RegisteredUserService.getRegisteredUserById(username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegisteredUser(@PathVariable String username) {
        boolean isDeleted = RegisteredUserService.deleteRegisteredUserById(username);

        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
