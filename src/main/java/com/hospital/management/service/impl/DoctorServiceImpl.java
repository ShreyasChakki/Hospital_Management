package com.hospital.management.service.impl;

import com.hospital.management.dto.DoctorDto;
import com.hospital.management.entity.AvailabilityStatus;
import com.hospital.management.entity.Doctor;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.service.DoctorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

/**
 * Service handling doctor registration, specialization filtering, availability, and updates.
 */
@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final EntityMapper mapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, EntityMapper mapper) {
        this.doctorRepository = doctorRepository;
        this.mapper = mapper;
    }

    @Override
    public DoctorDto registerDoctor(DoctorDto dto) {
        validateAndNormalize(dto, null);

        Doctor doctor = new Doctor();
        doctor.setDoctorCode(generateUniqueDoctorCode());
        applyDtoToEntity(dto, doctor);

        Doctor saved = doctorRepository.save(doctor);
        return mapper.toDoctorDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAllByOrderByIdDesc().stream()
                .map(mapper::toDoctorDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> searchDoctors(String keyword, String specialization) {
        String cleanKeyword = keyword != null ? keyword.trim() : null;
        String cleanSpec = specialization != null ? specialization.trim() : null;
        return doctorRepository.searchDoctors(cleanKeyword, cleanSpec).stream()
                .map(mapper::toDoctorDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> getDoctorsBySpecialization(String specialization) {
        if (specialization == null || specialization.isBlank()) {
            return getAllDoctors();
        }
        return doctorRepository.findBySpecializationIgnoreCaseOrderByFullNameAsc(specialization.trim()).stream()
                .map(mapper::toDoctorDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllSpecializations() {
        return doctorRepository.findDistinctSpecializations();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> getAvailableDoctors() {
        return doctorRepository.findByAvailabilityStatusOrderByFullNameAsc(AvailabilityStatus.AVAILABLE).stream()
                .map(mapper::toDoctorDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        return mapper.toDoctorDto(doctor);
    }

    @Override
    public DoctorDto updateDoctor(Long id, DoctorDto dto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        validateAndNormalize(dto, id);
        applyDtoToEntity(dto, existing);

        Doctor updated = doctorRepository.save(existing);
        return mapper.toDoctorDto(updated);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        doctorRepository.delete(existing);
    }

    private void validateAndNormalize(DoctorDto dto, Long currentId) {
        if (dto == null || dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new BusinessRuleException("Doctor name is required.");
        }
        if (dto.getPhoneNumber() == null || !dto.getPhoneNumber().trim().matches("^[+]?[0-9]{10,15}$")) {
            throw new BusinessRuleException("A valid 10 to 15 digit phone number is required.");
        }
        if (dto.getEmail() == null || !dto.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessRuleException("A valid doctor email address is required.");
        }

        String normalizedPhone = dto.getPhoneNumber().trim();
        String normalizedEmail = dto.getEmail().trim().toLowerCase();
        dto.setPhoneNumber(normalizedPhone);
        dto.setEmail(normalizedEmail);

        doctorRepository.findByPhoneNumber(normalizedPhone).ifPresent(found -> {
            if (!found.getId().equals(currentId)) {
                throw new DuplicateResourceException(
                        "A doctor with phone number " + normalizedPhone + " is already registered.");
            }
        });

        doctorRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(found -> {
            if (!found.getId().equals(currentId)) {
                throw new DuplicateResourceException(
                        "A doctor with email " + normalizedEmail + " is already registered.");
            }
        });
    }

    private void applyDtoToEntity(DoctorDto dto, Doctor doctor) {
        doctor.setFullName(dto.getFullName().trim());
        doctor.setSpecialization(dto.getSpecialization().trim());
        doctor.setQualification(dto.getQualification().trim());
        doctor.setPhoneNumber(dto.getPhoneNumber());
        doctor.setEmail(dto.getEmail());
        doctor.setDepartment(dto.getDepartment().trim());
        doctor.setAvailabilityStatus(dto.getAvailabilityStatus() != null
                ? dto.getAvailabilityStatus() : AvailabilityStatus.AVAILABLE);
    }

    private String generateUniqueDoctorCode() {
        long sequence = doctorRepository.count() + 1;
        String prefix = "DOC-" + Year.now().getValue() + "-";
        String candidate = String.format("%s%04d", prefix, sequence);
        while (doctorRepository.findByDoctorCode(candidate).isPresent()) {
            sequence++;
            candidate = String.format("%s%04d", prefix, sequence);
        }
        return candidate;
    }
}
