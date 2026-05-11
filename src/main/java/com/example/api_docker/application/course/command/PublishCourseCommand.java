package com.example.api_docker.application.course.command;

import com.example.api_docker.domain.course.CourseId;

public record PublishCourseCommand(CourseId courseId) {}
