package org.polymath.noteapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(String subject) {
        Map<String,Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+7*24*60*60*1000))
                .and()
                .signWith(getSecretKey()).compact();
    }
    public String extractEmail(String token){
        return extractClaim(token,Claims::getSubject);

    }
    private  <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public boolean validateToken(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }
    public boolean isTokenExpired(String token){
        LocalDateTime expirationTime = expirationDate(token);
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return expirationTime.isBefore(now);
    }
    protected LocalDateTime expirationDate(String token){
        Date _expirationDate = extractClaim(token,Claims::getExpiration);
        return coverToLocalDateTime(_expirationDate);
    }
    public SecretKey getSecretKey() {
        byte[] encodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(encodedKey);
    }
    public LocalDateTime coverToLocalDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
