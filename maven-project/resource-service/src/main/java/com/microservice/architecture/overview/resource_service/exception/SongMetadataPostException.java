package com.microservice.architecture.overview.resource_service.exception;

public class SongMetadataPostException extends RuntimeException {
    
    private static final long serialVersionUID = 5071646428281007896L;

    public SongMetadataPostException(String message) {
        super(message);
    }
    
}
