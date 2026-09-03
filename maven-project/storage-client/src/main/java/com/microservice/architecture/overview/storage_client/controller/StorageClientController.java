package com.microservice.architecture.overview.storage_client.controller;

import com.microservice.architecture.overview.storage_client.dto.StorageEntryDTO;
import com.microservice.architecture.overview.storage_client.dto.StorageEntryIDDTO;
import com.microservice.architecture.overview.storage_client.service.StorageClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/storage-client")
public class StorageClientController {
    
    @Autowired
    private StorageClientService storageClientService;

    @GetMapping
    public String getAllStorageEntries(Model model, @AuthenticationPrincipal OidcUser user) {
        log.info("GET getAllStorageEntries invoked");
        List<StorageEntryDTO> storageEntries = storageClientService.getAllStorageEntries();
        model.addAttribute("storages", storageEntries);
        model.addAttribute("user", user);
        log.info("Logged User: {}", user.getName());
        return "storages";
    }

    @GetMapping(params = "type")
    public ResponseEntity<List<StorageEntryDTO>> getAllStorageEntriesByType(@RequestParam("type") String type) {
        log.info("GET getAllStorageEntriesByType invoked");
        return ResponseEntity.ok(storageClientService.getAllStorageEntriesByType(type));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<StorageEntryIDDTO> createStorageEntity(@RequestBody StorageEntryDTO storageRequestBody) {
        log.info("POST createStorageEntity invoked");
        return ResponseEntity.ok(storageClientService.createStorageEntry(storageRequestBody));
    }

    @PostMapping("/create")
    public String createStorageEntity(
            @RequestParam String storageType,
            @RequestParam String containerName,
            @RequestParam String path) {
        log.info("POST createStorageEntity form invoked");
        StorageEntryDTO storageRequest = new StorageEntryDTO(
                null,
                storageType,
                containerName,
                path
        );
        storageClientService.createStorageEntry(storageRequest);
        return "redirect:/storage-client";
    }

    @DeleteMapping
    public String deleteStorageEntriesByIds(@RequestParam("id") String Ids) {
        log.info("DELETE deleteStorageEntriesByIds invoked");
        storageClientService.deleteStorageEntryById(Ids);
        return "redirect:/storage-client";
    }
    
}
