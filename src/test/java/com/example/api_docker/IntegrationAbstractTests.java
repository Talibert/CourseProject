package com.example.api_docker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { // TODO: remover isso e colocar especifico em cada classe de teste
        "enrollment.created",
        "enrollment.completed",
        "enrollment.cancelled",
        "enrollment.suspended",
        "course.published",
        "certificate.issued"
})
@TestPropertySource(locations = "classpath:application-test-integration.properties")
public abstract class IntegrationAbstractTests {
}
