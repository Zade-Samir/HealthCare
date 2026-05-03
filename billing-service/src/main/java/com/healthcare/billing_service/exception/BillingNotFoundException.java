package com.healthcare.billing_service.exception;

public class BillingNotFoundException extends RuntimeException {
    public BillingNotFoundException(String message) {
        super(message);
    }

    // MANDATORY for preserving stack trace
    public BillingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
