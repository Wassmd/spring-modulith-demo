package com.paxier.spring_modulith_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith
public class SpringModulithDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringModulithDemoApplication.class, args);
	}

}
