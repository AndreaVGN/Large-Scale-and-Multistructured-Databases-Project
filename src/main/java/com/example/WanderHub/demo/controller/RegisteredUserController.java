package com.example.WanderHub.demo.controller;

import com.example.WanderHub.demo.model.RegisteredUser;
import com.example.WanderHub.demo.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService userService;

    @PostMapping
    public RegisteredUser createUser(@RequestBody RegisteredUser user) {
        return userService.createRegisteredUser(user);
    }
}
