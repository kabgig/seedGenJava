package com.example.seedChecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = { MongoAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.example.seedChecker.repo")
public class SeedCheckerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeedCheckerApplication.class, args);
	}

}
