package com.example.api_docker.application.enrollment.command;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.payment.PaymentMethodType;
import com.example.api_docker.domain.user.UserId;

public record EnrollStudentCommand(
        UserId studentId,
        CourseId courseId,
        PaymentMethodType paymentMethod,
        int installments
) {}
