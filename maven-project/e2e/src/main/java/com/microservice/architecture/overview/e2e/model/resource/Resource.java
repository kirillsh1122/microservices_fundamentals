package com.microservice.architecture.overview.e2e.model.resource;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "RESOURCES")
public class Resource {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "RESOURCE_URL")
    private String resourceURL;

}
