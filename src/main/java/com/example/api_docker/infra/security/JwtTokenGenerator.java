package com.example.api_docker.infra.security;

import com.example.api_docker.domain.student.Email;
import com.example.api_docker.domain.student.StudentId;
import com.example.api_docker.domain.student.TokenGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final String secret;

    public JwtTokenGenerator(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    @Override
    public String generate(StudentId studentId, Email email) {
        return Jwts.builder()
                .subject(studentId.value().toString())
                .claim("email", email.value())
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(secretKey())
                .compact();
    }

    private SecretKey secretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
