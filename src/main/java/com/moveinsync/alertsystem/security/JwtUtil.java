package com.moveinsync.alertsystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "moveinsyncsupersecretmoveinsyncsupersecret";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 🔐 Generate token
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔐 Validate token (used in controller)
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;   // token valid
        }catch(Exception e){
            System.out.println("JWT Error: " + e.getMessage());
            return false;  // invalid token
        }
    }

    // 🔐 Extract username (bonus for interview)
    public String getUsername(String token){
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        }catch(Exception e){
            return null;
        }
    }
}