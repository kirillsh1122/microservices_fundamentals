package com.microservice.architecture.overview.storage_service.controller;

import com.microservice.architecture.overview.storage_service.constants.STORAGE_ENTRY_TYPE;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryIDDTO;
import com.microservice.architecture.overview.storage_service.service.StorageService;
import com.microservice.architecture.overview.storage_service.validation.FormatAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/storages")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping
    public ResponseEntity<List<StorageEntryDTO>> getAllStorageEntries() {
        log.info("GET getAllStorageEntries invoked");
        return ResponseEntity.ok(storageService.getAllStorageEntries());
    }

    @GetMapping(params = "type")
    public ResponseEntity<List<StorageEntryDTO>> getAllStorageEntriesByType(@RequestParam("type") String type) {
        log.info("GET getAllStorageEntriesByType invoked");
        return ResponseEntity.ok(storageService.getStorageEntriesByType(STORAGE_ENTRY_TYPE.fromValue(type)));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<StorageEntryIDDTO> createStorageEntity(@RequestBody StorageEntryDTO storageRequestBody) {
        log.info("POST createStorageEntity invoked");
        return ResponseEntity.ok(storageService.createStorageEntry(storageRequestBody));
    }

    @DeleteMapping
    public ResponseEntity<List<StorageEntryIDDTO>> deleteStorageEntriesByIds(@FormatAllowed @RequestParam("id") String Ids) {
        log.info("DELETE deleteStorageEntriesByIds invoked");
        return ResponseEntity.ok(storageService.deleteStorageEntryById(Ids));
    }

}
