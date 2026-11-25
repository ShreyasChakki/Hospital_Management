package com.hospital.management.service.impl;

import com.hospital.management.dto.AppointmentDto;
import com.hospital.management.dto.DoctorDto;
import com.hospital.management.dto.MedicalRecordDto;
import com.hospital.management.dto.PatientDto;
import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.MedicalRecord;
import com.hospital.management.entity.Patient;
import org.springframework.stereotype.Component;

/**
 * Centralized mapper converting between JPA entities and Data Transfer Objects.
 */
@Component
public class EntityMapper {

    public PatientDto toPatientDto(Patient patient) {
        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setPatientCode(patient.getPatientCode());
        dto.setFullName(patient.getFullName());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setAge(patient.getAge());
        dto.setGender(patient.getGender());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setEmail(patient.getEmail());
        dto.setAddress(patient.getAddress());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setRegistrationDate(patient.getRegistrationDate());
        return dto;
    }

    public DoctorDto toDoctorDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();
        dto.setId(doctor.getId());
        dto.setDoctorCode(doctor.getDoctorCode());
        dto.setFullName(doctor.getFullName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setQualification(doctor.getQualification());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setEmail(doctor.getEmail());
        dto.setDepartment(doctor.getDepartment());
        dto.setAvailabilityStatus(doctor.getAvailabilityStatus());
        return dto;
    }

    public AppointmentDto toAppointmentDto(Appointment appointment) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setAppointmentCode(appointment.getAppointmentCode());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientCode(appointment.getPatient().getPatientCode());
        dto.setPatientName(appointment.getPatient().getFullName());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorCode(appointment.getDoctor().getDoctorCode());
        dto.setDoctorName(appointment.getDoctor().getFullName());
        dto.setDoctorSpecialization(appointment.getDoctor().getSpecialization());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setReason(appointment.getReason());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    public MedicalRecordDto toMedicalRecordDto(MedicalRecord record) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setId(record.getId());
        dto.setRecordCode(record.getRecordCode());
        dto.setPatientId(record.getPatient().getId());
        dto.setPatientCode(record.getPatient().getPatientCode());
        dto.setPatientName(record.getPatient().getFullName());
        dto.setDoctorId(record.getDoctor().getId());
        dto.setDoctorCode(record.getDoctor().getDoctorCode());
        dto.setDoctorName(record.getDoctor().getFullName());
        dto.setVisitDate(record.getVisitDate());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setSymptoms(record.getSymptoms());
        dto.setTreatmentDetails(record.getTreatmentDetails());
        dto.setPrescription(record.getPrescription());
        dto.setAllergies(record.getAllergies());
        dto.setNotes(record.getNotes());
        dto.setDemoData(record.isDemoData());
        return dto;
    }
}
