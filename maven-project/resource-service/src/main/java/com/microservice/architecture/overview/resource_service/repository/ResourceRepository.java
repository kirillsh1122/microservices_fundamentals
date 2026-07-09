package com.microservice.architecture.overview.resource_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.microservice.architecture.overview.resource_service.model.Resource;


@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {

}
