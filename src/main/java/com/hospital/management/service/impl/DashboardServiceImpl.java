package com.hospital.management.service.impl;

import com.hospital.management.dto.DashboardSummaryDto;
import com.hospital.management.entity.AppointmentStatus;
import com.hospital.management.entity.AvailabilityStatus;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.MedicalRecordRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Aggregates live hospital statistics and recent activity for the admin dashboard.
 */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final EntityMapper mapper;

    public DashboardServiceImpl(PatientRepository patientRepository,
                                DoctorRepository doctorRepository,
                                AppointmentRepository appointmentRepository,
                                MedicalRecordRepository medicalRecordRepository,
                                EntityMapper mapper) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.mapper = mapper;
    }

    @Override
    public DashboardSummaryDto getDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto();

        summary.setTotalPatients(patientRepository.count());
        summary.setTotalDoctors(doctorRepository.count());
        summary.setTotalAppointments(appointmentRepository.count());
        summary.setTotalMedicalRecords(medicalRecordRepository.count());

        summary.setScheduledAppointments(appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED));
        summary.setCompletedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
        summary.setCancelledAppointments(appointmentRepository.countByStatus(AppointmentStatus.CANCELLED));
        summary.setAvailableDoctors(
                doctorRepository.findByAvailabilityStatusOrderByFullNameAsc(AvailabilityStatus.AVAILABLE).size());

        summary.setRecentPatients(patientRepository.findTop5ByOrderByRegistrationDateDescIdDesc()
                .stream()
                .map(mapper::toPatientDto)
                .toList());

        summary.setUpcomingAppointments(appointmentRepository
                .findUpcomingAppointments(AppointmentStatus.SCHEDULED, LocalDate.now())
                .stream()
                .limit(5)
                .map(mapper::toAppointmentDto)
                .toList());

        summary.setRecentDoctors(doctorRepository.findTop5ByOrderByIdDesc()
                .stream()
                .map(mapper::toDoctorDto)
                .toList());

        return summary;
    }
}
