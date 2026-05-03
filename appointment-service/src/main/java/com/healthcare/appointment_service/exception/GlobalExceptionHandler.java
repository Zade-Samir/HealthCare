package com.healthcare.appointment_service.exception;

import com.healthcare.appointment_service.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentNotFoundException(
            AppointmentNotFoundException ex,
            HttpServletRequest request
    ) {
        // 1. Sanitize the path to prevent XSS attacks
        String sanitizedPath = HtmlUtils.htmlEscape(request.getRequestURI());

        // 2. Create the response with the sanitized path
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                sanitizedPath // Use the sanitized string
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusTransitionException(
            InvalidStatusTransitionException ex,
            HttpServletRequest request
    ) {
        // 1. Sanitize the path before sending it to the client
        String sanitizedPath = HtmlUtils.htmlEscape(request.getRequestURI());

        // 2. Build the safe ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "CONFLICT",
                ex.getMessage(),
                sanitizedPath // Ab yeh path safe hai
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
