package com.hospital.management.repository;

import com.hospital.management.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientCode(String patientCode);

    Optional<Patient> findByPhoneNumber(String phoneNumber);

    Optional<Patient> findByEmailIgnoreCase(String email);

    List<Patient> findAllByOrderByIdDesc();

    List<Patient> findTop5ByOrderByRegistrationDateDescIdDesc();

    List<Patient> findByPatientCodeContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrderByIdDesc(
            String patientCode, String fullName);
}
