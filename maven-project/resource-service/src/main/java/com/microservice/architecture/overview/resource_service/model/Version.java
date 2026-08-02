package com.microservice.architecture.overview.resource_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Version {
    private String version;
    private String date;
}
