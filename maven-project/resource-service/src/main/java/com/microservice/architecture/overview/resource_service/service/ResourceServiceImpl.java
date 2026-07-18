package com.microservice.architecture.overview.resource_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.tika.metadata.Metadata;

import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.repository.ResourceRepository;
import com.microservice.architecture.overview.resource_service.utils.SongMetadataParser;

import feign.FeignException;

import com.microservice.architecture.overview.resource_service.client.SongServiceClient;
import com.microservice.architecture.overview.resource_service.client.ResourceProcessorClient;
import com.microservice.architecture.overview.resource_service.dto.SongDTO;
import com.microservice.architecture.overview.resource_service.exception.InvalidIdException;
import com.microservice.architecture.overview.resource_service.exception.SongMetadataPostException;


@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SongServiceClient songServiceClient;

    @Autowired
    private ResourceProcessorClient resourceProcessorClient;

    @Autowired
    private BlobResourceServiceImpl blobResourceService;

    @Override
    public Resource createResource(byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException {
        
        Metadata metadata = SongMetadataParser.extractMetadata(data);
        Resource resource = new Resource();
        String resourceURL = blobResourceService.uploadResource(data, metadata.get("dc:title") + "-" + UUID.randomUUID() + ".mp3");
        resource.setResourceURL(resourceURL);
        resource = resourceRepository.save(resource);

        SongDTO songMetadata = new SongDTO(
            resource.getId(), 
            metadata.get("dc:title"), 
            metadata.get("xmpDM:artist"), 
            metadata.get("xmpDM:album"), 
            formatDuration(metadata.get("xmpDM:duration")), 
            metadata.get("xmpDM:releaseDate")
        );

        try {
            songServiceClient.createSongMetadata(songMetadata);
            resourceProcessorClient.processResource(data);
        } catch (FeignException.BadRequest e) {
            resourceRepository.deleteById(resource.getId());
            blobResourceService.deleteResourceByURL(resourceURL);
            throw new SongMetadataPostException("Failed to post song metadata for ID=" + resource.getId() + ": " + e.contentUTF8());
        }

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
        return deletedIds;

    }

    private String formatDuration(String durationSeconds) {
        if (durationSeconds == null || durationSeconds.isEmpty()) {
            return "00:00";
        }
        try {
            double seconds = Double.parseDouble(durationSeconds);
            int minutes = (int) seconds / 60;
            int secs = (int) seconds % 60;
            return String.format("%02d:%02d", minutes, secs);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }

}
