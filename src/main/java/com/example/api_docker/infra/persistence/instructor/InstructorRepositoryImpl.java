package com.example.api_docker.infra.persistence.instructor;

import com.example.api_docker.domain.instructor.Instructor;
import com.example.api_docker.domain.instructor.InstructorRepository;
import com.example.api_docker.domain.instructor.SocialLinks;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class InstructorRepositoryImpl implements InstructorRepository {

    private final InstructorJpaRepository jpaRepository;

    @Override
    public void save(Instructor instructor) {
        jpaRepository.save(toJpaEntity(instructor));
    }

    @Override
    public Optional<Instructor> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Instructor> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public boolean existsById(UserId userId) {
        return jpaRepository.existsById(userId.value());
    }

    private InstructorJpaEntity toJpaEntity(Instructor instructor) {
        var entity = new InstructorJpaEntity();
        entity.setId(instructor.getId().value());
        entity.setFirstName(instructor.getName().firstName());
        entity.setLastName(instructor.getName().lastName());
        entity.setEmail(instructor.getEmail().value());
        entity.setPasswordHash(instructor.getPasswordHash());
        entity.setBio(instructor.getBio());
        entity.setSpecialty(instructor.getSpecialty());
        entity.setProfilePicture(instructor.getProfilePicture());
        entity.setLinkedin(instructor.getSocialLinks().linkedin());
        entity.setGithub(instructor.getSocialLinks().github());
        entity.setYoutube(instructor.getSocialLinks().youtube());
        entity.setInstagram(instructor.getSocialLinks().instagram());
        entity.setCreatedAt(instructor.getCreatedAt());
        return entity;
    }

    private Instructor toDomain(InstructorJpaEntity entity) {
        return Instructor.restore(
                new UserId(entity.getId()),
                new FullName(entity.getFirstName(), entity.getLastName()),
                new Email(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getBio(),
                entity.getSpecialty(),
                entity.getProfilePicture(),
                SocialLinks.of(
                        entity.getLinkedin(),
                        entity.getGithub(),
                        entity.getYoutube(),
                        entity.getInstagram()
                ),
                entity.getCreatedAt()
        );
    }
}
