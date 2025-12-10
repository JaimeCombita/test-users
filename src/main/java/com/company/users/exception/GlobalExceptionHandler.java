package com.company.users.exception;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message, String path, Map<String, Object> extra) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (path != null) body.put("path", path);
        if (extra != null && !extra.isEmpty()) body.putAll(extra);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(NoSuchResourceFoundException.class)
    public ResponseEntity<Object> handleNotFound(NoSuchResourceFoundException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), path, null);
    }

    @ExceptionHandler(BadResourceRequestException.class)
    public ResponseEntity<Object> handleBadResource(BadResourceRequestException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), path, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadResource(BadCredentialsException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), path, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
                .collect(Collectors.toList());
        Map<String, Object> extra = Map.of("validationErrors", errors);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", path, extra);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadable(HttpMessageNotReadableException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", path, null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        return buildResponse(status, ex.getReason() != null ? ex.getReason() : ex.getMessage(), path, null);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<Object> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        // Este error ocurre p. ej. cuando se pasa id == null a repository.deleteById(...)
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid data access: " + ex.getMostSpecificCause().getMessage(), path, null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", path, Map.of("detail", ex.getMessage()));
    }
}
