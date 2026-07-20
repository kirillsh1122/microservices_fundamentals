package com.microservice.architecture.overview.resource_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class ResourceMessagingServiceImpl implements ResourceMessagingService {

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Retryable(
            retryFor = KafkaException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public void sendResourceCreatedMessage(Long resourceId) {
        try {
            kafkaTemplate.send("resource-topic", resourceId).get();
            log.info("Sent resource created message for resource ID: {}", resourceId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaException("Interrupted while sending message", e);

        } catch (ExecutionException e) {
            throw new KafkaException("Failed to send Kafka message", e);
        }
    }
}
