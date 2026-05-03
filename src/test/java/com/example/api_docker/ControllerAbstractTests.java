package com.example.api_docker;

import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.UserId;
import com.example.api_docker.domain.user.UserRole;
import com.example.api_docker.infra.config.SecurityConfig;
import com.example.api_docker.infra.exception.GlobalExceptionHandler;
import com.example.api_docker.infra.security.JwtAuthenticationFilter;
import com.example.api_docker.infra.security.JwtSecretKey;
import com.example.api_docker.infra.security.JwtTokenGenerator;
import com.example.api_docker.infra.security.JwtTokenValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

@TestPropertySource(locations = "classpath:application-test-unit.properties")
@Import({SecurityConfig.class, JwtAuthenticationFilter.class,
        JwtTokenValidator.class, JwtTokenGenerator.class, JwtSecretKey.class,
        GlobalExceptionHandler.class})
public abstract class ControllerAbstractTests {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenGenerator jwtTokenGenerator;

    /**
     * Podemos utilizar esses tokens e ids em outros testes de controller para simular o comportamento de rotas que
     * exigem role especifica
     */
    protected String tokenDoAdmin;
    protected String tokenDoStudent;
    protected UserId idDoAdmin;
    protected UserId idDoStudent;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        idDoAdmin = new UserId(UUID.randomUUID());
        idDoStudent = new UserId(UUID.randomUUID());

        tokenDoAdmin = jwtTokenGenerator.generate(
                idDoAdmin,
                new Email("guilhermetaliberti@gmail.com"),
                UserRole.ADMIN
        );

        tokenDoStudent = jwtTokenGenerator.generate(
                idDoStudent,
                new Email("guilhermetaliberti@gmail.com"),
                UserRole.STUDENT
        );
    }
}
