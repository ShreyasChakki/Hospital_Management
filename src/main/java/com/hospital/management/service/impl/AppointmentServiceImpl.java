package com.hospital.management.service.impl;

import com.hospital.management.dto.AppointmentDto;
import com.hospital.management.entity.*;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;

/**
 * Service enforcing appointment scheduling business rules:
 * <ul>
 *     <li>No appointments in the past</li>
 *     <li>Selected patient and doctor must exist</li>
 *     <li>Selected doctor must be AVAILABLE</li>
 *     <li>No duplicate appointment slots for the same doctor</li>
 *     <li>No conflicting appointment slots for the same patient</li>
 * </ul>
 */
@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final EntityMapper mapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  EntityMapper mapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.mapper = mapper;
    }

    @Override
    public AppointmentDto scheduleAppointment(AppointmentDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected patient does not exist."));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected doctor does not exist."));

        validateAppointmentRules(patient, doctor, dto.getAppointmentDate(), dto.getAppointmentTime(), null);

        Appointment appointment = new Appointment();
        appointment.setAppointmentCode(generateUniqueAppointmentCode());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setReason(dto.getReason().trim());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setNotes(dto.getNotes() != null ? dto.getNotes().trim() : null);

        Appointment saved = appointmentRepository.save(appointment);
        return mapper.toAppointmentDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAllWithDetails().stream()
                .map(mapper::toAppointmentDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> searchAppointments(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllAppointments();
        }
        return appointmentRepository.searchAppointments(keyword.trim()).stream()
                .map(mapper::toAppointmentDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        return mapper.toAppointmentDto(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId)
                .stream()
                .map(mapper::toAppointmentDto)
                .toList();
    }

    @Override
    public AppointmentDto rescheduleAppointment(Long id, AppointmentDto dto) {
        Appointment existing = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        if (existing.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Cancelled appointments cannot be rescheduled.");
        }
        if (existing.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Completed appointments cannot be rescheduled.");
        }

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected patient does not exist."));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected doctor does not exist."));

        validateAppointmentRules(patient, doctor, dto.getAppointmentDate(), dto.getAppointmentTime(), id);

        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setAppointmentDate(dto.getAppointmentDate());
        existing.setAppointmentTime(dto.getAppointmentTime());
        existing.setReason(dto.getReason().trim());
        existing.setNotes(dto.getNotes() != null ? dto.getNotes().trim() : null);

        Appointment updated = appointmentRepository.save(existing);
        return mapper.toAppointmentDto(updated);
    }

    @Override
    public AppointmentDto cancelAppointment(Long id) {
        Appointment existing = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        if (existing.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Completed appointments cannot be cancelled.");
        }
        if (existing.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Appointment is already cancelled.");
        }

        existing.setStatus(AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(existing);
        return mapper.toAppointmentDto(updated);
    }

    @Override
    public AppointmentDto updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment existing = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        if (status == null) {
            throw new BusinessRuleException("Appointment status is required.");
        }
        existing.setStatus(status);
        Appointment updated = appointmentRepository.save(existing);
        return mapper.toAppointmentDto(updated);
    }

    private void validateAppointmentRules(Patient patient,
                                          Doctor doctor,
                                          LocalDate date,
                                          LocalTime time,
                                          Long currentAppointmentId) {
        if (date == null || time == null) {
            throw new BusinessRuleException("Appointment date and time are required.");
        }

        // Business Rule 1: Do not allow appointments in the past
        if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Appointment date and time cannot be in the past.");
        }

        // Business Rule 2: Prevent scheduling with unavailable doctors
        if (doctor.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new BusinessRuleException(
                    "Doctor " + doctor.getFullName() + " is currently " + doctor.getAvailabilityStatus().getDisplayName()
                            + " and cannot accept appointments.");
        }

        // Business Rule 3: Prevent duplicate doctor slot (ignoring cancelled appointments)
        boolean doctorSlotTaken = currentAppointmentId == null
                ? appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        doctor.getId(), date, time, AppointmentStatus.CANCELLED)
                : appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotAndIdNot(
                        doctor.getId(), date, time, AppointmentStatus.CANCELLED, currentAppointmentId);

        if (doctorSlotTaken) {
            throw new BusinessRuleException(
                    "Doctor " + doctor.getFullName() + " already has an appointment at " + date + " " + time + ".");
        }

        // Business Rule 4: Prevent conflicting patient slot (ignoring cancelled appointments)
        boolean patientSlotConflict = currentAppointmentId == null
                ? appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        patient.getId(), date, time, AppointmentStatus.CANCELLED)
                : appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNotAndIdNot(
                        patient.getId(), date, time, AppointmentStatus.CANCELLED, currentAppointmentId);

        if (patientSlotConflict) {
            throw new BusinessRuleException(
                    "Patient " + patient.getFullName() + " already has a conflicting appointment at "
                            + date + " " + time + ".");
        }
    }

    private String generateUniqueAppointmentCode() {
        long sequence = appointmentRepository.count() + 1;
        String prefix = "APT-" + Year.now().getValue() + "-";
        String candidate = String.format("%s%04d", prefix, sequence);
        while (appointmentRepository.findByAppointmentCode(candidate).isPresent()) {
            sequence++;
            candidate = String.format("%s%04d", prefix, sequence);
        }
        return candidate;
    }
}
