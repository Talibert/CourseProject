package com.example.api_docker.infra.kafka;

import com.example.api_docker.domain.admin.event.AdminCreatedEvent;
import com.example.api_docker.domain.certificate.event.CertificateIssuedEvent;
import com.example.api_docker.domain.course.event.CoursePublishedEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentCancelledEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentCompletedEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentCreatedEvent;
import com.example.api_docker.domain.enrollment.event.EnrollmentSuspendedEvent;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.student.event.StudentRegisteredEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Vamos usar essa classe para mapear os eventos. Isso evita que o domínio conheça o kafka
 */
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaTopicRegistry {

    private static final Map<Class<? extends DomainEvent>, String> TOPICS = Map.of(
            EnrollmentCreatedEvent.class,   "enrollment.created",
            EnrollmentCompletedEvent.class, "enrollment.completed",
            EnrollmentCancelledEvent.class, "enrollment.cancelled",
            EnrollmentSuspendedEvent.class, "enrollment.suspended",
            CoursePublishedEvent.class,     "course.published",
            CertificateIssuedEvent.class,   "certificate.issued",
            StudentRegisteredEvent.class,  "student.registered",
            AdminCreatedEvent.class,       "admin.created"
    );

    public String topicFor(DomainEvent event) {
        String topic = TOPICS.get(event.getClass());

        if (topic == null)
            throw new IllegalArgumentException("Evento sem tópico mapeado: " + event.getClass().getSimpleName());

        return topic;
    }
}
