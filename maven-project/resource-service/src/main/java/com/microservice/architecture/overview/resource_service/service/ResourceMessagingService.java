package com.microservice.architecture.overview.resource_service.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ResourceMessagingService {

    void sendResourceCreatedMessage(Long resourceId);
    void retrieveResourceProcessedMessage(ConsumerRecord<?, Long> record);
}
