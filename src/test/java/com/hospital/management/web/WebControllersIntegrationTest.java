package com.hospital.management.web;

import com.hospital.management.controller.*;
import com.hospital.management.dto.*;
import com.hospital.management.entity.AppointmentStatus;
import com.hospital.management.entity.AvailabilityStatus;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.AdminRepository;
import com.hospital.management.security.AdminUserDetailsService;
import com.hospital.management.security.SecurityConfig;
import com.hospital.management.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verifies every Thymeleaf page, sidebar link, CRUD form submission, and error handling
 * across all controllers using MockMvc.
 */
@WebMvcTest(controllers = {
        AuthController.class,
        DashboardController.class,
        PatientController.class,
        DoctorController.class,
        AppointmentController.class,
        MedicalRecordController.class
})
@Import({SecurityConfig.class, AdminUserDetailsService.class})
class WebControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private PatientService patientService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private MedicalRecordService medicalRecordService;

    private PatientDto samplePatient;
    private DoctorDto sampleDoctor;
    private AppointmentDto sampleAppointment;
    private MedicalRecordDto sampleRecord;

    @BeforeEach
    void setUp() {
        samplePatient = new PatientDto();
        samplePatient.setId(1L);
        samplePatient.setPatientCode("PAT-2026-0001");
        samplePatient.setFullName("Aarav Sharma");
        samplePatient.setDateOfBirth(LocalDate.of(1992, 4, 15));
        samplePatient.setAge(34);
        samplePatient.setGender("Male");
        samplePatient.setPhoneNumber("9876543210");
        samplePatient.setEmail("aarav@demo.local");
        samplePatient.setAddress("104 MG Road");
        samplePatient.setBloodGroup("O+");
        samplePatient.setRegistrationDate(LocalDate.now());

        sampleDoctor = new DoctorDto();
        sampleDoctor.setId(2L);
        sampleDoctor.setDoctorCode("DOC-2026-0001");
        sampleDoctor.setFullName("Dr. Vikram Sethi");
        sampleDoctor.setSpecialization("Cardiology");
        sampleDoctor.setQualification("MD, DM");
        sampleDoctor.setPhoneNumber("9123456701");
        sampleDoctor.setEmail("vikram@hospitalcare.local");
        sampleDoctor.setDepartment("Cardiology Department");
        sampleDoctor.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        sampleAppointment = new AppointmentDto();
        sampleAppointment.setId(10L);
        sampleAppointment.setAppointmentCode("APT-2026-0001");
        sampleAppointment.setPatientId(1L);
        sampleAppointment.setPatientCode("PAT-2026-0001");
        sampleAppointment.setPatientName("Aarav Sharma");
        sampleAppointment.setDoctorId(2L);
        sampleAppointment.setDoctorCode("DOC-2026-0001");
        sampleAppointment.setDoctorName("Dr. Vikram Sethi");
        sampleAppointment.setDoctorSpecialization("Cardiology");
        sampleAppointment.setAppointmentDate(LocalDate.now().plusDays(1));
        sampleAppointment.setAppointmentTime(LocalTime.of(10, 0));
        sampleAppointment.setReason("Routine checkup");
        sampleAppointment.setStatus(AppointmentStatus.SCHEDULED);

        sampleRecord = new MedicalRecordDto();
        sampleRecord.setId(20L);
        sampleRecord.setRecordCode("MR-2026-0001");
        sampleRecord.setPatientId(1L);
        sampleRecord.setPatientCode("PAT-2026-0001");
        sampleRecord.setPatientName("Aarav Sharma");
        sampleRecord.setDoctorId(2L);
        sampleRecord.setDoctorCode("DOC-2026-0001");
        sampleRecord.setDoctorName("Dr. Vikram Sethi");
        sampleRecord.setVisitDate(LocalDate.now().minusDays(1));
        sampleRecord.setDiagnosis("Mild Hypertension");
        sampleRecord.setSymptoms("Dizziness");
        sampleRecord.setTreatmentDetails("Low sodium diet");
        sampleRecord.setPrescription("Amlodipine 5mg");
        sampleRecord.setDemoData(true);

        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setTotalPatients(1);
        summary.setTotalDoctors(1);
        summary.setTotalAppointments(1);
        summary.setTotalMedicalRecords(1);
        summary.setAvailableDoctors(1);
        summary.setScheduledAppointments(1);
        summary.setRecentPatients(List.of(samplePatient));
        summary.setUpcomingAppointments(List.of(sampleAppointment));
        summary.setRecentDoctors(List.of(sampleDoctor));

        when(dashboardService.getDashboardSummary()).thenReturn(summary);
        when(patientService.getAllPatients()).thenReturn(List.of(samplePatient));
        when(patientService.searchPatients(any())).thenReturn(List.of(samplePatient));
        when(patientService.getPatientById(1L)).thenReturn(samplePatient);

        when(doctorService.getAllDoctors()).thenReturn(List.of(sampleDoctor));
        when(doctorService.searchDoctors(any(), any())).thenReturn(List.of(sampleDoctor));
        when(doctorService.getAllSpecializations()).thenReturn(List.of("Cardiology"));
        when(doctorService.getDoctorById(2L)).thenReturn(sampleDoctor);

        when(appointmentService.getAllAppointments()).thenReturn(List.of(sampleAppointment));
        when(appointmentService.searchAppointments(any())).thenReturn(List.of(sampleAppointment));
        when(appointmentService.getAppointmentById(10L)).thenReturn(sampleAppointment);
        when(appointmentService.getAppointmentsByPatientId(1L)).thenReturn(List.of(sampleAppointment));

        when(medicalRecordService.searchMedicalRecords(any())).thenReturn(List.of(sampleRecord));
        when(medicalRecordService.getMedicalRecordById(20L)).thenReturn(sampleRecord);
        when(medicalRecordService.getPatientMedicalHistory(1L)).thenReturn(List.of(sampleRecord));
    }

    @Test
    void loginPage_shouldRenderCleanly() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("MedCore Hospital Management")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void allModulePages_shouldRenderWithoutTemplateErrors() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hospital Administration Dashboard")));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PAT-2026-0001")));

        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register New Patient")));

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Aarav Sharma")));

        mockMvc.perform(get("/patients/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Edit Patient Details")));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("DOC-2026-0001")));

        mockMvc.perform(get("/doctors/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add New Doctor")));

        mockMvc.perform(get("/doctors/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dr. Vikram Sethi")));

        mockMvc.perform(get("/doctors/2/edit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Edit Doctor Profile")));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("APT-2026-0001")));

        mockMvc.perform(get("/appointments/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Schedule Patient Appointment")));

        mockMvc.perform(get("/appointments/10"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Routine checkup")));

        mockMvc.perform(get("/appointments/10/edit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Reschedule Appointment")));

        mockMvc.perform(get("/medical-records"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("DEMO DATA")));

        mockMvc.perform(get("/medical-records/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add Patient Medical Record")));

        mockMvc.perform(get("/medical-records/20/edit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Edit Clinical Record")));

        mockMvc.perform(get("/medical-records/patient/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mild Hypertension")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void patientAndAppointmentPostWorkflows_shouldRedirectOnSuccessAndHandleErrors() throws Exception {
        when(patientService.registerPatient(any())).thenReturn(samplePatient);

        mockMvc.perform(post("/patients/save")
                        .with(csrf())
                        .param("fullName", "Aarav Sharma")
                        .param("age", "34")
                        .param("gender", "Male")
                        .param("phoneNumber", "9876543210")
                        .param("email", "aarav@demo.local")
                        .param("address", "104 MG Road")
                        .param("bloodGroup", "O+"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        when(appointmentService.scheduleAppointment(any()))
                .thenThrow(new BusinessRuleException("Doctor already has an appointment at this slot."));

        mockMvc.perform(post("/appointments/save")
                        .with(csrf())
                        .param("patientId", "1")
                        .param("doctorId", "2")
                        .param("appointmentDate", LocalDate.now().plusDays(1).toString())
                        .param("appointmentTime", "10:00")
                        .param("reason", "Checkup"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/form"))
                .andExpect(content().string(containsString("Doctor already has an appointment at this slot.")));

        when(patientService.getPatientById(eq(999L)))
                .thenThrow(new ResourceNotFoundException("Patient not found with ID: 999"));

        mockMvc.perform(get("/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"))
                .andExpect(content().string(containsString("Record Not Found")));
    }
}
