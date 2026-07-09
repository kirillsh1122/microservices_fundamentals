package com.microservice.architecture.overview.resource_service.exception;


public class InvalidIdException extends RuntimeException {
    private static final long serialVersionUID = 5071646428281007896L;

    public InvalidIdException(String message) {
        super(message);
    }
}
