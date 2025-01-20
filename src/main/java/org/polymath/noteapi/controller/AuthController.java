package org.polymath.noteapi.controller;

import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.polymath.noteapi.service.UserService;
import org.polymath.noteapi.util.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;

//    @GetMapping("/")
//    public List<Users> home() {
//        return userService.getAllUsers();
//    }
    @GetMapping("/")
    public String home() {
        return "Hello World";
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        //return userService.createUser(user);
        try {
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status",HttpStatus.CONFLICT.value(),
                    "message",e.getMessage()
            ));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "status",HttpStatus.CONFLICT.value(),
                            "message",e.getMessage()
                    )
            );
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody Users user) {
        System.out.println(user);
        return userService.login(user);
    }
    @GetMapping("/user")
    public Optional<Users> getUser(@RequestParam UUID id) {
        return userRepo.findById(id);
    }
}
