package com.sentinel.sanction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SanctionServiceApplication {
    public static void main(String[] args) { SpringApplication.run(SanctionServiceApplication.class, args); }
}
