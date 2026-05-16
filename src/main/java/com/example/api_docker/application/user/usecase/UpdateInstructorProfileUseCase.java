package com.example.api_docker.application.user.usecase;

import com.example.api_docker.application.user.command.UpdateInstructorProfileCommand;
import com.example.api_docker.domain.course.exception.InstructorNotFoundException;
import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UpdateInstructorProfileUseCase {

    private final InstructorRepository instructorRepository;
    private final DomainEventPublisher eventPublisher;

    public void execute(UpdateInstructorProfileCommand command) {
        Instructor instructor = instructorRepository.findById(command.instructorId())
                .orElseThrow(() -> new InstructorNotFoundException(command.instructorId()));

        instructor.updateProfile(command.bio(), command.specialty());

        if(command.socialLinks() != null)
            instructor.updateSocialLinks(command.socialLinks());

        instructorRepository.save(instructor);
        instructor.pullDomainEvents().forEach(eventPublisher::publish);
    }
}
