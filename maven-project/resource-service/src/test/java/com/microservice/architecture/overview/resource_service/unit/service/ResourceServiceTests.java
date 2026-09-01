package com.microservice.architecture.overview.resource_service.unit.service;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.microservice.architecture.overview.resource_service.client.StorageServiceClient;
import com.microservice.architecture.overview.resource_service.dto.StorageEntryDTO;
import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.repository.ResourceRepository;
import com.microservice.architecture.overview.resource_service.service.ResourceServiceImpl;
import com.microservice.architecture.overview.resource_service.service.BlobResourceService;
import com.microservice.architecture.overview.resource_service.client.SongServiceClient;
import com.microservice.architecture.overview.resource_service.utils.SongMetadataParser;

import org.apache.tika.metadata.Metadata;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTests {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private BlobResourceService blobResourceService;

    @Mock
    private SongServiceClient songServiceClient;

    @Mock
    private StorageServiceClient storageServiceClient;

    private final List<StorageEntryDTO> mockedResponse = List.of(
            new StorageEntryDTO(
                    1111L,
                    "STAGING",
                    "staging-test-resource-1",
                    "files"
            ),
            new StorageEntryDTO(
                    2222L,
                    "PERMANENT",
                    "permanent-test-resource-1",
                    "files"
            )
    );

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    public void resourceService_createResource_Resource() {
        // prepare input bytes
        byte[] data = "dummy-audio-data".getBytes();

        // mock metadata extraction (static)
        Metadata metadata = new Metadata();
        metadata.set("dc:title", "MySongTitle");

        try (MockedStatic<SongMetadataParser> mocked = Mockito.mockStatic(SongMetadataParser.class)) {
            mocked.when(() -> SongMetadataParser.extractMetadata(data)).thenReturn(metadata);

            // mock blob upload to return a URL
            String uploadedUrl = "https://blob.example.com/files/MySongTitle-12345.mp3";
            Mockito.when(storageServiceClient.getAllStorageEntriesByType("STAGING"))
                    .thenReturn(ResponseEntity.ok(List.of(mockedResponse.getFirst())));
            Mockito.when(blobResourceService.uploadResource(Mockito.eq(data), Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(uploadedUrl);

            // mock repository save to return resource with id set
            Mockito.when(resourceRepository.save(Mockito.any(Resource.class)))
                    .thenAnswer(invocation -> {
                        Resource r = invocation.getArgument(0);
                        // try to set id via builder if available or setter
                        try {
                            r.setId(1L);
                        } catch (Exception ignored) {
                            // ignore if setter not present
                        }
                        return Resource.builder().id(1L).resourceURL(r.getResourceURL()).build();
                    });

            // execute
            try {
                Resource result = resourceService.createResource(data);

                // assertions
                Assertions.assertNotNull(result);
                Assertions.assertEquals(uploadedUrl, result.getResourceURL());
                Assertions.assertEquals(1L, result.getId());

                // capture filename passed to blob upload and ensure it contains title and .mp3
                ArgumentCaptor<String> filenameCaptor = ArgumentCaptor.forClass(String.class);
                Mockito.verify(blobResourceService).uploadResource(Mockito.eq(data), filenameCaptor.capture(), Mockito.anyString());
                String filename = filenameCaptor.getValue();
                Assertions.assertTrue(filename.contains("MySongTitle"));
                Assertions.assertTrue(filename.endsWith(".mp3"));

                Mockito.verify(resourceRepository).save(Mockito.any(Resource.class));
            } catch (Throwable e) {
                // rethrow as test failure
                Assertions.fail("createResource threw: " + e.getMessage());
            }
        }
    }

    @Test
    public void resourceService_getResourceById_Resource() {
        long validId = 1L;
        Resource resource = Resource.builder().id(validId).resourceURL("https://example.com/resource.mp3").build();
        Mockito.when(resourceRepository.findById(validId)).thenReturn(Optional.of(resource));

        Optional<Resource> result = resourceService.getResourceById(validId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(validId, result.get().getId());
        Assertions.assertEquals("https://example.com/resource.mp3", result.get().getResourceURL());
    }

    @Test
    public void resourceService_deleteResourceByIds_Ids() {
        String ids = "1,2,3";
        Mockito.doNothing().when(resourceRepository).deleteAllById(Mockito.anyList());
        Mockito.when(resourceRepository.existsById(Mockito.anyLong())).thenReturn(true);

        // Execute
        List<Long> result = resourceService.deleteResourceByIds(ids);

        // Assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.contains(1L));
        Assertions.assertTrue(result.contains(2L));
        Assertions.assertTrue(result.contains(3L));

        // Verify that deleteById was called for each ID
        Mockito.verify(resourceRepository).deleteAllById(List.of(1L, 2L, 3L));
    }
}
