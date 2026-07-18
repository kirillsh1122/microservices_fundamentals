package com.microservice.architecture.overview.resource_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResourceMessagingServiceImpl implements ResourceMessagingService {

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Override
    public void sendResourceCreatedMessage(Long resourceId) {
        kafkaTemplate.send("resource-topic", resourceId);
        log.info("Sent resource created message for resource ID: {}", resourceId);
    }
}
