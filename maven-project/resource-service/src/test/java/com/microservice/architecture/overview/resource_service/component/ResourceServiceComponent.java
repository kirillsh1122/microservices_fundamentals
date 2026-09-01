package com.microservice.architecture.overview.resource_service.component;

import com.microservice.architecture.overview.resource_service.client.SongServiceClient;
import com.microservice.architecture.overview.resource_service.client.StorageServiceClient;
import com.microservice.architecture.overview.resource_service.configuration.AzureBlobConfiguration;
import com.microservice.architecture.overview.resource_service.configuration.BlobContainerClientFactory;
import com.microservice.architecture.overview.resource_service.controller.ResourceController;
import com.microservice.architecture.overview.resource_service.dto.ResourceIdResponse;
import com.microservice.architecture.overview.resource_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.resource_service.repository.ResourceRepository;
import com.microservice.architecture.overview.resource_service.service.BlobResourceServiceImpl;
import com.microservice.architecture.overview.resource_service.service.ResourceMessagingServiceImpl;
import com.microservice.architecture.overview.resource_service.service.ResourceServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest(
        classes = {
                ResourceServiceComponent.ResourceConfig.class,
                AzureBlobConfiguration.class,
                BlobContainerClientFactory.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext
@Testcontainers
public class ResourceServiceComponent {

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
            ResourceServiceImpl.class,
            ResourceMessagingServiceImpl.class,
            ResourceController.class
    })
    static class ResourceConfig {
    }

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withEnv("POSTGRES_DB", "resource-db")
                    .withEnv("POSTGRES_USER", "postgres")
                    .withEnv("POSTGRES_PASSWORD", "postgres")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql");

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0")).withKraft()
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
            .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
            .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1");

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
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @MockitoBean
    private SongServiceClient songServiceClient;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testCreateResource() throws IOException {

        byte[] data;

        try (InputStream is = getClass().getResourceAsStream("/valid-sample-with-required-tags.mp3")) {
            assertNotNull(is, "Test resource file not found");
            data = is.readAllBytes();
        }

        this.webTestClient
                .post()
                .uri("/resources")
                .contentType(MediaType.valueOf("audio/mpeg"))
                .bodyValue(data)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ResourceIdResponse.class)
                .consumeWith(response -> {
                    ResourceIdResponse body = response.getResponseBody();
                    assertNotNull(body, "Response body should not be null");
                    assertTrue(body.id() > 0, "Resource ID should be positive");
                });
    }

}
