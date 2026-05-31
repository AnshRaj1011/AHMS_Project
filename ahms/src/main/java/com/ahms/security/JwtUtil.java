package com.ahms.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")   private String secret;
    @Value("${jwt.expiration}") private Long expiration;

    private Key key() { return Keys.hmacShaKeyFor(secret.getBytes()); }

    public String generate(UserDetails ud, String role) {
        return Jwts.builder()
                .setClaims(Map.of("role", role))
                .setSubject(ud.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String username(String token) { return claim(token, Claims::getSubject); }
    public String role(String token)     { return claims(token).get("role", String.class); }

    public boolean validate(String token, UserDetails ud) {
        return username(token).equals(ud.getUsername()) && !claims(token).getExpiration().before(new Date());
    }

    private <T> T claim(String t, Function<Claims,T> f) { return f.apply(claims(t)); }
    private Claims claims(String t) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(t).getBody();
    }
}
