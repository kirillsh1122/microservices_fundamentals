package com.microservice.architecture.overview.storage_service.service;

import com.microservice.architecture.overview.storage_service.constants.STORAGE_ENTRY_TYPE;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryIDDTO;
import com.microservice.architecture.overview.storage_service.mapper.StorageEntryMapper;
import com.microservice.architecture.overview.storage_service.model.StorageEntry;
import com.microservice.architecture.overview.storage_service.repository.StorageEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService{

    @Autowired
    private StorageEntryRepository storageEntryRepository;

    public List<StorageEntryDTO> getAllStorageEntries() {
        Iterable<StorageEntry> allStorages = storageEntryRepository.findAll();

        return StreamSupport.stream(allStorages.spliterator(), false)
                .map(StorageEntryMapper::toDTO)
                .toList();
    }

    public List<StorageEntryDTO> getStorageEntriesByType(STORAGE_ENTRY_TYPE type) {
        Iterable<StorageEntry> allStorages = storageEntryRepository.findAllByStorageType(type);

        return StreamSupport.stream(allStorages.spliterator(), false)
                .map(StorageEntryMapper::toDTO)
                .toList();
    }

    public StorageEntryIDDTO createStorageEntry(StorageEntryDTO storageEntryDTO) {
        log.info("createStorageEntry, " + storageEntryDTO.toString());
        StorageEntry storageEntry = StorageEntryMapper.toModelInstance(storageEntryDTO);
        Long id = storageEntryRepository.save(storageEntry).getId();
        return new StorageEntryIDDTO(id);
    }

    public List<StorageEntryIDDTO> deleteStorageEntryById(String storageIdsToDelete) {
        List<Long> storageIdsToDeleteList = Arrays.stream(storageIdsToDelete.split(",")).map(Long::parseLong).toList();
        List<Long> existingIds = storageIdsToDeleteList.stream()
                        .filter(id -> storageEntryRepository.existsById(id))
                        .toList();
        storageEntryRepository.deleteAllById(existingIds);
        return existingIds.stream().map(StorageEntryIDDTO::new).toList();
    }

}
