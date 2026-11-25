package com.hospital.management.entity;

/**
 * Represents a doctor's current scheduling availability.
 */
public enum AvailabilityStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    ON_LEAVE("On Leave");

    private final String displayName;

    AvailabilityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
