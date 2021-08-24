package com.example.translationtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TranslationtalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(TranslationtalkApplication.class, args);
	}

}
