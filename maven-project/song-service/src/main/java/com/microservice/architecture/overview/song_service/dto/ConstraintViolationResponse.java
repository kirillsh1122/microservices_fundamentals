package com.microservice.architecture.overview.song_service.dto;


import java.util.Map;

public record ConstraintViolationResponse(Map<String, String> details, String errorCode, String errorMessage) {
    
}
