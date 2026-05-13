package com.example.api_docker.domain.instructor;

import com.example.api_docker.domain.instructor.event.InstructorCreatedEvent;
import com.example.api_docker.domain.instructor.event.InstructorPasswordChangedEvent;
import com.example.api_docker.domain.instructor.event.InstructorProfileUpdatedEvent;
import com.example.api_docker.domain.shared.DomainEvent;
import com.example.api_docker.domain.shared.exception.DomainException;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.User;
import com.example.api_docker.domain.user.UserId;

import java.time.LocalDateTime;

public class Instructor extends User {

    private String bio;
    private String specialty;
    private String profilePicture;
    private SocialLinks socialLinks;

    private Instructor(UserId id, FullName name, Email email, String passwordHash,
                       String bio, String specialty, String profilePicture,
                       SocialLinks socialLinks, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, createdAt);
        this.bio = bio;
        this.specialty = specialty;
        this.profilePicture = profilePicture;
        this.socialLinks = socialLinks;
    }

    public static Instructor create(FullName name, Email email, String passwordHash,
                                    String bio, String specialty) {
        var instructor = new Instructor(
                UserId.generate(), name, email, passwordHash,
                bio, specialty, null, SocialLinks.empty(),
                LocalDateTime.now()
        );
        instructor.addDomainEvent(new InstructorCreatedEvent(instructor.getId(), email));
        return instructor;
    }

    public static Instructor restore(UserId id, FullName name, Email email,
                                     String passwordHash, String bio, String specialty,
                                     String profilePicture, SocialLinks socialLinks,
                                     LocalDateTime createdAt) {
        return new Instructor(id, name, email, passwordHash, bio, specialty,
                profilePicture, socialLinks, createdAt);
    }

    public void updateProfile(String bio, String specialty) {
        this.bio = bio;
        this.specialty = specialty;
        addDomainEvent(new InstructorProfileUpdatedEvent(getId()));
    }

    public void updateProfilePicture(String profilePicture) {
        if (profilePicture == null || profilePicture.isBlank())
            throw new DomainException("URL da foto não pode ser vazia");

        this.profilePicture = profilePicture;
    }

    public void updateSocialLinks(SocialLinks socialLinks) {
        this.socialLinks = socialLinks;
    }

    @Override
    protected DomainEvent onPasswordChanged() {
        return new InstructorPasswordChangedEvent(getId());
    }

    public String getBio()                { return bio; }
    public String getSpecialty()          { return specialty; }
    public String getProfilePicture()     { return profilePicture; }
    public SocialLinks getSocialLinks()   { return socialLinks; }
}
