package com.microservice.architecture.overview.resource_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.service.ResourceService;
import com.microservice.architecture.overview.resource_service.dto.ResourceIdResponse;
import com.microservice.architecture.overview.resource_service.dto.DeleteResponse;
import com.microservice.architecture.overview.resource_service.exception.ResourceNotFoundException;


@RestController
@RequestMapping("/resources")
public class ResourceController{
    
    @Autowired
    private ResourceService resourceService;

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResourceById(@PathVariable("id") long resourceId) {
        return resourceService.getResourceById(resourceId)
                .map(resource -> ResponseEntity.ok()
                        .contentType(MediaType.valueOf("audio/mpeg"))
                        .body(resource.getData()))
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=X not found".replace("X", String.valueOf(resourceId))));
    }

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<ResourceIdResponse> createResource(@RequestBody byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException {
        Resource savedResource = resourceService.createResource(data);
        return ResponseEntity.ok(new ResourceIdResponse(savedResource.getId()));
    }

    @DeleteMapping
    public ResponseEntity<DeleteResponse> deleteResourcesByQuery(@RequestParam("id") String resourceIds) {
        List<Long> deletedIds = resourceService.deleteResourceByIds(resourceIds);
        return ResponseEntity.ok(new DeleteResponse(deletedIds));
    }
	
}
