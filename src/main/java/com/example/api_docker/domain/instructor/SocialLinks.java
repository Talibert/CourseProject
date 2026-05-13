package com.example.api_docker.domain.instructor;

public record SocialLinks(String linkedin, String github, String youtube, String instagram) {
    public static SocialLinks empty() {
        return new SocialLinks(null, null, null, null);
    }

    public static SocialLinks of(String linkedin, String github,
                                 String youtube, String instagram) {
        return new SocialLinks(linkedin, github, youtube, instagram);
    }
}
