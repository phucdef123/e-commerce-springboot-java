package com.assignment.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@ComponentScan(basePackages = "com.assignment")
@EnableJpaRepositories(basePackages = "com.assignment.dao")
@EntityScan(basePackages = "com.assignment.entity")
@EnableScheduling 
public class Java5AssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(Java5AssignmentApplication.class, args);
	}

}
