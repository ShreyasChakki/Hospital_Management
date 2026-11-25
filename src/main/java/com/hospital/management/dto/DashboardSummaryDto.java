package com.hospital.management.dto;

import java.util.List;

/**
 * Aggregated metrics and summary lists displayed on the main administration dashboard.
 */
public class DashboardSummaryDto {

    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long totalMedicalRecords;

    private long scheduledAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long availableDoctors;

    private List<PatientDto> recentPatients;
    private List<AppointmentDto> upcomingAppointments;
    private List<DoctorDto> recentDoctors;

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getTotalMedicalRecords() {
        return totalMedicalRecords;
    }

    public void setTotalMedicalRecords(long totalMedicalRecords) {
        this.totalMedicalRecords = totalMedicalRecords;
    }

    public long getScheduledAppointments() {
        return scheduledAppointments;
    }

    public void setScheduledAppointments(long scheduledAppointments) {
        this.scheduledAppointments = scheduledAppointments;
    }

    public long getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(long cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public long getAvailableDoctors() {
        return availableDoctors;
    }

    public void setAvailableDoctors(long availableDoctors) {
        this.availableDoctors = availableDoctors;
    }

    public List<PatientDto> getRecentPatients() {
        return recentPatients;
    }

    public void setRecentPatients(List<PatientDto> recentPatients) {
        this.recentPatients = recentPatients;
    }

    public List<AppointmentDto> getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(List<AppointmentDto> upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }

    public List<DoctorDto> getRecentDoctors() {
        return recentDoctors;
    }

    public void setRecentDoctors(List<DoctorDto> recentDoctors) {
        this.recentDoctors = recentDoctors;
    }
}
