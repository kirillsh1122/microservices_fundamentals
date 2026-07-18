package com.microservice.architecture.overview.resource_processor.messaging;


import com.microservice.architecture.overview.resource_processor.client.ResourceServiceClient;
import com.microservice.architecture.overview.resource_processor.client.SongServiceClient;
import com.microservice.architecture.overview.resource_processor.dto.SongDTO;
import com.microservice.architecture.overview.resource_processor.model.ParsedResource;
import com.microservice.architecture.overview.resource_processor.service.ResourceProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;


@Slf4j
@Component
public class ResourceMessagingService{

    @Autowired
    private ResourceProcessorService resourceProcessorService;

    @Autowired
    private ResourceServiceClient resourceServiceClient;
    
    @Autowired
    private SongServiceClient songServiceClient;

    @KafkaListener(topics = "resource-topic", groupId = "resource-processor-group")
    public void handleResourceMessage(ConsumerRecord<?, Long> record) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException {
        Long resourceId = record.value();
        log.info("Received from partition {} with timestamp {}", record.partition(), record.timestamp());
        try {
            byte[] resourceRawData = resourceServiceClient.getResourceById(resourceId).getBody();
            log.info("Fetched resource data from resource-service for resource ID: {}", resourceId);
            ParsedResource parsedResource = resourceProcessorService.processResource(resourceRawData);
            SongDTO songDTO = new SongDTO(
                    resourceId,
                    parsedResource.getName(), 
                    parsedResource.getArtist(), 
                    parsedResource.getAlbum(), 
                    parsedResource.getDuration(), 
                    parsedResource.getYear()
            );
            log.info("Parsed Resource: {}", parsedResource);
            songServiceClient.createSongMetadata(songDTO);
            log.info("Sent song metadata to song-service for resource ID: {}", resourceId);
        } catch (IOException e) {
            log.error("I/O error while processing resource id {}: {}", resourceId, e.getMessage(), e);
            throw e; // declared on method signature
        } catch (TikaException e) {
            log.error("Tika processing error for resource id {}: {}", resourceId, e.getMessage(), e);
            throw e; // declared on method signature
        } catch (SAXException e) {
            log.error("XML parsing error for resource id {}: {}", resourceId, e.getMessage(), e);
            throw e; // declared on method signature
        } catch (org.springframework.web.client.RestClientException e) {
            // Errors when calling other microservices (resource-service, song-service)
            log.error("External service call failed for resource id {}: {}", resourceId, e.getMessage(), e);
            throw new RuntimeException("External service call failed", e);
        } catch (RuntimeException e) {
            // Preserve runtime exceptions but add logging for easier debugging
            log.error("Unexpected runtime exception while handling resource id {}: {}", resourceId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // Fallback for any other checked/unchecked exceptions
            log.error("Unexpected exception while handling resource id {}: {}", resourceId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
