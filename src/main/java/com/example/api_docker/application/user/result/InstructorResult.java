package com.example.api_docker.application.user.result;

import com.example.api_docker.domain.instructor.Instructor;

import java.time.LocalDateTime;
import java.util.UUID;

public record InstructorResult(
        UUID instructorId,
        String fullName,
        String email,
        String bio,
        String specialty,
        String profilePicture,
        SocialLinksResult socialLinks,
        LocalDateTime createdAt
) {
    public static InstructorResult from(Instructor instructor) {
        return new InstructorResult(
                instructor.getId().value(),
                instructor.getName().full(),
                instructor.getEmail().value(),
                instructor.getBio(),
                instructor.getSpecialty(),
                instructor.getProfilePicture(),
                SocialLinksResult.from(instructor.getSocialLinks()),
                instructor.getCreatedAt()
        );
    }
}
