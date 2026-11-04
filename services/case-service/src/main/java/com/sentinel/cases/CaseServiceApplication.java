package com.sentinel.cases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CaseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaseServiceApplication.class, args);
	}

}
