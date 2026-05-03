package com.healthcare.doctor_service.exception;

import com.healthcare.doctor_service.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDoctorNotFoundException(
            DoctorNotFoundException ex,
            HttpServletRequest request
    ) {
        // 1. Sanitize the path before creating the error response
        String sanitizedPath = HtmlUtils.htmlEscape(request.getRequestURI());

        // 2. Pass the sanitized path to the ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                sanitizedPath // Use the safe path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String message = "Data conflict: Likely a duplicate entry (Email/Phone).";
        if (ex.getMessage().contains("patient_email_key")){
            message = "Email is already registered!";
        }

        // SANITIZATION: Escape the URI to prevent XSS
        String sanitizedPath = HtmlUtils.htmlEscape(request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "CONFLICT",
                message,
                sanitizedPath // Use the sanitized path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
