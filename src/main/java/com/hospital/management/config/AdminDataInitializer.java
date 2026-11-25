package com.hospital.management.config;

import com.hospital.management.entity.*;
import com.hospital.management.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Safely initializes a default administrator account on startup when no admin account
 * exists, using BCrypt hashing and configurable environment properties.
 */
@Configuration
public class AdminDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminDataInitializer.class);

    @Value("${app.admin.default-username:admin}")
    private String defaultUsername;

    @Value("${app.admin.default-password:Admin@123}")
    private String defaultPassword;

    @Value("${app.admin.default-fullname:Hospital Chief Administrator}")
    private String defaultFullName;

    @Value("${app.admin.default-email:admin@hospitalcare.local}")
    private String defaultEmail;

    @Value("${app.demo-data.seed-on-startup:false}")
    private boolean seedDemoData;

    @Bean
    CommandLineRunner initializeDefaultAdmin(AdminRepository adminRepository,
                                             PasswordEncoder passwordEncoder,
                                             PatientRepository patientRepository,
                                             DoctorRepository doctorRepository,
                                             AppointmentRepository appointmentRepository,
                                             MedicalRecordRepository medicalRecordRepository) {
        return args -> {
            if (!adminRepository.existsByUsername(defaultUsername) && adminRepository.count() == 0) {
                Admin admin = new Admin();
                admin.setUsername(defaultUsername);
                admin.setPassword(passwordEncoder.encode(defaultPassword));
                admin.setFullName(defaultFullName);
                admin.setEmail(defaultEmail);
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                adminRepository.save(admin);
                log.info("Initialized default administrator account with username '{}'.", defaultUsername);
            }

            if (seedDemoData && patientRepository.count() == 0 && doctorRepository.count() == 0) {
                seedAcademicDemoData(patientRepository, doctorRepository,
                        appointmentRepository, medicalRecordRepository);
            }
        };
    }

    private void seedAcademicDemoData(PatientRepository patientRepository,
                                      DoctorRepository doctorRepository,
                                      AppointmentRepository appointmentRepository,
                                      MedicalRecordRepository medicalRecordRepository) {
        Patient p1 = new Patient();
        p1.setPatientCode("PAT-2026-0001");
        p1.setFullName("Aarav Sharma");
        p1.setDateOfBirth(LocalDate.of(1992, 4, 15));
        p1.setAge(34);
        p1.setGender("Male");
        p1.setPhoneNumber("9876543210");
        p1.setEmail("aarav.sharma@demo.local");
        p1.setAddress("104 MG Road, Bengaluru");
        p1.setBloodGroup("O+");
        p1.setRegistrationDate(LocalDate.now().minusDays(5));
        p1 = patientRepository.save(p1);

        Doctor d1 = new Doctor();
        d1.setDoctorCode("DOC-2026-0001");
        d1.setFullName("Dr. Vikram Sethi");
        d1.setSpecialization("Cardiology");
        d1.setQualification("MD, DM (Cardiology)");
        d1.setPhoneNumber("9123456701");
        d1.setEmail("vikram.sethi@hospitalcare.local");
        d1.setDepartment("Cardiology Department");
        d1.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        d1 = doctorRepository.save(d1);

        Appointment apt = new Appointment();
        apt.setAppointmentCode("APT-2026-0001");
        apt.setPatient(p1);
        apt.setDoctor(d1);
        apt.setAppointmentDate(LocalDate.now().plusDays(1));
        apt.setAppointmentTime(LocalTime.of(10, 0));
        apt.setReason("Routine cardiac checkup");
        apt.setStatus(AppointmentStatus.SCHEDULED);
        apt.setNotes("Demo scheduled appointment");
        appointmentRepository.save(apt);

        MedicalRecord mr = new MedicalRecord();
        mr.setRecordCode("MR-2026-0001");
        mr.setPatient(p1);
        mr.setDoctor(d1);
        mr.setVisitDate(LocalDate.now().minusDays(2));
        mr.setDiagnosis("Mild Essential Hypertension");
        mr.setSymptoms("Occasional dizziness");
        mr.setTreatmentDetails("Lifestyle modification and regular BP monitoring");
        mr.setPrescription("Amlodipine 5mg once daily");
        mr.setAllergies("None");
        mr.setNotes("DEMONSTRATION DATA: Seeded academic sample record.");
        mr.setDemoData(true);
        medicalRecordRepository.save(mr);

        log.info("Seeded demonstration sample data (marked as demoData=true).");
    }
}
