package com.microservice.architecture.overview.gateway_service.exception;

import com.microservice.architecture.overview.gateway_service.dto.GatewayErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GatewayErrorResponse> handleNoRoute(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        log.warn(
                "No route found: method={}, path={}, remoteAddress={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr()
        );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "ROUTE_NOT_FOUND",
                "No route is defined for the requested resource.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<GatewayErrorResponse> handleConnectionFailure(
            ResourceAccessException ex,
            HttpServletRequest request) {

        log.warn(
                "Unable to connect to upstream service: method={}, path={}, exception={}: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
        );

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                "The requested service is currently unavailable. Please try again later.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GatewayErrorResponse> handleGatewayNotFound(
            NotFoundException ex,
            HttpServletRequest request) {

        log.warn(
                "Gateway downstream service unavailable: method={}, path={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                "The requested service is currently unavailable. Please try again later.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GatewayErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unexpected gateway error: method={}, path={}, exception={}: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getName(),
                ex.getMessage(),
                ex
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
    }

    private ResponseEntity<GatewayErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            String message,
            String path) {

        GatewayErrorResponse response = new GatewayErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                path
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

}
