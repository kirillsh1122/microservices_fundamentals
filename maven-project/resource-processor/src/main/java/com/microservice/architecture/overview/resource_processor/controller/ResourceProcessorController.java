package com.microservice.architecture.overview.resource_processor.controller;

import com.microservice.architecture.overview.resource_processor.dto.ResourceProcessorDefaultResponse;
import com.microservice.architecture.overview.resource_processor.service.ResourceProcessorService;
import com.microservice.architecture.overview.resource_processor.model.ParsedResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource-processor")
public class ResourceProcessorController {

    @Autowired
    private ResourceProcessorService resourceProcessorService;

    @PostMapping
    public ResponseEntity<ResourceProcessorDefaultResponse> processorResponse(@RequestBody byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException {
        ParsedResource savedResource = resourceProcessorService.processResource(data);
        return ResponseEntity.ok(new ResourceProcessorDefaultResponse(savedResource.toString()));
    }
}
