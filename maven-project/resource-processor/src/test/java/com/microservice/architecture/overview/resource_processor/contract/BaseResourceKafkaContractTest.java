package com.microservice.architecture.overview.resource_processor.contract;

import com.microservice.architecture.overview.resource_processor.messaging.ResourceMessagingService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

//@SpringBootTest
//@Testcontainers
//@AutoConfigureMessageVerifier
//@ActiveProfiles("contracts")
public abstract class BaseResourceKafkaContractTest {

//    @Container
//    @ServiceConnection
//    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
//
//    @Autowired
//    private ResourceMessagingService resourceMessagingService;

    // TBD: Add methods to send messages to Kafka and verify the behavior of ResourceMessagingService

}
