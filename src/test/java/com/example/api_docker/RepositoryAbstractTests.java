package com.example.api_docker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test-repository.properties")
public abstract class RepositoryAbstractTests {
}
