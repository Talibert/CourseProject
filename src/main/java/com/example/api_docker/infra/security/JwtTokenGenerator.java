package com.example.api_docker.infra.security;

import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.TokenGenerator;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.domain.user.UserRole;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Gera o token do usuário para utilizar em requisições fechadas
 */
@Component
@AllArgsConstructor
public class JwtTokenGenerator implements TokenGenerator {

    private final JwtSecretKey jwtSecretKey;

    @Override
    public String generate(UserId userId, Email email, UserRole userRole) {
        return Jwts.builder()
                .subject(userId.value().toString())
                .claim("email", email.value())
                .claim("role", userRole.name())
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .signWith(jwtSecretKey.get())
                .compact();
    }
}
