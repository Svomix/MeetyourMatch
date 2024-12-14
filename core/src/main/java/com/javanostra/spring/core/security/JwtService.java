package com.javanostra.spring.core.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.javanostra.spring.core.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;

    public JwtService(String privateKey){
        algorithm = Algorithm.HMAC256(privateKey);
    }

    public String generateToken(User user) {
        return JWT.create()
                .withClaim("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withClaim("id", user.getId())
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(Instant.now()))
                .sign(algorithm);
    }

    public boolean verifyToken(String token){
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e){
            return false;
        }
    }

    public String extractToken(String token) throws JWTVerificationException{
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        return verifier.verify(token).getSubject();
    }
}
