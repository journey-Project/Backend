package com.project.Journey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JourneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(JourneyApplication.class, args);
	}


}
