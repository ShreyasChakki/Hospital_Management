package com.hospital.management.repository;

import com.hospital.management.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    @Query("SELECT m FROM MedicalRecord m JOIN FETCH m.patient JOIN FETCH m.doctor ORDER BY m.visitDate DESC, m.id DESC")
    List<MedicalRecord> findAllWithDetails();

    @Query("SELECT m FROM MedicalRecord m JOIN FETCH m.patient JOIN FETCH m.doctor WHERE m.id = :id")
    Optional<MedicalRecord> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            SELECT m FROM MedicalRecord m
            JOIN FETCH m.patient p
            JOIN FETCH m.doctor d
            WHERE p.id = :patientId
            ORDER BY m.visitDate DESC, m.id DESC
            """)
    List<MedicalRecord> findByPatientIdWithDetails(@Param("patientId") Long patientId);

    @Query("""
            SELECT m FROM MedicalRecord m
            JOIN FETCH m.patient p
            JOIN FETCH m.doctor d
            WHERE LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(m.recordCode) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(m.diagnosis) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY m.visitDate DESC, m.id DESC
            """)
    List<MedicalRecord> searchRecords(@Param("query") String query);
}
