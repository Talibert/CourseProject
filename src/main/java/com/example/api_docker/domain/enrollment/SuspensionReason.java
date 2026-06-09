package com.example.api_docker.domain.enrollment;

public enum SuspensionReason {

    PAYMENT_OVERDUE("Pagamento em atraso"),
    TERMS_VIOLATION("Violação de termos de uso"),
    ADMINISTRATIVE_REQUEST("Solicitação administrativa");

    private final String description;

    SuspensionReason(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
