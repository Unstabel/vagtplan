package com.vagtplan.vagtplan.controller;

import com.vagtplan.vagtplan.model.User;
import com.vagtplan.vagtplan.repository.UserRepository;
import com.vagtplan.vagtplan.security.JWTUtil;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public AuthController(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        User user = userRepository.findByUsernameIgnoreCase(loginUser.getUsername());

        if (user != null && user.getPassword().equals(loginUser.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole(),
                "token", token
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}

