package com.microservice.architecture.overview.resource_service.client;

import com.microservice.architecture.overview.resource_service.dto.StorageEntryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageServiceClientFallback implements StorageServiceClient {

    @Override
    public ResponseEntity<List<StorageEntryDTO>> getAllStorageEntries() {
        return ResponseEntity.ok(defaultStorageEntries());
    }

    @Override
    public ResponseEntity<List<StorageEntryDTO>> getAllStorageEntriesByType(String type) {
        return ResponseEntity.ok(
                switch (type) {
                    case "STAGING" -> List.of(defaultStorageEntries().get(0));
                    case "PERMANENT" -> List.of(defaultStorageEntries().get(1));
                    default -> List.of(
                            new StorageEntryDTO(
                                    3333L,
                                    "DEFAULT",
                                    "default-fallback-resource-1",
                                    "files"
                            )
                    );
                }
        );
    }

    private List<StorageEntryDTO> defaultStorageEntries() {
        return List.of(
                new StorageEntryDTO(
                        1111L,
                        "STAGING",
                        "staging-fallback-resource-1",
                        "files"
                ),
                new StorageEntryDTO(
                        2222L,
                        "PERMANENT",
                        "permanent-fallback-resource-1",
                        "files"
                )
        );
    }
}
