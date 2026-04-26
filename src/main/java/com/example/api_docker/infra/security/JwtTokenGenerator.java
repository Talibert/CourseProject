package com.example.api_docker.infra.security;

import com.example.api_docker.domain.student.Email;
import com.example.api_docker.domain.student.StudentId;
import com.example.api_docker.domain.student.TokenGenerator;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final JwtSecretKey jwtSecretKey;

    public JwtTokenGenerator(JwtSecretKey jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    @Override
    public String generate(StudentId studentId, Email email) {
        return Jwts.builder()
                .subject(studentId.value().toString())
                .claim("email", email.value())
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(jwtSecretKey.get())
                .compact();
    }
}
