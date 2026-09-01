package com.microservice.architecture.overview.storage_service.service;

import com.microservice.architecture.overview.storage_service.constants.STORAGE_ENTRY_TYPE;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryIDDTO;

import java.util.List;

public interface StorageService {
    List<StorageEntryDTO> getAllStorageEntries();
    List<StorageEntryDTO> getStorageEntriesByType(STORAGE_ENTRY_TYPE type);
    StorageEntryIDDTO createStorageEntry(StorageEntryDTO storageEntry);
    List<StorageEntryIDDTO> deleteStorageEntryById(String storageIdsToDelete);
}
