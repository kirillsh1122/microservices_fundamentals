package com.microservice.architecture.overview.resource_service.integration.service;


import com.microservice.architecture.overview.resource_service.client.SongServiceClient;
import com.microservice.architecture.overview.resource_service.client.StorageServiceClient;
import com.microservice.architecture.overview.resource_service.configuration.AzureBlobConfiguration;
import com.microservice.architecture.overview.resource_service.configuration.BlobContainerClientFactory;
import com.microservice.architecture.overview.resource_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.repository.ResourceRepository;
import com.microservice.architecture.overview.resource_service.service.BlobResourceServiceImpl;
import com.microservice.architecture.overview.resource_service.service.ResourceService;
import com.microservice.architecture.overview.resource_service.service.ResourceServiceImpl;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest(
        classes = {
                ResourceServiceIT.Config.class,
                AzureBlobConfiguration.class,
                BlobContainerClientFactory.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.test.database.replace=none"
})
@Testcontainers
public class ResourceServiceIT {

    @MockitoBean
    private StorageServiceClient storageServiceClient;

    @BeforeEach
    void setup() {
        when(storageServiceClient.getAllStorageEntriesByType(anyString()))
                .thenReturn(ResponseEntity.ok(List.of(
                        new StorageEntryDTO(
                                1111L,
                                "MOCKED",
                                "resource",
                                "files" + UUID.randomUUID()
                        )
                )));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableJpaRepositories(basePackageClasses = ResourceRepository.class)
    @EntityScan(basePackages = "com.microservice.architecture.overview.resource_service.model")
    @Import({
            BlobResourceServiceImpl.class,
            ResourceServiceImpl.class
    })
    static class Config {
    }

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withEnv("POSTGRES_DB", "resource-db")
                    .withEnv("POSTGRES_USER", "postgres")
                    .withEnv("POSTGRES_PASSWORD", "postgres")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql");

    @Container
    @ServiceConnection
    private static final GenericContainer<?> emulator = new GenericContainer<>(
        "mcr.microsoft.com/azure-storage/azurite:3.33.0")
        .withExposedPorts(10000)
        .withCommand("azurite --skipApiVersionCheck && azurite -l /data --blobHost 0.0.0.0 --queueHost 0.0.0.0 --tableHost 0.0.0.0");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @BeforeAll
    static void init() {
        System.out.println(emulator.getLogs());
    }

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceService resourceService;

    @MockitoBean
    private SongServiceClient songServiceClient;

    @Test
    public void resourceService_createResource_Resource() throws IOException, TikaException, SAXException {

        byte [] data;

        try (InputStream is = getClass().getResourceAsStream("/valid-sample-with-required-tags.mp3")) {
            assertNotNull(is);
            data = is.readAllBytes();
        }

        // Act
        Resource resource = resourceService.createResource(data);

        // Returned object
        assertNotNull(resource);
        assertNotNull(resource.getId());
        assertNotNull(resource.getResourceURL());

        // Database
        Resource persisted =
                resourceService.getResourceById(resource.getId()).orElseThrow();

        assertEquals(resource.getId(), persisted.getId());
        assertEquals(resource.getResourceURL(), persisted.getResourceURL());

    }

    @Test
    public void resourceService_deleteResource_Resource() throws IOException, TikaException, SAXException {
        byte [] data;

        try (InputStream is = getClass().getResourceAsStream("/valid-sample-with-required-tags.mp3")) {
            assertNotNull(is);
            data = is.readAllBytes();
        }

        // Arrange
        Resource resource = resourceService.createResource(data);

        // Act
        resourceService.deleteResourceByIds(resource.getId().toString());

        // Database
        assertTrue(resourceService.getResourceById(resource.getId()).isEmpty());
    }

}
