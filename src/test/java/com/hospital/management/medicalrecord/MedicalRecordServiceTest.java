package com.hospital.management.medicalrecord;

import com.hospital.management.dto.MedicalRecordDto;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.MedicalRecord;
import com.hospital.management.entity.Patient;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.MedicalRecordRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.impl.EntityMapper;
import com.hospital.management.service.impl.MedicalRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Spy
    private EntityMapper mapper = new EntityMapper();

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    private Patient patient;
    private Doctor doctor;
    private MedicalRecordDto recordDto;
    private MedicalRecord savedRecord;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setPatientCode("PAT-2026-0001");
        patient.setFullName("Aarav Sharma");

        doctor = new Doctor();
        doctor.setId(2L);
        doctor.setDoctorCode("DOC-2026-0001");
        doctor.setFullName("Dr. Vikram Sethi");

        recordDto = new MedicalRecordDto();
        recordDto.setPatientId(1L);
        recordDto.setDoctorId(2L);
        recordDto.setVisitDate(LocalDate.now().minusDays(1));
        recordDto.setDiagnosis("Mild Hypertension");
        recordDto.setSymptoms("Dizziness");
        recordDto.setTreatmentDetails("Low sodium diet and daily BP check");
        recordDto.setPrescription("Amlodipine 5mg");
        recordDto.setDemoData(true);

        savedRecord = new MedicalRecord();
        savedRecord.setId(100L);
        savedRecord.setRecordCode("MR-2026-0001");
        savedRecord.setPatient(patient);
        savedRecord.setDoctor(doctor);
        savedRecord.setVisitDate(recordDto.getVisitDate());
        savedRecord.setDiagnosis("Mild Hypertension");
        savedRecord.setSymptoms("Dizziness");
        savedRecord.setTreatmentDetails("Low sodium diet and daily BP check");
        savedRecord.setPrescription("Amlodipine 5mg");
        savedRecord.setDemoData(true);
    }

    @Test
    void createMedicalRecord_shouldSucceed() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(medicalRecordRepository.count()).thenReturn(0L);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        MedicalRecordDto created = medicalRecordService.createMedicalRecord(recordDto);

        assertNotNull(created);
        assertEquals("MR-2026-0001", created.getRecordCode());
        assertEquals("Mild Hypertension", created.getDiagnosis());
        assertTrue(created.isDemoData());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    void getPatientMedicalHistory_shouldReturnHistoryForExistingPatient() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(medicalRecordRepository.findByPatientIdWithDetails(1L)).thenReturn(List.of(savedRecord));

        List<MedicalRecordDto> history = medicalRecordService.getPatientMedicalHistory(1L);

        assertEquals(1, history.size());
        assertEquals("Aarav Sharma", history.get(0).getPatientName());
    }

    @Test
    void createMedicalRecord_shouldRejectNonexistentPatient() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());
        recordDto.setPatientId(999L);

        assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.createMedicalRecord(recordDto));
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void createMedicalRecord_shouldRejectNonexistentDoctor() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());
        recordDto.setDoctorId(999L);

        assertThrows(ResourceNotFoundException.class, () -> medicalRecordService.createMedicalRecord(recordDto));
        verify(medicalRecordRepository, never()).save(any());
    }
}
