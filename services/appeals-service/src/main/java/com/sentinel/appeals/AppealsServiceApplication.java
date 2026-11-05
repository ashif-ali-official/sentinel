package com.sentinel.appeals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppealsServiceApplication {
    public static void main(String[] args) { SpringApplication.run(AppealsServiceApplication.class, args); }
}
