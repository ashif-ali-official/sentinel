package com.sentinel.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class AssignmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentServiceApplication.class, args);
	}

}
