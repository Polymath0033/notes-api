package org.polymath.noteapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.polymath.noteapi.dto.response.AuthResponse;
import org.polymath.noteapi.exceptions.UserDoesNotExist;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.polymath.noteapi.util.CalculateExpiresInSeconds;
import org.polymath.noteapi.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;

    public GoogleOauth2SuccessHandler(UserRepo userRepo, ObjectMapper objectMapper) {
        this.userRepo = userRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, chain, authentication);
        try {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            String email = token.getPrincipal().toString();
            if(email==null){
                throw new UserDoesNotExist("Email not found in oauth");
            }
            Users user = userRepo.findUserByEmail(email).orElseThrow(()->new UserDoesNotExist("Email not found in oauth"));
            AuthResponse authResponse = new AuthResponse(user.getId(),user.getEmail(),user.getAuthToken(), CalculateExpiresInSeconds.calculateTimeInSeconds(user.getTokenExpiresAt()));
            sendSuccessResponse(response, authResponse);

        } catch (IOException e) {
            sendFailureResponse(response);
        }
    }
    private void sendSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        ResponseEntity<?> responseEntity = ResponseHandler.handleResponse(data, HttpStatus.OK, "Oauth2 successfull");
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }
    private void sendFailureResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json");
        ResponseEntity<?> responseEntity = ResponseHandler.handleResponse(null, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication fAiled");
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }

}
