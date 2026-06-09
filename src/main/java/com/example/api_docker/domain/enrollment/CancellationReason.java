package com.example.api_docker.domain.enrollment;

public enum CancellationReason {

    STUDENT_REQUEST("Solicitação do student"),
    STUDENT_BANNED("Student banido"),
    COURSE_CLOSED("Curso encerrado"),
    PAYMENT_OVERDUE("Inadimplência"),
    ADMINISTRATIVE_REQUEST("Solicitação administrativa");

    private final String description;

    CancellationReason(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
