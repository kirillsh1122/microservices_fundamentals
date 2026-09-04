package com.microservice.architecture.overview.resource_processor.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class ConsumerConfiguration {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Long> kafkaListenerContainerFactory(
            ConsumerFactory<String, Long> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Long>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setObservationEnabled(true);
        return factory;
    }

}
