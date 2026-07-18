package com.microservice.architecture.overview.resource_service.exception;

public class AzureBlobStorageException extends RuntimeException {
    public AzureBlobStorageException(String message) {
        super(message);
    }
}
