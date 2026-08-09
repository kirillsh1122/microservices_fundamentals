package com.microservice.architecture.overview.e2e.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfiguration {

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;

    @Bean
    BlobContainerClient blobContainerClient(BlobServiceClient serviceClient) {
        BlobContainerClient client = serviceClient.getBlobContainerClient(containerName);
        if (!client.exists()) {
            client.create();
        }
        return client;
    }
}
