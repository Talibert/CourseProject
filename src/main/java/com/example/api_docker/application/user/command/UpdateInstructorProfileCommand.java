package com.example.api_docker.application.user.command;

import com.example.api_docker.domain.instructor.SocialLinks;
import com.example.api_docker.domain.user.UserId;

public record UpdateInstructorProfileCommand(
        UserId instructorId,
        String bio,
        String specialty,
        SocialLinks socialLinks
) {}
