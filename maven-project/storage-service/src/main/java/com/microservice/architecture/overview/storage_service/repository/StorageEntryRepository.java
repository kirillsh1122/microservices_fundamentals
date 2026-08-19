package com.microservice.architecture.overview.storage_service.repository;

import com.microservice.architecture.overview.storage_service.constants.STORAGE_ENTRY_TYPE;
import com.microservice.architecture.overview.storage_service.model.StorageEntry;
import org.springframework.data.repository.CrudRepository;


public interface StorageEntryRepository extends CrudRepository<StorageEntry, Long> {

    Iterable<StorageEntry> findByStorageEntryType(STORAGE_ENTRY_TYPE storageEntryType);

}
