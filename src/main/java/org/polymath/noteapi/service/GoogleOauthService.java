package org.polymath.noteapi.service;

import org.polymath.noteapi.dto.response.TokenInfo;
import org.polymath.noteapi.models.UserPrincipal;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class GoogleOauthService extends DefaultOAuth2UserService {
    private final UserRepo userRepo;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public GoogleOauthService(UserRepo userRepo, JWTService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        Users user = userRepo.findUserByEmail(email).map(this::regenerateAuthTokenForRegisteredUser).orElseGet(()->createNewUserAndItsToken(email));
        return new UserPrincipal(user,oAuth2User.getAttributes());
    }

    private Users regenerateAuthTokenForRegisteredUser(Users user){
        TokenInfo tokenInfo = generateToken(user.getEmail());
        user.setAuthToken(tokenInfo.authToken());
        user.setTokenExpiresAt(tokenInfo.tokenExpiration());
        return userRepo.save(user);
    }

    private Users createNewUserAndItsToken(String email){
        Users newUser = new Users();
        newUser.setEmail(email);
        String randomPassword = UUID.randomUUID().toString();
        newUser.setPassword(encoder.encode(randomPassword));
        TokenInfo tokenInfo = generateToken(email);
        newUser.setAuthToken(tokenInfo.authToken());
        newUser.setTokenExpiresAt(tokenInfo.tokenExpiration());
        userRepo.save(newUser);
        return newUser;
    }

    private TokenInfo generateToken(String email){
        String authToken = jwtService.generateToken(email);
        LocalDateTime tokenExpirations = jwtService.expirationDate(authToken);
        return new TokenInfo(authToken,tokenExpirations);
    }
}
