package com.microservice.architecture.overview.resource_service.service;


import com.microservice.architecture.overview.resource_service.exception.AzureBlobStorageException;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobStorageException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class BlobResourceServiceImpl implements BlobResourceService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    @Override
    public String uploadResource(byte[] data, String fileName) {
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

    @Override
    public byte[] getResourceByURL(String resourceURL) {
        log.debug("Retrieving resource. URL: {}", resourceURL);
        try {
            String blobName = getBlobNameFromURL(resourceURL);
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

    @Override
    public void deleteResourceByURL(String resourceURL) {
        log.debug("Deleting resource. URL: {}", resourceURL);
        try {
            String blobName = getBlobNameFromURL(resourceURL);
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.delete();
            log.info("Successfully deleted resource. blobName: {}", blobName);
        } catch(BlobStorageException e){
            log.error("Failed to delete resource. resourceURL: {}", resourceURL, e);
            throw new AzureBlobStorageException(e.getServiceMessage());
        } catch (Exception e){
            log.error("Failed to delete resource. resourceURL: {}", resourceURL, e);
            throw new AzureBlobStorageException(e.getMessage());
        }
    }

    private String getBlobNameFromURL(String resourceURL) {
        try {
            URI uri = URI.create(resourceURL);
            String path = uri.getPath();
            String blobName = URLDecoder.decode(path.substring(path.lastIndexOf("/") + 1), StandardCharsets.UTF_8);
            log.debug("Extracted blob name from URL. blobName: {}", blobName);
            return blobName;
        } catch (Exception e) {
            log.warn("Failed to extract blob name from URL. URL: {}", resourceURL, e);
            throw e;
        }
    }
}
