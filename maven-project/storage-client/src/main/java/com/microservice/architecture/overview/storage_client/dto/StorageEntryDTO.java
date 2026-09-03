package com.microservice.architecture.overview.storage_client.dto;

public record StorageEntryDTO(
        Long Id,
        String storageType,
        String containerName,
        String path
) {}
