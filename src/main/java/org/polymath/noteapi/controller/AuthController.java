package org.polymath.noteapi.controller;

import org.polymath.noteapi.dto.request.ChangePasswordRequest;
import org.polymath.noteapi.dto.request.RegisterRequest;
import org.polymath.noteapi.dto.response.AuthResponse;

import org.polymath.noteapi.service.UserService;
import org.polymath.noteapi.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@CrossOrigin
public class AuthController {
    private final UserService userService;

    public AuthController( UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public String home() {
        return "Hello World";
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest user) {
        AuthResponse response = userService.createUser(user);
        return ResponseHandler.handleResponse(response,HttpStatus.CREATED,"New user created");
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody RegisterRequest user) {
        AuthResponse response = userService.login(user);
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"Successfully logged in");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, @RequestHeader("Authorization") String authHeader){
        userService.changePassword(changePasswordRequest, authHeader);
        return ResponseHandler.handleResponse(null,HttpStatus.OK,"Changed password successfully");

    }
}
