package com.microservice.architecture.overview.resource_service.exception;


public class ResourceNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 5071646428281007896L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}
