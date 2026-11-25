package com.hospital.management.service;

import com.hospital.management.dto.MedicalRecordDto;

import java.util.List;

public interface MedicalRecordService {

    MedicalRecordDto createMedicalRecord(MedicalRecordDto medicalRecordDto);

    List<MedicalRecordDto> getAllMedicalRecords();

    List<MedicalRecordDto> searchMedicalRecords(String query);

    MedicalRecordDto getMedicalRecordById(Long id);

    List<MedicalRecordDto> getPatientMedicalHistory(Long patientId);

    MedicalRecordDto updateMedicalRecord(Long id, MedicalRecordDto medicalRecordDto);
}
