package com.hospital.management.repository;

import com.hospital.management.entity.AvailabilityStatus;
import com.hospital.management.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByDoctorCode(String doctorCode);

    Optional<Doctor> findByEmailIgnoreCase(String email);

    Optional<Doctor> findByPhoneNumber(String phoneNumber);

    List<Doctor> findAllByOrderByIdDesc();

    List<Doctor> findTop5ByOrderByIdDesc();

    List<Doctor> findBySpecializationIgnoreCaseOrderByFullNameAsc(String specialization);

    List<Doctor> findByAvailabilityStatusOrderByFullNameAsc(AvailabilityStatus availabilityStatus);

    @Query("SELECT DISTINCT d.specialization FROM Doctor d ORDER BY d.specialization ASC")
    List<String> findDistinctSpecializations();

    @Query("""
            SELECT d FROM Doctor d
            WHERE (:keyword IS NULL OR :keyword = '' OR
                   LOWER(d.doctorCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(d.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(d.specialization) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(d.department) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:specialization IS NULL OR :specialization = '' OR
                   LOWER(d.specialization) = LOWER(:specialization))
            ORDER BY d.id DESC
            """)
    List<Doctor> searchDoctors(@Param("keyword") String keyword,
                               @Param("specialization") String specialization);
}
