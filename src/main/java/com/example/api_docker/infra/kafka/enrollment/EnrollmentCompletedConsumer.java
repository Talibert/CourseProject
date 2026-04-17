package com.example.api_docker.infra.kafka.enrollment;

import com.example.api_docker.application.certificate.usecase.IssueCertificateUseCase;
import com.example.api_docker.application.certificate.command.IssueCertificateCommand;
import com.example.api_docker.domain.enrollment.event.EnrollmentCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentCompletedConsumer {

    private final IssueCertificateUseCase issueCertificateUseCase;

    public EnrollmentCompletedConsumer(IssueCertificateUseCase issueCertificateUseCase) {
        this.issueCertificateUseCase = issueCertificateUseCase;
    }

    @KafkaListener(topics = "enrollment.completed")
    public void handle(EnrollmentCompletedEvent event) {
        IssueCertificateCommand command = new IssueCertificateCommand(
                event.enrollmentId(),
                event.studentId(),
                event.courseId()
        );
        issueCertificateUseCase.execute(command);
    }
}
