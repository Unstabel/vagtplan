package com.vagtplan.vagtplan.controller;

import com.vagtplan.vagtplan.model.User;
import com.vagtplan.vagtplan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()) != null) {
            return "Bruger eksisterer allerede.";
        }
        userRepository.save(user);
        return "Bruger oprettet!";
    }
}
