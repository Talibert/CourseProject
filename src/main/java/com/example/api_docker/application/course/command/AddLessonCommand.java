package com.example.api_docker.application.course.command;

import com.example.api_docker.domain.course.CourseId;
import com.example.api_docker.domain.course.ModuleId;

public record AddLessonCommand(
        CourseId courseId,
        ModuleId moduleId,
        String title,
        int order,
        int durationMinutes
) {}
