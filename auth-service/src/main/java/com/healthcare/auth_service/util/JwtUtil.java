package com.healthcare.auth_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

//help to print and validate token
@Component
public class JwtUtil {

    @Value("${app.security.secret-key}")
    public String SECRET;

    //generate token
    public String generateToken(String userName, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of(role));

        return Jwts
                .builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] decode = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(decode);
    }



    //extract information from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    //validation logic
    public Boolean validateToken(String token, String username) {
        final String extractUsername = extractUsername(token);
        final Date expiration = extractClaim(token, Claims::getExpiration);

        return (
                extractUsername.equals(username)
                        &&
                !expiration.before(new Date())
        );
    }



}
