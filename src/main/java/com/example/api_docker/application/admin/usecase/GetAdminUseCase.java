package com.example.api_docker.application.admin.usecase;

import com.example.api_docker.application.admin.query.GetAdminQuery;
import com.example.api_docker.application.admin.result.AdminResult;
import com.example.api_docker.domain.admin.Admin;
import com.example.api_docker.domain.admin.AdminRepository;
import com.example.api_docker.domain.admin.exception.AdminNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetAdminUseCase {

    private final AdminRepository adminRepository;

    public AdminResult execute(GetAdminQuery query) {
        Admin admin = adminRepository.findById(query.userId())
                .orElseThrow(() -> new AdminNotFoundException(query.userId()));

        return new AdminResult(
                admin.getId().value(),
                admin.getName().full(),
                admin.getEmail().value(),
                admin.getCreatedAt()
        );
    }
}
