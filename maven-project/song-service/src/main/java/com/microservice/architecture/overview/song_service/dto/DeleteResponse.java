package com.microservice.architecture.overview.song_service.dto;


import java.util.List;

public record DeleteResponse(List<Long> ids) {
    
}