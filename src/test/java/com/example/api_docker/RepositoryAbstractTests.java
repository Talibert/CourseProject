package com.example.api_docker;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test-repository.properties")
@Transactional
public abstract class RepositoryAbstractTests {
    protected static final String DEFAULT_EMAIL = "guilherme@email.com";
    protected static final String DEFAULT_FIRST_NAME = "Guilherme";
    protected static final String DEFAULT_LAST_NAME = "Taliberti";
    protected static final String DEFAULT_PASSWORD_HASH = "hash-senha123";
}
