package com.example.api_docker.infra.persistence.admin;

import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.user.Email;
import com.example.api_docker.domain.user.FullName;
import com.example.api_docker.domain.user.UserId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminRepositoryImpl implements AdminRepository {

    private final AdminJpaRepository jpaRepository;

    public AdminRepositoryImpl(AdminJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Admin admin) {
        jpaRepository.save(toJpaEntity(admin));
    }

    @Override
    public Optional<Admin> findById(UserId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Admin> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    private AdminJpaEntity toJpaEntity(Admin admin) {
        var entity = new AdminJpaEntity();
        entity.setId(admin.getId().value());
        entity.setFirstName(admin.getName().firstName());
        entity.setLastName(admin.getName().lastName());
        entity.setEmail(admin.getEmail().value());
        entity.setPasswordHash(admin.getPasswordHash());
        entity.setCreatedAt(admin.getCreatedAt());
        return entity;
    }

    private Admin toDomain(AdminJpaEntity entity) {
        return Admin.restore(
                new UserId(entity.getId()),
                new FullName(entity.getFirstName(), entity.getLastName()),
                new Email(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getCreatedAt()
        );
    }
}
