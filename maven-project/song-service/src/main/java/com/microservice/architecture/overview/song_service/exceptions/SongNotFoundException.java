package com.microservice.architecture.overview.song_service.exceptions;


public class SongNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5071646428281007896L;

    public SongNotFoundException(String message) {
        super(message);
    }
    
}
