package com.microservice.architecture.overview.resource_service.configuration;


import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class BlobContainerClientFactory {

    @Autowired
    private BlobServiceClient blobServiceClient;

    private final ConcurrentMap<String, BlobContainerClient> clients = new ConcurrentHashMap<>();

    public BlobContainerClient getClient(String containerName) {
        return clients.computeIfAbsent(containerName, blobServiceClient::getBlobContainerClient);
    }

}
