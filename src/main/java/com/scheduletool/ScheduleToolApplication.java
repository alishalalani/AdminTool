package com.scheduletool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application
 * Replaces the Swing-based Main.java
 */
@SpringBootApplication
public class ScheduleToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleToolApplication.class, args);
    }
}

