package com.company.users.exception;

import com.company.users.crosscutting.ErrorCode;
import com.company.users.crosscutting.ErrorMessage;
import com.company.users.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, String path, Map<String, Object> extra) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(path)
                .details(extra)
                .build();

        return new ResponseEntity<>(body, errorCode.getStatus());
    }

    @ExceptionHandler({
            NoSuchResourceFoundException.class,
            BadCredentialsException.class,
            AccessDeniedException.class,
            HttpMessageNotReadableException.class,
            InvalidDataAccessApiUsageException.class,
            RecoveryTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleKnownExceptions(Exception ex, WebRequest request) {
        ErrorCode errorCode = mapExceptionToErrorCode(ex);
        Map<String, Object> extra = extractExtra(ex);
        return buildResponse(errorCode, extractPath(request), extra);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
                .collect(Collectors.toList());

        Map<String, Object> extra = Map.of("validationErrors", errors);
        return buildResponse(ErrorCode.VALIDATION_FAILED, extractPath(request), extra);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        Map<String, Object> extra = Map.of("detail", ex.getMessage());
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, extractPath(request), extra);
    }

    private ErrorCode mapExceptionToErrorCode(Exception ex) {
        if (ex instanceof NoSuchResourceFoundException) return ErrorCode.USER_NOT_FOUND;
        if (ex instanceof BadCredentialsException) return ErrorCode.INVALID_CREDENTIALS;
        if (ex instanceof AccessDeniedException) return ErrorCode.USER_NOT_ACTIVE;
        if (ex instanceof HttpMessageNotReadableException) return ErrorCode.MALFORMED_JSON;
        if (ex instanceof RecoveryTokenException rte) return rte.getErrorCode();
        if (ex instanceof InvalidDataAccessApiUsageException) return ErrorCode.INVALID_DATA_ACCESS;
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }

    private Map<String, Object> extractExtra(Exception ex) {
        if (ex instanceof InvalidDataAccessApiUsageException) {
            return Map.of("detail", ((InvalidDataAccessApiUsageException) ex).getMostSpecificCause().getMessage());
        }
        return null;
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
