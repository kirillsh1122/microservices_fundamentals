package com.microservice.architecture.overview.song_service.exceptions;

public class DuplicatedMetadataException extends RuntimeException {

    private static final long serialVersionUID = 5071646428281007896L;

    public DuplicatedMetadataException(String message) {
        super(message);
    }
    
}
