package com.vehicleoem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.vehicleoem.controller")
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotFoundException(VehicleNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(SubscriptionException.class)
    public ResponseEntity<ErrorResponse> handleSubscriptionException(SubscriptionException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(KeyManagementException.class)
    public ResponseEntity<ErrorResponse> handleKeyManagementException(KeyManagementException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed: " + errors.toString(),
            LocalDateTime.now()
        );
        error.setValidationErrors(errors);
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
            "INVALID_ARGUMENT",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
            "INVALID_JSON",
            "Invalid JSON format: " + ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(
            "UNSUPPORTED_MEDIA_TYPE",
            "Unsupported media type: " + ex.getContentType(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred: " + ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> validationErrors;
        
        public ErrorResponse(String errorCode, String message, LocalDateTime timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        // Getters and setters
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
    }
}
