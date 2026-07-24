package com.microservice.architecture.overview.resource_processor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "resource-service")
public interface ResourceServiceClient {

    @GetMapping("/resources/{id}")
    ResponseEntity<byte[]> getResourceById(@PathVariable("id") long resourceId);

    @DeleteMapping("/resources")
    ResponseEntity<?> deleteResourcesByQuery(@RequestParam("id") String resourceIds);

}
