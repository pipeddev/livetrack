package com.livetrack.shipment_tracker.infrastructure.common;

import com.livetrack.shipment_tracker.domain.exception.ShipmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(MissingRequestHeaderException ex) {
        return badRequest("MISSING_HEADER",
                "Falta el header requerido '%s'".formatted(ex.getHeaderName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return badRequest("MALFORMED_BODY", "El body no es un JSON válido o tiene tipos incorrectos");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return badRequest("INVALID_PARAMETER",
                "El parámetro '%s' tiene un formato inválido".formatted(ex.getName()));
    }

    /*@ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("Error no controlado", ex); // stack trace completo en logs, nunca en la respuesta
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "code", "INTERNAL_ERROR",
                "message", "Ocurrió un error inesperado"));
    }*/

    private ResponseEntity<Map<String, Object>> badRequest(String code, String message) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", "fail",
                "code", code,
                "message", message));
    }
}
