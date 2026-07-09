package com.microservice.architecture.overview.resource_service.exception.handler;


import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import com.microservice.architecture.overview.resource_service.exception.ResourceNotFoundException;
import com.microservice.architecture.overview.resource_service.exception.InvalidIdException;
import com.microservice.architecture.overview.resource_service.exception.SongMetadataPostException;
import com.microservice.architecture.overview.resource_service.dto.ErrorResponse;


@ControllerAdvice
public class ResourceServiceGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(), "404");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        MediaType contentType = ex.getContentType();

        String mediaType = contentType != null
                ? contentType.getType() + "/" + contentType.getSubtype()
                : "unknown";

        ErrorResponse error = new ErrorResponse(
                "Invalid file format: " + mediaType + ". Only MP3 files are allowed",
                "400");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String invalidValue = String.valueOf(ex.getValue());
        ErrorResponse error = null;

        if (invalidValue.length() > 200) {
            error = new ErrorResponse(
                    "CSV string is too long: received " + invalidValue.length() + " characters, maximum allowed is 200",
                    "400"
            );
        } else if (invalidValue.contains(",")) {
            String[] values = invalidValue.split(",");
            for (String value : values) {
                String trimmed = value.trim();
                try {
                    Long.parseLong(trimmed);
                } catch (NumberFormatException nfe) {
                    error = new ErrorResponse(
                            "Invalid ID format: '" + trimmed + "'. Only positive integers are allowed",
                            "400"
                    );
                    break;
                }
            }
        } else {
            error = new ErrorResponse(
                        "Invalid value '" + invalidValue + "' for ID. Must be a positive integer",
                        "400");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidId(InvalidIdException ex) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(SongMetadataPostException.class)
    public ResponseEntity<ErrorResponse> handleSongMetadataPost(SongMetadataPostException ex) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

}
