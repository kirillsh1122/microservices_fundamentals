package com.microservice.architecture.overview.e2e.repository.resource;

import com.microservice.architecture.overview.e2e.model.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

}
