package com.commerceflow.orderservice.exception;

import com.commerceflow.orderservice.dto.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductUnavailableException.class)
    public ResponseEntity<ApiError> handleProductUnavailableException(ProductUnavailableException e, HttpServletRequest request) {
        return buildApiError(e.getMessage(), request, HttpStatus.NOT_FOUND, null);
    }

    private static ResponseEntity<ApiError> buildApiError(String message, HttpServletRequest request, HttpStatus status, Map<String, String> errors) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                errors));
    }
}
