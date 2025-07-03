package org.example.services;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    public static final String SECRET = "A+FybXodIYJY8xj2Z/DU6xuJhH3eNlUrXYsSjy3XoWg=";

    public String extractUsername(String token)
    {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiry(String token)
    {
        return extractClaims(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token)
    {
        return extractExpiry(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String GenerateToken(String userName)
    {
        Map<String, Object> initialClaims = new HashMap<>();
        return createToken(initialClaims, userName);
    }

    private String createToken(Map<String, Object> claims, String username)
    {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30)) // 30-minute validity
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String token)
    {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();


    }

    private Key getSignKey()
    {
       byte[] keyBytes = Decoders.BASE64.decode(SECRET);
       return Keys.hmacShaKeyFor(keyBytes);
    }
}
