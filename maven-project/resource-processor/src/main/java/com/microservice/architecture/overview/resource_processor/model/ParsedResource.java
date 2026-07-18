package com.microservice.architecture.overview.resource_processor.model;


import lombok.Data;

@Data
public class ParsedResource {

    private final String name;
    private final String artist;
    private final String album;
    private final String duration;
    private final String year;

}
