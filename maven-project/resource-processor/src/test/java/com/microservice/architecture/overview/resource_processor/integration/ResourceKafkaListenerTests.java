package com.microservice.architecture.overview.resource_processor.integration;


import com.microservice.architecture.overview.resource_processor.client.ResourceServiceClient;
import com.microservice.architecture.overview.resource_processor.client.SongServiceClient;
import com.microservice.architecture.overview.resource_processor.dto.SongDTO;
import com.microservice.architecture.overview.resource_processor.messaging.ResourceMessagingService;
import com.microservice.architecture.overview.resource_processor.model.ParsedResource;
import com.microservice.architecture.overview.resource_processor.service.ResourceProcessorService;
import com.microservice.architecture.overview.resource_processor.utils.SongMetadataParser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@CucumberContextConfiguration
@SpringBootTest
public class ResourceKafkaListenerTests {

    @Autowired
    private ResourceMessagingService resourceMessagingService;

    @Autowired
    private ResourceProcessorService resourceProcessorService;

    @MockitoBean
    private ResourceServiceClient resourceServiceClient;

    @MockitoBean
    private SongServiceClient songServiceClient;

    private long resourceId;
    private static byte[] resourceContent = loadTestMp3Data();

    @Given("retrieved resource ID {string} message from resource-service via the kafka topic")
    public void given_resourceId(String id) {
        this.resourceId = Long.parseLong(id);
        when(resourceServiceClient.getResourceById(resourceId)).thenReturn(ResponseEntity.of(Optional.of(resourceContent)));
    }

    @When("resource-processor processes the resource ID")
    public void when_resource_processor_consumes_resource_id() throws TikaException, IOException, SAXException {
        ConsumerRecord<?, Long> record = new ConsumerRecord<>("resource-topic", 0, 0L, null, resourceId);
        resourceMessagingService.handleResourceMessage(record);
    }

    @Then("get the resource from the resource-service")
    public void then_get_resource_from_resource_service() {
        verify(resourceServiceClient).getResourceById(resourceId);
    }

    @And("processed the resource metadata")
    public void and_processed_resource_metadata() throws TikaException, IOException, SAXException {
        Metadata metadata = SongMetadataParser.extractMetadata(resourceContent);
        Assertions.assertNotNull(metadata);
    }

    @And("sent the processed resource metadata to the song-service")
    public void and_sent_processed_resource_metadata_to_song_service() throws TikaException, IOException, SAXException {
        ParsedResource parsedResource = resourceProcessorService.processResource(resourceContent);
        SongDTO songDTO = new SongDTO(
                resourceId,
                parsedResource.getName(),
                parsedResource.getArtist(),
                parsedResource.getAlbum(),
                parsedResource.getDuration(),
                parsedResource.getYear()
        );
        verify(songServiceClient).createSongMetadata(songDTO);
    }

    private static byte[] loadTestMp3Data() {
        try (InputStream is = ResourceKafkaListenerTests.class.getResourceAsStream("/test_data/valid-sample-with-required-tags.mp3")) {
            assert is != null;
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test MP3 data", e);
        }
    }

}
