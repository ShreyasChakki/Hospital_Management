package com.hospital.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor entity mapped to the {@code doctors} table.
 */
@Entity
@Table(name = "doctors", uniqueConstraints = {
        @UniqueConstraint(name = "uk_doctors_code", columnNames = "doctor_code"),
        @UniqueConstraint(name = "uk_doctors_phone", columnNames = "phone_number"),
        @UniqueConstraint(name = "uk_doctors_email", columnNames = "email")
}, indexes = {
        @Index(name = "idx_doctors_specialization", columnList = "specialization"),
        @Index(name = "idx_doctors_availability", columnList = "availability_status")
})
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_code", nullable = false, length = 30)
    private String doctorCode;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(nullable = false, length = 150)
    private String qualification;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 20)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }
}
