package com.livetrack.shipment_tracker.infrastructure.common;

import com.livetrack.shipment_tracker.domain.exception.ShipmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JSendResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Invalid request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(JSendResponse.fail(Map.of("error", field)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<JSendResponse<Map<String, String>>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(JSendResponse.fail(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(ShipmentNotFoundException.class)
    public ResponseEntity<JSendResponse<Map<String, String>>> handleNotFound(ShipmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(JSendResponse.fail(Map.of("error", ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSendResponse<Void>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(JSendResponse.error("Ocurrió un error inesperado", "INTERNAL_ERROR"));
    }
}
