package com.example.model;

import java.util.List;
import java.util.Map;

/**
 * Immutable data model describing the profile that is rendered on the
 * home page and served as JSON from /api/profile.
 *
 * Records are part of core Java (21) and are serialized to JSON by
 * Jackson out of the box, which already ships inside
 * spring-boot-starter-web — so no extra dependency is required.
 */
public record Profile(
        String name,
        String title,
        String tagline,
        ContactInfo contact,
        String summary,
        Map<String, List<String>> skills,
        List<Project> projects,
        List<Education> education,
        List<Certification> certifications
) {

    public record ContactInfo(String phone, String email, String location) {}

    public record Project(String name, String stack, List<String> highlights) {}

    public record Education(String institution, String location, String degree, String detail, String period) {}

    public record Certification(String title, String provider, String period, String detail) {}
}
