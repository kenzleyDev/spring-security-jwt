package br.com.treinaweb.javajobs.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    /***
     * Em Produção, o SIGNIN_KEY deve vir de uma enviroment
     */
    private static final String SIGNIN_KEY = "IbuapnZN7F3pFTQ0dEZmmhIdl3SWn5rt";

    private static final String REFRESH_SIGNIN_KEY = "EZ682tcHMJ9x3cLVtFzwqkToUJzYuKKZS";

    private static final int REFRESH_EXPIRATION_TIME = 60;

    private static final int EXPIRATION_TIME = 30;

    public String gerenateToken(Authentication authentication) {
        return generateToken(SIGNIN_KEY, authentication.getName(), EXPIRATION_TIME);
    }

    public String generateRefreshToken(String username) {
        return generateToken(REFRESH_SIGNIN_KEY, username, REFRESH_EXPIRATION_TIME);
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = getClaims(token, SIGNIN_KEY);
        return claims.getExpiration();
    }
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token, SIGNIN_KEY);
        return claims.getSubject();
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        Claims claims = getClaims(refreshToken, REFRESH_SIGNIN_KEY);
        return claims.getSubject();
    }

    private String generateToken(String signinKey, String subject, int expirationTime) {
        Map<String, Object> claims = new HashMap<>();

        Instant currentDate = Instant.now();
        Instant expirationDate = currentDate.plusSeconds(expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(currentDate.toEpochMilli()))
                .setExpiration(new Date(expirationDate.toEpochMilli()))
                .signWith(SignatureAlgorithm.HS512, signinKey)
                .compact();

    }
    private Claims getClaims(String token, String signinKey) {
        return Jwts.parser()
                .setSigningKey(signinKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
