package com.healthcare.patient_service.exception;

import com.healthcare.patient_service.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        // 2. SANITIZE the Request URI before adding it to the response
        String sanitizedPath = HtmlUtils.htmlEscape(request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                message,
                sanitizedPath // Use the sanitized string
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFoundException(
            PatientNotFoundException ex,
            HttpServletRequest request
    ) {

        // Escape the URI to neutralize any malicious script tags
        String sanitizedPath = HtmlUtils.htmlEscape(request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                sanitizedPath // Use the sanitized path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String message = "Data conflict: Likely a duplicate entry (Email/Phone).";
        if (ex.getMessage() != null && ex.getMessage().contains("patient_email_key")){
            message = "Email is already registered!";
        }

        // SANITIZATION STEP: Escape the URI to prevent XSS
        String sanitizedPath = HtmlUtils.htmlEscape(request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "CONFLICT",
                message,
                sanitizedPath // Use sanitized version to don't show html error directly on UI(else hacker can see it)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
