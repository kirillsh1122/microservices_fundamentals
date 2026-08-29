package com.microservice.architecture.overview.auth_server.repository;

import com.microservice.architecture.overview.auth_server.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
