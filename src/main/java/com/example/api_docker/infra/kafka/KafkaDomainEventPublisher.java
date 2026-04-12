package com.example.api_docker.infra.kafka;

import com.example.api_docker.domain.certificate.event.CertificateIssuedEvent;
import com.example.api_docker.domain.course.event.CoursePublishedEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentCancelledEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentCompletedEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentCreatedEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentSuspendedEvent;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDomainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(DomainEvent event) {
        String topic = topicFor(event);
        kafkaTemplate.send(topic, event);
    }

    // TODO trocar o uso seco por annotations nas classes dos events
    private String topicFor(DomainEvent event) {
        return switch (event) {
            case EnrollmentCreatedEvent e    -> "enrollment.created";
            case EnrollmentCompletedEvent e  -> "enrollment.completed";
            case EnrollmentCancelledEvent e  -> "enrollment.cancelled";
            case EnrollmentSuspendedEvent e  -> "enrollment.suspended";
            case CoursePublishedEvent e      -> "course.published";
            case CertificateIssuedEvent e    -> "certificate.issued";
            default -> throw new IllegalArgumentException(
                    "Evento sem tópico mapeado: " + event.getClass().getSimpleName()
            );
        };
    }
}
