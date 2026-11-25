package com.hospital.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating, updating, and viewing patient medical records.
 */
public class MedicalRecordDto {

    private Long id;

    private String recordCode;

    @NotNull(message = "Please select a patient.")
    private Long patientId;

    private String patientCode;
    private String patientName;

    @NotNull(message = "Please select the attending doctor.")
    private Long doctorId;

    private String doctorCode;
    private String doctorName;

    @NotNull(message = "Visit date is required.")
    @PastOrPresent(message = "Visit date cannot be in the future.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate visitDate;

    @NotBlank(message = "Diagnosis is required.")
    @Size(max = 255, message = "Diagnosis must not exceed 255 characters.")
    private String diagnosis;

    @NotBlank(message = "Symptoms are required.")
    @Size(max = 500, message = "Symptoms must not exceed 500 characters.")
    private String symptoms;

    @NotBlank(message = "Treatment details are required.")
    @Size(max = 1000, message = "Treatment details must not exceed 1000 characters.")
    private String treatmentDetails;

    @Size(max = 1000, message = "Prescription must not exceed 1000 characters.")
    private String prescription;

    @Size(max = 255, message = "Allergies must not exceed 255 characters.")
    private String allergies;

    @Size(max = 1000, message = "Medical notes must not exceed 1000 characters.")
    private String notes;

    private boolean demoData = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecordCode() {
        return recordCode;
    }

    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    public void setTreatmentDetails(String treatmentDetails) {
        this.treatmentDetails = treatmentDetails;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isDemoData() {
        return demoData;
    }

    public void setDemoData(boolean demoData) {
        this.demoData = demoData;
    }
}
