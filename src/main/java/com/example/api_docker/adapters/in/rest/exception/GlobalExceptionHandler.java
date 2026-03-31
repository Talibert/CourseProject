package com.example.api_docker.adapters.in.rest.exception;

import com.example.api_docker.domain.enrollment.exception.EnrollmentNotFoundException;
import com.example.api_docker.domain.enrollment.exception.InvalidEnrollmentTransitionException;
import com.example.api_docker.domain.shared.exception.DomainException;
import com.example.api_docker.domain.shared.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex) {
        return ResponseEntity
                .unprocessableEntity()
                .body(ErrorResponse.of("BUSINESS_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidEnrollmentTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidEnrollmentTransitionException ex) {
        return ResponseEntity
                .unprocessableEntity()
                .body(ErrorResponse.of("INVALID_TRANSITION", ex.getMessage()));
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EnrollmentNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of("INTERNAL_ERROR", "Erro interno. Tente novamente."));
    }
}
