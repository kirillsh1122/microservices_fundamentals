package com.microservice.architecture.overview.storage_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class StorageClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorageClientApplication.class, args);
	}

}
