package org.polymath.noteapi.service;

import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.polymath.noteapi.util.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    public void createUser(Users user) {
        if(userRepo.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("Email already exist");
        }
        if(userRepo.existsByUsername(user.getUsername())){
            throw new UserAlreadyExistsException("Username already exist");
        }
        userRepo.save(user);
        user.setPassword(encoder.encode(user.getPassword()));
        String authToken = jwtService.generateToken(user.getId().toString());
        LocalDateTime expirationTime = jwtService.expirationDate(authToken);
        user.setAuthToken(authToken);
        user.setExpiresAt(expirationTime);
        userRepo.save(user);
    }

    public String login(Users user) {
        Users existingUser = userRepo.findUserByEmail(user.getEmail());
        //  CustomAuthenticationToken token = new CustomAuthenticationToken(user.getUsername(),user.getPassword(),user.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(existingUser.getId(), user.getPassword()));
            System.out.println("Authentication successful: " + authentication.isAuthenticated());
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(existingUser.getId().toString());
                user.setAuthToken(token);
                user.setExpiresAt(jwtService.expirationDate(token));
                return token;
            } else {
                return "Invalid email or password";
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Authentication failed: " + e.getMessage());
            return "Invalid email or password";
//            throw new RuntimeException(e);
        }
    }
    public List<Users> getAllUsers(){
        return userRepo.findAll();
    }
}
