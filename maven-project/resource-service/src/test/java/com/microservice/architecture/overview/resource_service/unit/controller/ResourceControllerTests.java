package com.microservice.architecture.overview.resource_service.unit.controller;

import com.microservice.architecture.overview.resource_service.controller.ResourceController;
import com.microservice.architecture.overview.resource_service.exception.ResourceNotFoundException;
import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.service.BlobResourceService;
import com.microservice.architecture.overview.resource_service.service.ResourceMessagingService;
import com.microservice.architecture.overview.resource_service.service.ResourceService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ResourceController.class)
public class ResourceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @MockitoBean
    private BlobResourceService blobResourceService;

    @MockitoBean
    private ResourceMessagingService resourceMessagingService;

    // GET /resources/{id} Tests
    
    @Test
    public void testGetResourceById_Success() throws Exception {

        long resourceId = 1L;
        String resourceURL = "http://example.com/resource/1";
        byte[] audioData = "mock audio data".getBytes();

        Resource mockResource = new Resource();
        mockResource.setId(resourceId);
        mockResource.setResourceURL(resourceURL);

        when(resourceService.getResourceById(resourceId)).thenReturn(Optional.of(mockResource));
        when(blobResourceService.getResourceByURL(resourceURL)).thenReturn(audioData);

        mockMvc.perform(get("/resources/{id}", resourceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("audio/mpeg"))
                .andExpect(content().bytes(audioData));

        verify(resourceService, times(1)).getResourceById(resourceId);
        verify(blobResourceService, times(1)).getResourceByURL(resourceURL);
    }

    @Test
    public void testGetResourceById_ResourceNotFound() throws Exception {

        long resourceId = 999L;
        when(resourceService.getResourceById(resourceId)).thenReturn(Optional.empty());

        // Act & Assert - When resource not found, exception is thrown
        mockMvc.perform(get("/resources/{id}", resourceId))
                .andExpect(result -> {
                    Assertions.assertNotNull(result.getResolvedException());
                    Assertions.assertInstanceOf(
                            ResourceNotFoundException.class,
                            result.getResolvedException()
                    );
                });

        verify(resourceService, times(1)).getResourceById(resourceId);
        verify(blobResourceService, never()).getResourceByURL(anyString());
    }

    @Test
    public void testGetResourceById_ExceptionThrown() throws Exception {
        long resourceId = 1L;
        when(resourceService.getResourceById(resourceId))
                .thenThrow(new ResourceNotFoundException("Resource with ID=1 not found"));

        mockMvc.perform(get("/resources/{id}", resourceId))
                .andExpect(result -> {
                    Assertions.assertNotNull(result.getResolvedException());
                    Assertions.assertInstanceOf(
                            ResourceNotFoundException.class,
                            result.getResolvedException()
                    );
                });

        verify(resourceService, times(1)).getResourceById(resourceId);
    }

    // POST /resources Tests

    @Test
    public void testCreateResource_Success() throws Exception {

        long resourceId = 1L;
        byte[] audioData = "mock audio data".getBytes();

        Resource savedResource = new Resource();
        savedResource.setId(resourceId);
        savedResource.setResourceURL("http://example.com/resource/1");

        when(resourceService.createResource(audioData)).thenReturn(savedResource);

        mockMvc.perform(post("/resources")
                .contentType("audio/mpeg")
                .content(audioData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) resourceId)));

        verify(resourceService, times(1)).createResource(audioData);
        verify(resourceMessagingService, times(1)).sendResourceCreatedMessage(resourceId);
    }

    @Test
    public void testDeleteResourcesByQuery_SingleId() throws Exception {

        String resourceIds = "1";
        List<Long> deletedIds = Collections.singletonList(1L);

        when(resourceService.deleteResourceByIds(resourceIds)).thenReturn(deletedIds);

        mockMvc.perform(delete("/resources")
                .queryParam("id", resourceIds))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]", is(1)));

        verify(resourceService, times(1)).deleteResourceByIds(resourceIds);
    }

    @Test
    public void testDeleteResourcesByQuery_MultipleIds() throws Exception {

        String resourceIds = "1,2,3";
        List<Long> deletedIds = List.of(1L, 2L, 3L);

        when(resourceService.deleteResourceByIds(resourceIds)).thenReturn(deletedIds);

        mockMvc.perform(delete("/resources")
                .queryParam("id", resourceIds))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids[0]", is(1)))
                .andExpect(jsonPath("$.ids[1]", is(2)))
                .andExpect(jsonPath("$.ids[2]", is(3)));

        verify(resourceService, times(1)).deleteResourceByIds(resourceIds);
    }

    @Test
    public void testDeleteResourcesByQuery_NoIdsDeleted() throws Exception {

        String resourceIds = "999";
        List<Long> deletedIds = Collections.emptyList();

        when(resourceService.deleteResourceByIds(resourceIds)).thenReturn(deletedIds);

        mockMvc.perform(delete("/resources")
                .queryParam("id", resourceIds))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", is(deletedIds)));

        verify(resourceService, times(1)).deleteResourceByIds(resourceIds);
    }

    @Test
    public void testDeleteResourcesByQuery_EmptyQueryParam() throws Exception {

        String resourceIds = "";
        List<Long> deletedIds = Collections.emptyList();

        when(resourceService.deleteResourceByIds(resourceIds)).thenReturn(deletedIds);

        mockMvc.perform(delete("/resources")
                .queryParam("id", resourceIds))
                .andExpect(status().isOk());

        verify(resourceService, times(1)).deleteResourceByIds(resourceIds);
    }
}
