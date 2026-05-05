package com.example.api_docker.infra.kafka;

import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers") // Só cria o bean se o kafka estiver habilitado nas properties
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicRegistry topicRegistry;

    @Override
    public void publish(DomainEvent event) {
        String topic = topicRegistry.topicFor(event);
        kafkaTemplate.send(topic, event);
    }
}
