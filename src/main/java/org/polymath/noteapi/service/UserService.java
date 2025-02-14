package org.polymath.noteapi.service;

import org.polymath.noteapi.dto.request.ChangePasswordRequest;
import org.polymath.noteapi.dto.request.RegisterRequest;
import org.polymath.noteapi.dto.response.AuthResponse;
import org.polymath.noteapi.exceptions.CustomBadRequest;
import org.polymath.noteapi.exceptions.UserDoesNotExist;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.polymath.noteapi.util.CalculateExpiresInSeconds;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class UserService {

    private final UserRepo userRepo;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepo userRepo, JWTService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse createUser(RegisterRequest user) {
        if(userRepo.existsByEmail(user.email())){
            throw new CustomBadRequest.UserAlreadyExistsException("Email already exist");
        }

       Users users = new Users();
        users.setEmail(user.email());
        users.setPassword(encoder.encode(user.password()));
        String authToken = jwtService.generateToken(user.email());
        LocalDateTime expirationTime = jwtService.expirationDate(authToken);
        users.setAuthToken(authToken);
        users.setTokenExpiresAt(expirationTime);
        userRepo.save(users);
        return getAuthResponse(users);
    }

    public AuthResponse login(RegisterRequest user) {
        Users existingUser = userRepo.findUserByEmail(user.email()).orElseThrow(()->new UserDoesNotExist("You've not authenticated yet"));
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(existingUser.getEmail());
                existingUser.setAuthToken(token);
                existingUser.setTokenExpiresAt(jwtService.expirationDate(token));
                return getAuthResponse(existingUser);
            } else {
               throw new RuntimeException("Authentication failed");
            }
        } catch (Exception e) {
           throw new RuntimeException("Authentication failed");

        }
    }

    public void changePassword(ChangePasswordRequest request,String authHeader){
        if((request.oldPassword()==null||request.oldPassword().isEmpty())&&(request.newPassword()==null||request.newPassword().isEmpty())){
            throw new CustomBadRequest("Old password and new password are required");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        Users user = userRepo.findUserByEmail(email).orElseThrow(()->new UserDoesNotExist("You are not authenticated"));
        if(!encoder.matches(request.oldPassword(),user.getPassword())){
            throw new CustomBadRequest("Old password does not match");
        }
        String encodedNewPassword = encoder.encode(request.newPassword());
        user.setPassword(encodedNewPassword);
        userRepo.save(user);
    }


    private AuthResponse getAuthResponse(Users user) {
        return new AuthResponse(user.getId(),user.getEmail(),user.getAuthToken(), CalculateExpiresInSeconds.calculateTimeInSeconds(user.getTokenExpiresAt()));
    }
}
