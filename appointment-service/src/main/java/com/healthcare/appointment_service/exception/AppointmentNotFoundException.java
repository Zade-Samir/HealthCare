package com.healthcare.appointment_service.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String message) {
        super(message);
    }

    // NEW: Constructor for message + the actual cause (Exception Chaining)
    public AppointmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
