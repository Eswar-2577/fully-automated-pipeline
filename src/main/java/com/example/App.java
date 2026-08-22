package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Aspiring Employee Profile application.
 *
 * Spring Boot auto-scans the com.example package (and sub-packages),
 * so the REST controller in com.example.controller and the model in
 * com.example.model are picked up automatically. No new dependencies
 * were added — this still runs purely on spring-boot-starter-web
 * (which brings in the embedded Tomcat server, Jackson JSON support,
 * and static resource serving) and spring-boot-starter-test.
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
