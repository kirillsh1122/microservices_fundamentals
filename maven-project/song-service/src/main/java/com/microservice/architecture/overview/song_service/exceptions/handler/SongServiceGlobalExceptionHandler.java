package com.microservice.architecture.overview.song_service.exceptions.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import com.microservice.architecture.overview.song_service.exceptions.SongNotFoundException;
import com.microservice.architecture.overview.song_service.dto.ErrorResponse;
import com.microservice.architecture.overview.song_service.dto.ConstraintViolationResponse;
import com.microservice.architecture.overview.song_service.exceptions.DuplicatedMetadataException;
import com.microservice.architecture.overview.song_service.exceptions.InvalidIdException;

import java.util.Map;

@ControllerAdvice
public class SongServiceGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSongNotFound(SongNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(), "404");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(DuplicatedMetadataException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedMetadata(DuplicatedMetadataException ex) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(), "409");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ConstraintViolationResponse> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> constraintViolations = ex.getConstraintViolations().stream().collect(java.util.stream.Collectors.toMap(
            violation -> violation.getPropertyPath().toString(),
            violation -> violation.getMessage(),
            (existing, replacement) -> {
                if (existing.contains("required")) {
                    return replacement;
                } else {
                    return existing;
                }
            }
        ));

        ConstraintViolationResponse error = new ConstraintViolationResponse(constraintViolations, "400", "Validation error");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String invalidValue = String.valueOf(ex.getValue());
        ErrorResponse error = new ErrorResponse(
            "Invalid value '" + invalidValue + "' for ID. Must be a positive integer",
            "400");

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
    
}
