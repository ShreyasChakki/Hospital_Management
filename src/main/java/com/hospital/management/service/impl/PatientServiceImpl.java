package com.hospital.management.service.impl;

import com.hospital.management.dto.PatientDto;
import com.hospital.management.entity.Patient;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.PatientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.List;

/**
 * Service handling patient registration, validation, automatic ID generation, and updates.
 */
@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final EntityMapper mapper;

    public PatientServiceImpl(PatientRepository patientRepository, EntityMapper mapper) {
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    @Override
    public PatientDto registerPatient(PatientDto dto) {
        validateAndNormalize(dto, null);

        Patient patient = new Patient();
        patient.setPatientCode(generateUniquePatientCode());
        applyDtoToEntity(dto, patient);
        patient.setRegistrationDate(dto.getRegistrationDate() != null ? dto.getRegistrationDate() : LocalDate.now());

        Patient saved = patientRepository.save(patient);
        return mapper.toPatientDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAllByOrderByIdDesc().stream()
                .map(mapper::toPatientDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> searchPatients(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllPatients();
        }
        String trimmed = keyword.trim();
        return patientRepository
                .findByPatientCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByIdDesc(trimmed, trimmed)
                .stream()
                .map(mapper::toPatientDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return mapper.toPatientDto(patient);
    }

    @Override
    public PatientDto updatePatient(Long id, PatientDto dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        validateAndNormalize(dto, id);
        applyDtoToEntity(dto, existing);
        if (dto.getRegistrationDate() != null) {
            existing.setRegistrationDate(dto.getRegistrationDate());
        }

        Patient updated = patientRepository.save(existing);
        return mapper.toPatientDto(updated);
    }

    @Override
    public void deletePatient(Long id) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        patientRepository.delete(existing);
    }

    private void validateAndNormalize(PatientDto dto, Long currentId) {
        if (dto == null || dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new BusinessRuleException("Patient full name is required.");
        }
        if (dto.getPhoneNumber() == null || !dto.getPhoneNumber().trim().matches("^[+]?[0-9]{10,15}$")) {
            throw new BusinessRuleException("A valid 10 to 15 digit phone number is required.");
        }

        String normalizedPhone = dto.getPhoneNumber().trim();
        dto.setPhoneNumber(normalizedPhone);

        patientRepository.findByPhoneNumber(normalizedPhone).ifPresent(found -> {
            if (!found.getId().equals(currentId)) {
                throw new DuplicateResourceException(
                        "A patient with phone number " + normalizedPhone + " is already registered.");
            }
        });

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String normalizedEmail = dto.getEmail().trim().toLowerCase();
            if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new BusinessRuleException("Please enter a valid email address.");
            }
            dto.setEmail(normalizedEmail);
            patientRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(found -> {
                if (!found.getId().equals(currentId)) {
                    throw new DuplicateResourceException(
                            "A patient with email " + normalizedEmail + " is already registered.");
                }
            });
        } else {
            dto.setEmail(null);
        }

        // Compute age from date of birth when provided, or require explicit age
        if (dto.getDateOfBirth() != null) {
            if (dto.getDateOfBirth().isAfter(LocalDate.now())) {
                throw new BusinessRuleException("Date of birth cannot be in the future.");
            }
            dto.setAge(Period.between(dto.getDateOfBirth(), LocalDate.now()).getYears());
        } else if (dto.getAge() == null || dto.getAge() < 0 || dto.getAge() > 150) {
            throw new BusinessRuleException("Please provide either a valid Date of Birth or Age (0-150).");
        }
    }

    private void applyDtoToEntity(PatientDto dto, Patient patient) {
        patient.setFullName(dto.getFullName().trim());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender().trim());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress().trim());
        patient.setBloodGroup(dto.getBloodGroup().trim());
    }

    private String generateUniquePatientCode() {
        long sequence = patientRepository.count() + 1;
        String prefix = "PAT-" + Year.now().getValue() + "-";
        String candidate = String.format("%s%04d", prefix, sequence);
        while (patientRepository.findByPatientCode(candidate).isPresent()) {
            sequence++;
            candidate = String.format("%s%04d", prefix, sequence);
        }
        return candidate;
    }
}
