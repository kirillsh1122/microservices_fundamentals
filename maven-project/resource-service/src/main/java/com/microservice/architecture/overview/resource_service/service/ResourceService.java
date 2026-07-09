package com.microservice.architecture.overview.resource_service.service;

import java.util.List;
import java.util.Optional;

import com.microservice.architecture.overview.resource_service.model.Resource;

public interface ResourceService {

	Resource createResource(byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException;
	
	Optional<Resource> getResourceById(long resourceId);
	
	List<Long> deleteResourceByIds(String resourceIds);
}