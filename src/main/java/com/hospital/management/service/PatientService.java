package com.hospital.management.service;

import com.hospital.management.dto.PatientDto;

import java.util.List;

public interface PatientService {

    PatientDto registerPatient(PatientDto patientDto);

    List<PatientDto> getAllPatients();

    List<PatientDto> searchPatients(String keyword);

    PatientDto getPatientById(Long id);

    PatientDto updatePatient(Long id, PatientDto patientDto);

    void deletePatient(Long id);
}
