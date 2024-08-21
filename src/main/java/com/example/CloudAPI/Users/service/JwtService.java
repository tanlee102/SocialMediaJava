package com.example.CloudAPI.Users.service;

import com.example.CloudAPI.Users.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.secret.key}")
    private String SECRET_KEY;

    @Value("${application.secret.key_expiration}")
    private Long SECRET_KEY_EXPIRE;

    public boolean isValid(String token, UserDetails user){
        String email = extractEmail(token);
        String password = extractUserPassword(token);
        return (email.equals(user.getUsername())
                && password.equals(user.getPassword())
                && !isTokenExpired(token)
                && user.isAccountNonLocked());
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }
    public String extractUserPassword(String token) {
        return extractClaim(token, claims -> claims.get("password", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = Map.of(
                "id", user.getId(),
                "password", user.getPassword(),
                "role", user.getRole().getName(),
                "email", user.getEmail(),
                "upload", 2312
        );
        String token = Jwts
                .builder()
                .setClaims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + SECRET_KEY_EXPIRE ))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256) // Explicitly specify HS256
                .setHeaderParam("typ", "JWT") // Explicitly set the typ header
                .compact();
        return token;
    }

    private SecretKey getSigninKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // Use raw secret key bytes
    }
}