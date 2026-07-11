package com.microservice.architecture.overview.resource_service.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class BlobResourceServiceImpl implements BlobResourceService {

    static final String BLOB_RESOURCE_PATTERN = "azure-blob://%s/%s";

    @Autowired
    private BlobContainerClient blobContainerClient;

    @Override
    public String uploadResource(byte[] data, String fileName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        blobClient.upload(BinaryData.fromBytes(data));
        return blobClient.getBlobUrl();
    }

    @Override
    public byte[] getResourceByURL(String resourceURL) {
        BlobClient blobClient = blobContainerClient.getBlobClient(resourceURL);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.downloadStream(outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public void deleteResourceByURL(String resourceURL) {
        BlobClient blobClient = blobContainerClient.getBlobClient(resourceURL);
        blobClient.delete();
    }
}
