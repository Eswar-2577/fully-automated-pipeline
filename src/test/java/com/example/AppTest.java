package com.example;

import com.example.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests only rely on spring-boot-starter-test (JUnit 5, AssertJ,
 * TestRestTemplate, MockMvc) which is already declared in pom.xml as a
 * test-scoped dependency — nothing new was added.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Verifies the Spring application context starts up cleanly.
    }

    @Test
    void profileEndpointReturnsExpectedData() {
        ResponseEntity<Profile> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/profile", Profile.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Eswar Morla");
        assertThat(response.getBody().title()).isEqualTo("AWS DevOps Engineer");
        assertThat(response.getBody().skills()).isNotEmpty();
        assertThat(response.getBody().projects()).hasSize(3);
        assertThat(response.getBody().education()).hasSize(3);
        assertThat(response.getBody().certifications()).hasSize(1);
    }

    @Test
    void staticHomePageIsServedAndContainsProfileHighlights() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/", String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Eswar Morla");
        assertThat(response.getBody()).contains("AWS DevOps Engineer");
    }
}
