package com.commerceflow.inventoryservice.exception;

import com.commerceflow.inventoryservice.dto.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ApiError> handleInventoryNotFoundException(InventoryNotFoundException ex, HttpServletRequest request) {
        return buildApiError(ex.getMessage(), request, HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<ApiError> handleInsufficientInventoryException(InsufficientInventoryException ex, HttpServletRequest request) {
        return buildApiError(ex.getMessage(), request, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(InventoryReservationNotFound.class)
    public ResponseEntity<ApiError> handleInventoryReservationNotFound(InventoryReservationNotFound ex, HttpServletRequest request) {
        return buildApiError(ex.getMessage(), request, HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(InventoryReservationAlreadyExists.class)
    public ResponseEntity<ApiError> handleInventoryReservationAlreadyExistsException(InventoryReservationAlreadyExists ex, HttpServletRequest request) {
        return buildApiError(ex.getMessage(), request, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return buildApiError(null, request, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            errors.put(fieldName, violation.getMessage());
        }

        String summary = "Validation failed";
        return buildApiError(summary, request, HttpStatus.BAD_REQUEST, errors);
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
