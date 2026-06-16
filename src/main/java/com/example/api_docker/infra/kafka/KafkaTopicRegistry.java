package com.example.api_docker.infra.kafka;

import com.example.api_docker.domain.admin.event.AdminCreatedEvent;
import com.example.api_docker.domain.certificate.event.CertificateIssuedEvent;
import com.example.api_docker.domain.course.event.CourseCreatedEvent;
import com.example.api_docker.domain.course.event.CoursePublishedEvent;
import com.example.api_docker.domain.enrollment.event.*;
import com.example.api_docker.domain.payment.event.PaymentCancelledEvent;
import com.example.api_docker.domain.payment.event.PaymentConfirmedEvent;
import com.example.api_docker.domain.payment.event.PaymentCreatedEvent;
import com.example.api_docker.domain.payment.event.PaymentFailedEvent;
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

    private static final Map<Class<? extends DomainEvent>, String> TOPICS = Map.ofEntries(
            Map.entry(EnrollmentCreatedEvent.class,   "enrollment.created"),
            Map.entry(EnrollmentCompletedEvent.class, "enrollment.completed"),
            Map.entry(EnrollmentCancelledEvent.class, "enrollment.cancelled"),
            Map.entry(EnrollmentSuspendedEvent.class, "enrollment.suspended"),
            Map.entry(EnrollmentActivatedEvent.class, "enrollment.activated"),
            Map.entry(EnrollmentReactivatedEvent.class, "enrollment.reactivated"),
            Map.entry(CoursePublishedEvent.class,     "course.published"),
            Map.entry(CourseCreatedEvent.class,       "course.created"),
            Map.entry(CertificateIssuedEvent.class,   "certificate.issued"),
            Map.entry(StudentRegisteredEvent.class,   "student.registered"),
            Map.entry(AdminCreatedEvent.class,        "admin.created"),
            Map.entry(PaymentCreatedEvent.class,      "payment.created"),
            Map.entry(PaymentConfirmedEvent.class,    "payment.confirmed"),
            Map.entry(PaymentFailedEvent.class,       "payment.failed"),
            Map.entry(PaymentCancelledEvent.class,    "payment.cancelled")
    );

    public String topicFor(DomainEvent event) {
        String topic = TOPICS.get(event.getClass());
        if (topic == null) {
            throw new IllegalArgumentException(
                    "Evento sem tópico mapeado: " + event.getClass().getSimpleName()
            );
        }
        return topic;
    }
}
