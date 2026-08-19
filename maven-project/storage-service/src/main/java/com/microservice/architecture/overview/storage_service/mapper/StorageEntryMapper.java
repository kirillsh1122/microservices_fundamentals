package com.microservice.architecture.overview.storage_service.mapper;

import com.microservice.architecture.overview.storage_service.constants.STORAGE_ENTRY_TYPE;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_service.model.StorageEntry;

public class StorageEntryMapper {

    public static StorageEntryDTO toDTO(StorageEntry storageEntry) {
        return new StorageEntryDTO(
                storageEntry.getId(),
                storageEntry.getStorageType().toString(),
                storageEntry.getContainerName(),
                storageEntry.getPath()
        );
    }

    public static StorageEntry toModelInstance(StorageEntryDTO storageEntryDTO) {
        StorageEntry storageEntry = new StorageEntry();

        storageEntry.setStorageType(STORAGE_ENTRY_TYPE.valueOf(storageEntryDTO.storageType()));
        storageEntry.setContainerName(storageEntryDTO.containerName());
        storageEntry.setPath(storageEntryDTO.path());

        return storageEntry;
    }
}
