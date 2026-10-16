package com.paxier.spring_modulith_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
