package com.microservice.architecture.overview.storage_service.controller;

import com.microservice.architecture.overview.storage_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_service.dto.StorageEntryIDDTO;
import com.microservice.architecture.overview.storage_service.service.StorageService;
import com.microservice.architecture.overview.storage_service.validation.FormatAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/storages")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping
    public ResponseEntity<List<StorageEntryDTO>> getAllStorageEntries() {
        return ResponseEntity.ok(storageService.getAllStorageEntries());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<StorageEntryIDDTO> createStorageEntity(@RequestBody StorageEntryDTO storageRequestBody) {
        return ResponseEntity.ok(storageService.createStorageEntry(storageRequestBody));
    }

    @DeleteMapping
    public ResponseEntity<List<StorageEntryIDDTO>> deleteStorageEntriesByIds(@FormatAllowed @RequestParam("id") String Ids) {
        return ResponseEntity.ok(storageService.deleteStorageEntryById(Ids));
    }

}
