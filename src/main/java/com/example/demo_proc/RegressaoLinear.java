package com.example.demo_proc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RegressaoLinear {

	public static void main(String[] args) {
		SpringApplication.run(RegressaoLinear.class, args);
	}

}
