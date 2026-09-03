package com.microservice.architecture.overview.storage_client.service;

import com.microservice.architecture.overview.storage_client.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_client.dto.StorageEntryIDDTO;

import java.util.List;

public interface StorageClientService {
    List<StorageEntryDTO> getAllStorageEntries();
    List<StorageEntryDTO> getAllStorageEntriesByType(String type);
    StorageEntryIDDTO createStorageEntry(StorageEntryDTO storageEntryDTO);
    List<StorageEntryIDDTO> deleteStorageEntryById(String storageIdsToDelete);
}
