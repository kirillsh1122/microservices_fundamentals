package com.microservice.architecture.overview.resource_service.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.microservice.architecture.overview.resource_service.client.StorageServiceClient;
import com.microservice.architecture.overview.resource_service.dto.StorageEntryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.tika.metadata.Metadata;

import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.repository.ResourceRepository;
import com.microservice.architecture.overview.resource_service.utils.SongMetadataParser;
import com.microservice.architecture.overview.resource_service.client.SongServiceClient;
import com.microservice.architecture.overview.resource_service.exception.InvalidIdException;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SongServiceClient songServiceClient;

    @Autowired
    private StorageServiceClient storageServiceClient;

    @Autowired
    private BlobResourceService blobResourceService;

    @Override
    public Resource createResource(byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException {

        StorageEntryDTO stagingStorageEntry = storageServiceClient.getAllStorageEntriesByType("STAGING")
                .getBody()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No STAGING storage entry found"));
        String containerName = stagingStorageEntry.containerName();
        String blobRootPath = stagingStorageEntry.path();

        Metadata metadata = SongMetadataParser.extractMetadata(data);
        Resource resource = new Resource();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedNow = now.format(formatter);

        String blobName = blobRootPath + "/" + metadata.get("dc:title") + "-" + formattedNow + ".mp3";

        String resourceURL = blobResourceService.uploadResource(
                data,
                blobName,
                containerName
        );
        log.info("Resource blob: {} uploaded to blob container: {}", blobName, containerName);
        resource.setResourceURL(resourceURL);
        resource = resourceRepository.save(resource);
        log.info("Resource created with ID: {} and URL: {}", resource.getId(), resource.getResourceURL());

        return resource;
    }

    @Override
    public Optional<Resource> getResourceById(long resourceId) {
        if (resourceId <= 0) {
            throw new InvalidIdException(
                    "Invalid value '" + resourceId + "' for ID. Must be a positive integer");
        }
        return resourceRepository.findById(resourceId);
    }

    @Override
    public List<Long> deleteResourceByIds(String resourceIds) {

        if (resourceIds.length() > 200) {
            throw new InvalidIdException(
                    "CSV string is too long: received " + resourceIds.length() + " characters, maximum allowed is 200");
        }

        List<Long> resourceIdsParsed = Arrays.stream(resourceIds.split(","))
            .map(String::trim)
            .map(id -> {
                try {
                    return Long.parseLong(id);
                } catch (NumberFormatException e) {
                    throw new InvalidIdException(
                            "Invalid ID format: '" + id + "'. Only positive integers are allowed");
                }
            })
            .map(id -> {
                if (id <= 0) {
                    throw new InvalidIdException(
                            "Invalid value '" + id + "' for ID. Must be a positive integer");
                }
                return id;
            })
            .toList();
        
        List<Long> deletedIds = resourceIdsParsed.stream()
                .filter(resourceRepository::existsById)
                .toList();

        resourceRepository.findAllById(deletedIds).forEach(resource -> {
            blobResourceService.deleteResourceByURL(resource.getResourceURL());
        });
        resourceRepository.deleteAllById(deletedIds);
        songServiceClient.deleteSongMetadata(resourceIds);
        log.info("Cascade deleted resources with IDs: {}", deletedIds);

        return deletedIds;
    }

    @Override
    public void moveResourceToPermanentStorage(long resourceId) {

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with ID: " + resourceId));
        String sourceResourceURL = resource.getResourceURL();

        StorageEntryDTO permanentStorageEntry = storageServiceClient.getAllStorageEntriesByType("PERMANENT")
                .getBody()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No PERMANENT storage entry found"));
        String permanentContainerName = permanentStorageEntry.containerName();
        String permanentBlobRootPath = permanentStorageEntry.path();

        String newResourceURL = blobResourceService.moveResourceToPermanentStorage(sourceResourceURL, permanentContainerName, permanentBlobRootPath);
        resource.setResourceURL(newResourceURL);
        resourceRepository.save(resource);
    }

}
