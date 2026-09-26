package ru.mirea.autopartsstore.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mirea.autopartsstore.auth.entity.AppUser;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        this.expiration = expiration;
    }

    public String generateToken(AppUser user) {

        Date now = new Date();

        Date expirationDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim(
                        "role",
                        user.getRole().name()
                )
                .claim(
                        "userId",
                        user.getId()
                )
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            AppUser user
    ) {

        Claims claims = extractClaims(token);

        String email = claims.getSubject();

        return email.equalsIgnoreCase(
                user.getEmail()
        ) && claims.getExpiration()
                .after(new Date());
    }

    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}