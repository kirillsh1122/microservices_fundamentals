package com.microservice.architecture.overview.resource_service.service;

public interface BlobResourceService {

    String uploadResource(byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException;
    byte[] getResourceByURL(String resourceURL) throws java.io.IOException;
}
