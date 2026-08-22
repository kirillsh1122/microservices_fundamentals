package com.microservice.architecture.overview.resource_service.service;

import java.io.InputStream;

public interface BlobResourceService {

    String uploadResource(byte[] data, String fileName, String stagingContainerName);
    byte[] getResourceByURL(String resourceURL);
    void deleteResourceByURL(String resourceURL);
    String moveResourceToPermanentStorage(String resourceURL, String permanentContainerName, String permanentBlobPath);
}
