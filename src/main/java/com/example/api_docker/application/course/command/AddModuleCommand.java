package com.example.api_docker.application.course.command;

import com.example.api_docker.domain.course.CourseId;

public record AddModuleCommand(
        CourseId courseId,
        String title,
        int order
) {}
