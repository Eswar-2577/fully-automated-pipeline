package com.example.controller;

import com.example.model.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Exposes the profile data at GET /api/profile.
 *
 * The static front end (index.html + app.js) fetches this endpoint on
 * load and renders the Skills / Projects / Education / Certifications
 * sections dynamically, so the page is driven by real server-side data
 * rather than being hard-coded HTML.
 */
@RestController
@RequestMapping("/api")
public class ProfileController {

    @GetMapping("/profile")
    public Profile getProfile() {
        return buildProfile();
    }

    private Profile buildProfile() {

        Profile.ContactInfo contact = new Profile.ContactInfo(
                "+91-7981692577",
                "eswarm2577@gmail.com",
                "Hyderabad, Telangana, India"
        );

        Map<String, List<String>> skills = new java.util.LinkedHashMap<>();
        skills.put("Operating Systems", List.of("Linux (Ubuntu, CentOS, Amazon Linux)"));
        skills.put("Cloud Platforms", List.of("AWS (EC2, S3, IAM, VPC, CloudWatch)", "GCP"));
        skills.put("CI/CD & DevOps Tools", List.of("Jenkins", "Maven", "SonarQube", "Nexus Repository", "Apache Tomcat"));
        skills.put("Containerization", List.of("Docker", "Kubernetes"));
        skills.put("Infrastructure as Code", List.of("Terraform", "Ansible"));
        skills.put("Monitoring & Logging", List.of("Prometheus", "Grafana", "CloudWatch"));
        skills.put("Version Control", List.of("Git", "GitHub"));
        skills.put("Networking", List.of("TCP/IP", "DNS", "HTTP/HTTPS", "Load Balancing", "SSH", "VPC"));

        List<Profile.Project> projects = List.of(
                new Profile.Project(
                        "CI/CD Pipeline using Jenkins, SonarQube & Nexus",
                        "Jenkins · Maven · GitHub · SonarQube · Nexus Repository · Apache Tomcat",
                        List.of(
                                "Designed and implemented an end-to-end CI/CD pipeline integrating Jenkins with Maven, SonarQube quality gates, and Nexus, reducing manual build steps by 80%.",
                                "Automated multi-environment deployments to Apache Tomcat with build notifications, enabling zero-downtime continuous delivery."
                        )
                ),
                new Profile.Project(
                        "AWS Cloud Infrastructure Deployment",
                        "AWS EC2 · IAM · S3 · VPC · CloudWatch · Amazon Linux",
                        List.of(
                                "Provisioned EC2 instances with IAM role-based access controls, S3 policies, and VPC segmentation for secure, isolated cloud environments.",
                                "Implemented CloudWatch monitoring with automated alarms for CPU, memory, and network metrics, maintaining 99%+ uptime."
                        )
                ),
                new Profile.Project(
                        "Authenticated Key Agreement Scheme for Fog Computing in Healthcare",
                        "Cryptography (JCA/JCE) · Fog Computing · IoT Security · Network Security",
                        List.of(
                                "Designed a lightweight authenticated key agreement protocol securing IoT health device to fog node communication, preserving patient data confidentiality and integrity.",
                                "Validated the protocol against replay, impersonation, and man-in-the-middle attacks with minimal computational overhead and low latency."
                        )
                )
        );

        List<Profile.Education> education = List.of(
                new Profile.Education("Bomma Institue of Technology & Science", "Khammam, Telangana",
                        "B.Tech — Computer Science Engineering (AI & ML)", "CGPA: 8.12 / 10", "2022 – 2026"),
                new Profile.Education("Krishnaveni Junior College", "Khammam, Telangana",
                        "Intermediate, Class XII (TSBIE)", "81.4%", "2020 – 2022"),
                new Profile.Education("Adarsha High School", "Telangana",
                        "Secondary School Certificate (SSC), Class X", "GPA: 10.0 / 10", "2020")
        );

        List<Profile.Certification> certifications = List.of(
                new Profile.Certification(
                        "DevOps & Cloud Technologies — Professional Training",
                        "JSpiders JNTU Institute, Hyderabad, Telangana",
                        "Jan 2026 – Jun 2026",
                        "Intensive hands-on training covering Linux, AWS, Git, GitHub, Maven, Jenkins, Docker, Kubernetes, Terraform, Ansible, and monitoring tools through real-world CI/CD and cloud deployment projects."
                )
        );

        String summary = "Computer Science (AI & ML) graduate proficient in Linux administration, AWS cloud "
                + "infrastructure, CI/CD automation, Docker, Kubernetes, Terraform, and Ansible. Specialized "
                + "DevOps training with hands-on expertise in container orchestration, monitoring, and cloud "
                + "provisioning. Seeking an entry-level AWS DevOps Engineer role to design and deploy reliable, "
                + "scalable, and automated infrastructure solutions.";

        return new Profile(
                "Eswar Morla",
                "AWS DevOps Engineer",
                "Aspiring DevOps Engineer building reliable, automated infrastructure",
                contact,
                summary,
                skills,
                projects,
                education,
                certifications
        );
    }
}
