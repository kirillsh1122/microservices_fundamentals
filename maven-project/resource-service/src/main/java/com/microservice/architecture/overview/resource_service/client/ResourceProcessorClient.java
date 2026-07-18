package com.microservice.architecture.overview.resource_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "resource-processor")
public interface ResourceProcessorClient {

    @PostMapping("/resource-processor")
    ResponseEntity<?> processResource(@RequestBody byte[] data);
}
