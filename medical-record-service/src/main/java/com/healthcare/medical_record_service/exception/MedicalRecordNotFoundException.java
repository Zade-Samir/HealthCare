package com.healthcare.medical_record_service.exception;

public class MedicalRecordNotFoundException extends RuntimeException {
    public MedicalRecordNotFoundException(String message) {
        super(message);
    }

    // NEW: Constructor that accepts the 'cause' (e)
    public MedicalRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
