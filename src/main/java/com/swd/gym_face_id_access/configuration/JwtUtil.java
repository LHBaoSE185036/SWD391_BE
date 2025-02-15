package com.swd.gym_face_id_access.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    //This class is used to generate a token, extract username from a token and validate

    private final String SECRET_KEY = "003fa646aa7e7b652966ba3edf68823f16eaf50445716b1b3d27e24947af2ed672443fc30413316b79cceb13475a283d806897022a9157acf3a04061583a8648981787e2ab7e11b3e3687ff662ab282b4f312d68b4cfcebcde66bc3a66930a5f5a64edac90ce920f5a44d83d0be32f7e7a4961c46d1c0457d03f0e7eb1db2ce9bbb8c59da38e527ec601cd7a33523818b5d1da0e1dbe6f2d75630a76e29947acb4b05a147f7bc5b87ed9bd0f31bae5e4948a32bdf2897b4c05826492f34cf8928e9cb7438137a3ae45ab558b0a1e01b4e73e464b27b233350d9f7eb662f3f0eb84770268d1bb541e49678cdf895280cf52afb4dbdd9cd81caa58e829e74e5484";
    private final long EXPIRATION_TIME = 86400000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
