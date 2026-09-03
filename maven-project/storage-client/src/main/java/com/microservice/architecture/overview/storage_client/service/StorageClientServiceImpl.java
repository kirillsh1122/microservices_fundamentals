package com.microservice.architecture.overview.storage_client.service;

import com.microservice.architecture.overview.storage_client.client.StorageServiceClient;
import com.microservice.architecture.overview.storage_client.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_client.dto.StorageEntryIDDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageClientServiceImpl implements StorageClientService {

    @Autowired
    private StorageServiceClient storageServiceClient;

    @Override
    public List<StorageEntryDTO> getAllStorageEntries() {
        return storageServiceClient.getAllStorageEntries()
                .getBody()
                .stream()
                .toList();
    }

    @Override
    public List<StorageEntryDTO> getAllStorageEntriesByType(String type) {
        return storageServiceClient.getAllStorageEntriesByType(type)
                .getBody()
                .stream()
                .toList();
    }

    @Override
    public StorageEntryIDDTO createStorageEntry(StorageEntryDTO storageEntryDTO) {
        return storageServiceClient.createStorageEntity(storageEntryDTO).getBody();
    }

    @Override
    public List<StorageEntryIDDTO> deleteStorageEntryById(String storageIdsToDelete) {
        return storageServiceClient.deleteStorageEntriesByIds(storageIdsToDelete).getBody();
    }
}
