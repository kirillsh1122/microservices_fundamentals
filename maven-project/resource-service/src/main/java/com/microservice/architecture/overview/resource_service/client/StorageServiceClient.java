package com.microservice.architecture.overview.resource_service.client;


import com.microservice.architecture.overview.resource_service.dto.StorageEntryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "storage-service",
        fallback = StorageServiceClientFallback.class
)
public interface StorageServiceClient {

    @GetMapping("/storages")
    ResponseEntity<List<StorageEntryDTO>> getAllStorageEntries();

    @GetMapping(path = "/storages", params = "type")
    ResponseEntity<List<StorageEntryDTO>> getAllStorageEntriesByType(@RequestParam("type") String type);

}
