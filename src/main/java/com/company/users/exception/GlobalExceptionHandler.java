package com.company.users.exception;

import com.company.users.crosscutting.ErrorCode;
import com.company.users.crosscutting.ErrorMessage;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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

    private ResponseEntity<Object> buildResponse(ErrorCode errorCode, String path, Map<String, Object> extra) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", errorCode.getStatus().value());
        body.put("error", errorCode.getStatus().getReasonPhrase());
        body.put("message", errorCode.getMessage());
        if (path != null) body.put("path", path);
        if (extra != null && !extra.isEmpty()) body.putAll(extra);
        return new ResponseEntity<>(body, errorCode.getStatus());
    }

    @ExceptionHandler(NoSuchResourceFoundException.class)
    public ResponseEntity<Object> handleNotFound(NoSuchResourceFoundException ex, WebRequest request) {
        return buildResponse(ErrorCode.USER_NOT_FOUND, extractPath(request), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        return buildResponse(ErrorCode.INVALID_CREDENTIALS, extractPath(request), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return buildResponse(ErrorCode.USER_NOT_ACTIVE, extractPath(request), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
                .collect(Collectors.toList());
        Map<String, Object> extra = Map.of("validationErrors", errors);
        return buildResponse(ErrorCode.VALIDATION_FAILED, extractPath(request), extra);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadable(HttpMessageNotReadableException ex, WebRequest request) {
        return buildResponse(ErrorCode.MALFORMED_JSON, extractPath(request), null);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<Object> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex, WebRequest request) {
        Map<String, Object> extra = Map.of("detail", ex.getMostSpecificCause().getMessage());
        return buildResponse(ErrorCode.INVALID_DATA_ACCESS, extractPath(request), extra);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        Map<String, Object> extra = Map.of("detail", ex.getMessage());
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, extractPath(request), extra);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
