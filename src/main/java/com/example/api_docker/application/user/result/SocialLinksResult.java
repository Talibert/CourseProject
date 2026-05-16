package com.example.api_docker.application.user.result;

import com.example.api_docker.domain.instructor.SocialLinks;

public record SocialLinksResult(
        String linkedin,
        String github,
        String youtube,
        String instagram
) {
    public static SocialLinksResult from(SocialLinks socialLinks) {
        return new SocialLinksResult(
                socialLinks.linkedin(),
                socialLinks.github(),
                socialLinks.youtube(),
                socialLinks.instagram()
        );
    }
}
