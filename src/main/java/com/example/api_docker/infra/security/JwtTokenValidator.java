package com.example.api_docker.infra.security;

import com.example.api_docker.domain.student.StudentId;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Segundo passo da cadeia -> valida o token do usuário
 */
@Component
public class JwtTokenValidator {

    private final JwtSecretKey jwtSecretKey;

    public JwtTokenValidator(JwtSecretKey jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    public StudentId validate(String token) {
        var claims = Jwts.parser()
                .verifyWith(jwtSecretKey.get())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new StudentId(UUID.fromString(claims.getSubject()));
    }
}
