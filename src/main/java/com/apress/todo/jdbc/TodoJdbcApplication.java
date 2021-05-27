package com.apress.todo.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoJdbcApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(TodoJdbcApplication.class);
		app.run(args);
	}

}
