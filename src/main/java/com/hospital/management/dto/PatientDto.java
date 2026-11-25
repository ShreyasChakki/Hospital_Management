package com.hospital.management.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating, updating, and displaying {@code Patient} records.
 */
public class PatientDto {

    private Long id;

    private String patientCode;

    @NotBlank(message = "Full name is required.")
    @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters.")
    private String fullName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @Min(value = 0, message = "Age cannot be negative.")
    @Max(value = 150, message = "Please enter a valid age up to 150.")
    private Integer age;

    @NotBlank(message = "Gender is required.")
    private String gender;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits.")
    private String phoneNumber;

    @Email(message = "Please provide a valid email address.")
    @Size(max = 120, message = "Email must not exceed 120 characters.")
    private String email;

    @NotBlank(message = "Residential address is required.")
    @Size(max = 255, message = "Address must not exceed 255 characters.")
    private String address;

    @NotBlank(message = "Blood group is required.")
    private String bloodGroup;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registrationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}
