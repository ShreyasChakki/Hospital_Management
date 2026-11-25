package com.hospital.management.service;

import com.hospital.management.dto.AppointmentDto;
import com.hospital.management.entity.AppointmentStatus;

import java.util.List;

public interface AppointmentService {

    AppointmentDto scheduleAppointment(AppointmentDto appointmentDto);

    List<AppointmentDto> getAllAppointments();

    List<AppointmentDto> searchAppointments(String keyword);

    AppointmentDto getAppointmentById(Long id);

    List<AppointmentDto> getAppointmentsByPatientId(Long patientId);

    AppointmentDto rescheduleAppointment(Long id, AppointmentDto appointmentDto);

    AppointmentDto cancelAppointment(Long id);

    AppointmentDto updateAppointmentStatus(Long id, AppointmentStatus status);
}
