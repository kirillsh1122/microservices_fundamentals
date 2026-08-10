package com.microservice.architecture.overview.resource_service.contract;


import com.azure.storage.blob.BlobServiceClient;
import com.microservice.architecture.overview.resource_service.configuration.AzureBlobConfiguration;
import com.microservice.architecture.overview.resource_service.controller.ResourceController;
import com.microservice.architecture.overview.resource_service.integration.service.ResourceServiceIT;
import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.service.BlobResourceService;
import com.microservice.architecture.overview.resource_service.service.ResourceMessagingService;
import com.microservice.architecture.overview.resource_service.service.ResourceService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ResourceServiceBaseContractClass.Config.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class ResourceServiceBaseContractClass {

    @Autowired
    private ResourceController resourceController;

    @MockitoBean
    private ResourceService resourceService;

    @MockitoBean
    private BlobResourceService blobResourceService;

    @MockitoBean
    private ResourceMessagingService resourceMessagingService;

    @BeforeEach
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(resourceController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);

        Mockito.when(resourceService.getResourceById(Mockito.anyLong()))
                .thenAnswer(invocation -> {
                    Long id = invocation.getArgument(0);
                    // Simulate creating a resource and returning a Resource object
                    return Optional.of(new Resource(id, "http://example.com/resource/" + id));
                });

        Mockito.when(blobResourceService.getResourceByURL(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String url = invocation.getArgument(0);
                    // Load and return MP3 file bytes from test resources
                    return Files.readAllBytes(Paths.get("src/test/resources/valid-sample-with-required-tags.mp3"));
                });

        try {
            Mockito.when(resourceService.createResource(Mockito.any(byte[].class)))
                    .thenAnswer(invocation -> {
                        byte[] data = invocation.getArgument(0);
                        // Simulate creating a resource and returning a Resource object with a generated ID
                        return new Resource(123L, "http://example.com/resource/1");
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.microservice.architecture.overview.resource_service.model")
    @Import(ResourceController.class)
    static class Config {}

}
