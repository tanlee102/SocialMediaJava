package com.example.CloudAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CloudApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(CloudApiApplication.class, args);
	}
}