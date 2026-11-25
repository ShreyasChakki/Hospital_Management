package com.hospital.management.appointment;

import com.hospital.management.dto.AppointmentDto;
import com.hospital.management.entity.*;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.impl.AppointmentServiceImpl;
import com.hospital.management.service.impl.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Spy
    private EntityMapper mapper = new EntityMapper();

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient patient;
    private Doctor doctor;
    private AppointmentDto bookingDto;
    private Appointment savedAppointment;

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
        doctor.setSpecialization("Cardiology");
        doctor.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        bookingDto = new AppointmentDto();
        bookingDto.setPatientId(1L);
        bookingDto.setDoctorId(2L);
        bookingDto.setAppointmentDate(LocalDate.now().plusDays(2));
        bookingDto.setAppointmentTime(LocalTime.of(10, 30));
        bookingDto.setReason("Routine cardiac consultation");

        savedAppointment = new Appointment();
        savedAppointment.setId(10L);
        savedAppointment.setAppointmentCode("APT-2026-0001");
        savedAppointment.setPatient(patient);
        savedAppointment.setDoctor(doctor);
        savedAppointment.setAppointmentDate(bookingDto.getAppointmentDate());
        savedAppointment.setAppointmentTime(bookingDto.getAppointmentTime());
        savedAppointment.setReason(bookingDto.getReason());
        savedAppointment.setStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    void scheduleAppointment_shouldBookSuccessfully() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                2L, bookingDto.getAppointmentDate(), bookingDto.getAppointmentTime(), AppointmentStatus.CANCELLED))
                .thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                1L, bookingDto.getAppointmentDate(), bookingDto.getAppointmentTime(), AppointmentStatus.CANCELLED))
                .thenReturn(false);
        when(appointmentRepository.count()).thenReturn(0L);
        when(appointmentRepository.findByAppointmentCode(anyString())).thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentDto result = appointmentService.scheduleAppointment(bookingDto);

        assertNotNull(result);
        assertEquals("APT-2026-0001", result.getAppointmentCode());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void scheduleAppointment_shouldRejectPastDate() {
        bookingDto.setAppointmentDate(LocalDate.now().minusDays(1));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> appointmentService.scheduleAppointment(bookingDto)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("past"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void scheduleAppointment_shouldRejectDuplicateDoctorSlot() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                2L, bookingDto.getAppointmentDate(), bookingDto.getAppointmentTime(), AppointmentStatus.CANCELLED))
                .thenReturn(true);

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> appointmentService.scheduleAppointment(bookingDto)
        );
        assertTrue(ex.getMessage().contains("already has an appointment"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void scheduleAppointment_shouldRejectConflictingPatientAppointment() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                2L, bookingDto.getAppointmentDate(), bookingDto.getAppointmentTime(), AppointmentStatus.CANCELLED))
                .thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                1L, bookingDto.getAppointmentDate(), bookingDto.getAppointmentTime(), AppointmentStatus.CANCELLED))
                .thenReturn(true);

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> appointmentService.scheduleAppointment(bookingDto)
        );
        assertTrue(ex.getMessage().contains("conflicting appointment"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void scheduleAppointment_shouldRejectUnavailableDoctor() {
        doctor.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));

        assertThrows(BusinessRuleException.class, () -> appointmentService.scheduleAppointment(bookingDto));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_shouldMarkStatusCancelled() {
        when(appointmentRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(savedAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentDto cancelled = appointmentService.cancelAppointment(10L);

        assertEquals(AppointmentStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void rescheduleAppointment_shouldUpdateDateAndTime() {
        AppointmentDto rescheduleDto = new AppointmentDto();
        rescheduleDto.setPatientId(1L);
        rescheduleDto.setDoctorId(2L);
        rescheduleDto.setAppointmentDate(LocalDate.now().plusDays(5));
        rescheduleDto.setAppointmentTime(LocalTime.of(14, 0));
        rescheduleDto.setReason("Rescheduled cardiac checkup");

        when(appointmentRepository.findByIdWithDetails(10L)).thenReturn(Optional.of(savedAppointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotAndIdNot(
                2L, rescheduleDto.getAppointmentDate(), rescheduleDto.getAppointmentTime(),
                AppointmentStatus.CANCELLED, 10L)).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNotAndIdNot(
                1L, rescheduleDto.getAppointmentDate(), rescheduleDto.getAppointmentTime(),
                AppointmentStatus.CANCELLED, 10L)).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentDto updated = appointmentService.rescheduleAppointment(10L, rescheduleDto);

        assertEquals(rescheduleDto.getAppointmentDate(), updated.getAppointmentDate());
        assertEquals(LocalTime.of(14, 0), updated.getAppointmentTime());
        assertEquals("Rescheduled cardiac checkup", updated.getReason());
    }
}
