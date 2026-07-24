package com.microservice.architecture.overview.resource_processor.dto;

public record SongDTO(
        long id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {}
