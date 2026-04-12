package com.example.api_docker.infra.certificate;

import com.example.api_docker.domain.certificate.VerificationCodeGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidVerificationCodeGenerator implements VerificationCodeGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
