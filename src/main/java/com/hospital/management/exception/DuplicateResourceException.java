package com.hospital.management.exception;

/**
 * Thrown when creating or updating a record would violate uniqueness constraints
 * (e.g., duplicate phone number or email).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
