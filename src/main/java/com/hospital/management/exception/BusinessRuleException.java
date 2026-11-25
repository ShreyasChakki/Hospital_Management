package com.hospital.management.exception;

/**
 * Thrown when a domain business rule is violated (e.g., past appointment date,
 * conflicting time slot, unavailable doctor).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
