package com.example.api_docker.infra.certificate;

import com.example.api_docker.domain.certificate.VerificationCodeGenerator;
import com.google.common.hash.Hashing;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Primary
@Component
public class HashVerificationCodeGenerator implements VerificationCodeGenerator {

    @Override
    public String generate() {
        return Hashing.sha256()
                .hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8)
                .toString()
                .toUpperCase();
    }
}
