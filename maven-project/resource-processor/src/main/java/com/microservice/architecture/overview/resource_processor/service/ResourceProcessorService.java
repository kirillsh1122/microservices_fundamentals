package com.microservice.architecture.overview.resource_processor.service;

import com.microservice.architecture.overview.resource_processor.model.ParsedResource;

public interface ResourceProcessorService {

    ParsedResource processResource(byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException;
}
