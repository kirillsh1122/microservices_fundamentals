package com.microservice.architecture.overview.resource_service.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class BlobResourceServiceImpl implements BlobResourceService {

    static final String BLOB_RESOURCE_PATTERN = "azure-blob://%s/%s";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;

    @Override
    public String uploadResource(byte[] data) throws IOException {
        Resource resource = resourceLoader.getResource(String.format(BLOB_RESOURCE_PATTERN, this.containerName, UUID.randomUUID()));
        try (OutputStream os = ((WritableResource) resource).getOutputStream()) {
            os.write(data);
        }
        return resource.getURL().toString();
    }

    @Override
    public byte[] getResourceByURL(String resourceURL) throws IOException {
        Resource resource = resourceLoader.getResource(resourceURL);
        return resource.getInputStream().readAllBytes();
    }
}
