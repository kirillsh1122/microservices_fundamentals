package com.microservice.architecture.overview.resource_service.service;


import com.microservice.architecture.overview.resource_service.configuration.BlobContainerClientFactory;
import com.microservice.architecture.overview.resource_service.exception.AzureBlobStorageException;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@Slf4j
public class BlobResourceServiceImpl implements BlobResourceService {

    @Autowired
    private BlobContainerClientFactory blobContainerClientFactory;

    @Retryable(
            retryFor = {AzureBlobStorageException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public String uploadResource(byte[] data, String fileName, String containerName) {
        BlobContainerClient blobContainerClient = blobContainerClientFactory.getClient(containerName);
        log.debug("Uploading resource. fileName: {}, dataSize: {} bytes", fileName, data.length);
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
            blobClient.upload(BinaryData.fromBytes(data));
            String blobUrl = blobClient.getBlobUrl();
            log.info("Successfully uploaded resource. fileName: {}, url: {}", fileName, blobUrl);
            return blobUrl;
        } catch(BlobStorageException e){
            log.error("Failed to upload resource. fileName: {}", fileName, e);
            throw new AzureBlobStorageException(e.getServiceMessage());
        } catch (Exception e){
            log.error("Failed to upload resource. fileName: {}", fileName, e);
            throw new AzureBlobStorageException(e.getMessage());
        }
    }

    @Retryable(
            retryFor = {AzureBlobStorageException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public byte[] getResourceByURL(String resourceURL) {
        log.debug("Retrieving resource. URL: {}", resourceURL);
        try {
            String blobName = getBlobNameFromURL(resourceURL);
            String containerName = getContainerNameFromURL(resourceURL);
            BlobContainerClient blobContainerClient = blobContainerClientFactory.getClient(containerName);
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.downloadStream(outputStream);
            byte[] data = outputStream.toByteArray();
            log.info("Successfully retrieved resource. blobName: {}, dataSize: {} bytes", blobName, data.length);
            return data;
        } catch(BlobStorageException e){
            log.error("Failed to retrieve resource. resourceURL: {}", resourceURL, e);
            throw new AzureBlobStorageException(e.getServiceMessage());
        } catch (Exception e){
            log.error("Failed to retrieve resource. resourceURL: {}", resourceURL, e);
            throw new AzureBlobStorageException(e.getMessage());
        }
    }

    @Retryable(
            retryFor = {AzureBlobStorageException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void deleteResourceByURL(String resourceURL) {
        log.debug("Deleting resource. URL: {}", resourceURL);
        try {
            String blobName = getBlobNameFromURL(resourceURL);
            String containerName = getContainerNameFromURL(resourceURL);
            BlobContainerClient blobContainerClient = blobContainerClientFactory.getClient(containerName);
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.delete();
            log.info("Successfully deleted resource from container: {}. blobName: {}", containerName, blobName);
        } catch(BlobStorageException e){
            log.error("Failed to delete resource. resourceURL: {}", resourceURL, e);
            throw new AzureBlobStorageException(e.getServiceMessage());
        } catch (Exception e){
            log.error("Failed to delete resource. resourceURL: {}", resourceURL, e);
            throw new AzureBlobStorageException(e.getMessage());
        }
    }

    @Override
    public String moveResourceToPermanentStorage(String resourceURL, String permanentContainerName, String permanentBlobPath) {
        String blobName = getBlobNameFromURL(resourceURL);
        String[] segments = blobName.split("/");
        String fileName = segments[segments.length-1];
        BlobContainerClient permanentContainerClient = blobContainerClientFactory.getClient(permanentContainerName);
        BlobClient permanentBlobClient = permanentContainerClient.getBlobClient(permanentBlobPath + "/" + fileName);
        permanentBlobClient.copyFromUrl(resourceURL);
        deleteResourceByURL(resourceURL);
        log.info("Successfully moved resource to permanent storage container {}. originalURL: {}, newURL: {}", permanentContainerName, resourceURL, permanentBlobClient.getBlobUrl());
        return permanentBlobClient.getBlobUrl();
    }

    private String getBlobNameFromURL(String resourceURL) {
        try {
            URI uri = URI.create(resourceURL);
            String path = uri.getPath();
            String[] segments = path.split("/");
            if (segments.length < 2) {
                throw new IllegalArgumentException("Invalid resource URL format. URL: " + resourceURL);
            }
            String blobName = URLDecoder.decode(String.join("/", Arrays.copyOfRange(segments, segments.length-2, segments.length)), StandardCharsets.UTF_8);
//            String blobName = URLDecoder.decode(segments[segments.length-1], StandardCharsets.UTF_8);
            System.out.println("blobName: " + blobName);
            log.info("Extracted blob name from URL. blobName: {}", blobName);
            return blobName;
        } catch (Exception e) {
            log.warn("Failed to extract blob name from URL. URL: {}", resourceURL, e);
            throw e;
        }
    }

    private String getContainerNameFromURL(String resourceURL) {
        try {
            URI uri = URI.create(resourceURL);
            String path = uri.getPath();
            String[] segments = path.split("/");
            if (segments.length < 2) {
                throw new IllegalArgumentException("Invalid resource URL format. URL: " + resourceURL);
            }
            String containerName = URLDecoder.decode(segments[segments.length-3], StandardCharsets.UTF_8);
            if (containerName.isEmpty()) {
                throw new IllegalArgumentException("Container name cannot be empty. URL: " + resourceURL);
            }
            System.out.println("containerName: " + containerName);
            log.info("Extracted container name from URL. containerName: {}", containerName);
            return containerName;
        } catch (Exception e) {
            log.warn("Failed to extract container name from URL. URL: {}", resourceURL, e);
            throw e;
        }
    }
}
