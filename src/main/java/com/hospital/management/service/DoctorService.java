package com.hospital.management.service;

import com.hospital.management.dto.DoctorDto;

import java.util.List;

public interface DoctorService {

    DoctorDto registerDoctor(DoctorDto doctorDto);

    List<DoctorDto> getAllDoctors();

    List<DoctorDto> searchDoctors(String keyword, String specialization);

    List<DoctorDto> getDoctorsBySpecialization(String specialization);

    List<String> getAllSpecializations();

    List<DoctorDto> getAvailableDoctors();

    DoctorDto getDoctorById(Long id);

    DoctorDto updateDoctor(Long id, DoctorDto doctorDto);

    void deleteDoctor(Long id);
}
