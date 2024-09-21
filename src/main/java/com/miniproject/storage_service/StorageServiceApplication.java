package com.miniproject.storage_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StorageServiceApplication {

	public static void main(String[] args) {
		System.out.println("welcome");
		SpringApplication.run(StorageServiceApplication.class, args);
	}

}
