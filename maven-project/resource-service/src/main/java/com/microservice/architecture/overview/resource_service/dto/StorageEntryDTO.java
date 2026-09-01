package com.microservice.architecture.overview.resource_service.dto;

public record StorageEntryDTO(
        Long Id,
        String storageType,
        String containerName,
        String path
) {}
