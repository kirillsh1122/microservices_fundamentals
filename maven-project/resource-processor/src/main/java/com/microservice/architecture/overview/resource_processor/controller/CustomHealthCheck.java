package com.microservice.architecture.overview.resource_processor.controller;

import com.microservice.architecture.overview.resource_processor.model.ContainerEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthCheck implements HealthIndicator {

    @Autowired
    private ContainerEnvironment containerEnvironment;

    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "resource-processor")
                .withDetail("version", containerEnvironment.getAppVersion())
                .withDetail("date", containerEnvironment.getAppDate())
                .withDetail("container", containerEnvironment.getContainerHostName())
                .build();
    }
}
