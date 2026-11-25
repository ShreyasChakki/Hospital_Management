package com.hospital.management.dto;

import com.hospital.management.entity.AvailabilityStatus;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for creating, updating, and displaying {@code Doctor} records.
 */
public class DoctorDto {

    private Long id;

    private String doctorCode;

    @NotBlank(message = "Doctor name is required.")
    @Size(min = 2, max = 120, message = "Doctor name must be between 2 and 120 characters.")
    private String fullName;

    @NotBlank(message = "Specialization is required.")
    @Size(max = 100, message = "Specialization must not exceed 100 characters.")
    private String specialization;

    @NotBlank(message = "Qualification is required.")
    @Size(max = 150, message = "Qualification must not exceed 150 characters.")
    private String qualification;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits.")
    private String phoneNumber;

    @NotBlank(message = "Email address is required.")
    @Email(message = "Please provide a valid email address.")
    @Size(max = 120, message = "Email must not exceed 120 characters.")
    private String email;

    @NotBlank(message = "Department is required.")
    @Size(max = 100, message = "Department must not exceed 100 characters.")
    private String department;

    @NotNull(message = "Availability status is required.")
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
