package com.microservice.architecture.overview.auth_server;

import com.microservice.architecture.overview.auth_server.model.User;
import com.microservice.architecture.overview.auth_server.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

	@Bean
	public ApplicationRunner dataLoader(
			UserRepository repo, PasswordEncoder encoder) {
		return args -> {
			repo.save(
					new User("ADMIN", encoder.encode("admin_password"), "ROLE_ADMIN"));
			repo.save(
					new User("USER", encoder.encode("user_password"), "ROLE_GUEST"));
		};
	}

}
