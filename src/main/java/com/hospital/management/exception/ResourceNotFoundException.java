package com.hospital.management.exception;

/**
 * Thrown when a requested entity (Patient, Doctor, Appointment, MedicalRecord) does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
