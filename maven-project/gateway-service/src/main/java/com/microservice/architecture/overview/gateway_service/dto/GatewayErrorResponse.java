package com.microservice.architecture.overview.gateway_service.dto;

import java.time.Instant;

public record GatewayErrorResponse (
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}
