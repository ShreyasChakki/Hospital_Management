package com.hospital.management.service.impl;

import com.hospital.management.dto.MedicalRecordDto;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.MedicalRecord;
import com.hospital.management.entity.Patient;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.MedicalRecordRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.MedicalRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Service managing patient medical records and full clinical history retrieval.
 */
@Service
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EntityMapper mapper;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    PatientRepository patientRepository,
                                    DoctorRepository doctorRepository,
                                    EntityMapper mapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.mapper = mapper;
    }

    @Override
    public MedicalRecordDto createMedicalRecord(MedicalRecordDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected patient does not exist."));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected doctor does not exist."));

        validateVisitDate(dto.getVisitDate());

        MedicalRecord record = new MedicalRecord();
        record.setRecordCode(generateUniqueRecordCode());
        record.setPatient(patient);
        record.setDoctor(doctor);
        applyDtoToEntity(dto, record);

        MedicalRecord saved = medicalRecordRepository.save(record);
        return mapper.toMedicalRecordDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDto> getAllMedicalRecords() {
        return medicalRecordRepository.findAllWithDetails().stream()
                .map(mapper::toMedicalRecordDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDto> searchMedicalRecords(String query) {
        if (query == null || query.isBlank()) {
            return getAllMedicalRecords();
        }
        return medicalRecordRepository.searchRecords(query.trim()).stream()
                .map(mapper::toMedicalRecordDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordDto getMedicalRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with ID: " + id));
        return mapper.toMedicalRecordDto(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDto> getPatientMedicalHistory(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }
        return medicalRecordRepository.findByPatientIdWithDetails(patientId).stream()
                .map(mapper::toMedicalRecordDto)
                .toList();
    }

    @Override
    public MedicalRecordDto updateMedicalRecord(Long id, MedicalRecordDto dto) {
        MedicalRecord existing = medicalRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with ID: " + id));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected patient does not exist."));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected doctor does not exist."));

        validateVisitDate(dto.getVisitDate());

        existing.setPatient(patient);
        existing.setDoctor(doctor);
        applyDtoToEntity(dto, existing);

        MedicalRecord updated = medicalRecordRepository.save(existing);
        return mapper.toMedicalRecordDto(updated);
    }

    private void validateVisitDate(LocalDate visitDate) {
        if (visitDate == null) {
            throw new BusinessRuleException("Visit date is required.");
        }
        if (visitDate.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Visit date cannot be in the future.");
        }
    }

    private void applyDtoToEntity(MedicalRecordDto dto, MedicalRecord record) {
        record.setVisitDate(dto.getVisitDate());
        record.setDiagnosis(dto.getDiagnosis().trim());
        record.setSymptoms(dto.getSymptoms().trim());
        record.setTreatmentDetails(dto.getTreatmentDetails().trim());
        record.setPrescription(dto.getPrescription() != null ? dto.getPrescription().trim() : null);
        record.setAllergies(dto.getAllergies() != null ? dto.getAllergies().trim() : null);
        record.setNotes(dto.getNotes() != null ? dto.getNotes().trim() : null);
        record.setDemoData(dto.isDemoData());
    }

    private String generateUniqueRecordCode() {
        long sequence = medicalRecordRepository.count() + 1;
        return String.format("MR-%d-%04d", Year.now().getValue(), sequence);
    }
}
