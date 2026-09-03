package com.microservice.architecture.overview.storage_client.client;

import com.microservice.architecture.overview.storage_client.config.StorageFeignConfig;
import com.microservice.architecture.overview.storage_client.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_client.dto.StorageEntryIDDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "storage-service",
        fallback = StorageServiceClientFallback.class,
        configuration = StorageFeignConfig.class
)
public interface StorageServiceClient {

    @GetMapping("/storages")
    ResponseEntity<List<StorageEntryDTO>> getAllStorageEntries();

    @GetMapping(path = "/storages", params = "type")
    ResponseEntity<List<StorageEntryDTO>> getAllStorageEntriesByType(@RequestParam("type") String type);

    @PostMapping(path = "/storages", consumes = "application/json")
    public ResponseEntity<StorageEntryIDDTO> createStorageEntity(@RequestBody StorageEntryDTO storageRequestBody);

    @DeleteMapping(path = "/storages")
    public ResponseEntity<List<StorageEntryIDDTO>> deleteStorageEntriesByIds(@RequestParam("id") String Ids);
}
