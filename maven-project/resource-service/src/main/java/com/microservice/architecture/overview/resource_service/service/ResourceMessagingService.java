package com.microservice.architecture.overview.resource_service.service;


public interface ResourceMessagingService {

    void sendResourceCreatedMessage(Long resourceId);

}
