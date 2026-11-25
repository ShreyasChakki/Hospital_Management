package com.hospital.management.repository;

import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentCode(String appointmentCode);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> findAllWithDetails();

    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            JOIN FETCH a.doctor d
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   LOWER(a.appointmentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(d.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(d.doctorCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY a.appointmentDate DESC, a.appointmentTime DESC
            """)
    List<Appointment> searchAppointments(@Param("keyword") String keyword);

    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(Long doctorId);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNotAndIdNot(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status, Long id);

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long patientId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status);

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNotAndIdNot(
            Long patientId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status, Long id);

    long countByStatus(AppointmentStatus status);

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient
            JOIN FETCH a.doctor
            WHERE a.status = :status AND a.appointmentDate >= :fromDate
            ORDER BY a.appointmentDate ASC, a.appointmentTime ASC
            """)
    List<Appointment> findUpcomingAppointments(@Param("status") AppointmentStatus status,
                                               @Param("fromDate") LocalDate fromDate);
}
