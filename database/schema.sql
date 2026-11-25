-- ==============================================================================
-- Hospital Management System - MySQL Database Schema
-- Database: hospital_management_db
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS hospital_management_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hospital_management_db;

-- 1. Admins Table
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ROLE_ADMIN',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_admins_username UNIQUE (username),
    CONSTRAINT uk_admins_email UNIQUE (email)
) ENGINE=InnoDB;

-- 2. Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_code VARCHAR(30) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    date_of_birth DATE NULL,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(120) NULL,
    address VARCHAR(255) NOT NULL,
    blood_group VARCHAR(10) NOT NULL,
    registration_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_patients_code UNIQUE (patient_code),
    CONSTRAINT uk_patients_phone UNIQUE (phone_number),
    CONSTRAINT uk_patients_email UNIQUE (email),
    INDEX idx_patients_full_name (full_name),
    INDEX idx_patients_reg_date (registration_date)
) ENGINE=InnoDB;

-- 3. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_code VARCHAR(30) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    qualification VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(120) NOT NULL,
    department VARCHAR(100) NOT NULL,
    availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_doctors_code UNIQUE (doctor_code),
    CONSTRAINT uk_doctors_phone UNIQUE (phone_number),
    CONSTRAINT uk_doctors_email UNIQUE (email),
    INDEX idx_doctors_specialization (specialization),
    INDEX idx_doctors_availability (availability_status)
) ENGINE=InnoDB;

-- 4. Appointments Table (Normalized: references patients.id and doctors.id)
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_code VARCHAR(30) NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    notes VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_appointments_code UNIQUE (appointment_code),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id)
        REFERENCES patients (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors (id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_appointments_doctor_slot (doctor_id, appointment_date, appointment_time),
    INDEX idx_appointments_patient_slot (patient_id, appointment_date, appointment_time),
    INDEX idx_appointments_status (status)
) ENGINE=InnoDB;

-- 5. Medical Records Table (Normalized: references patients.id and doctors.id)
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_code VARCHAR(30) NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    visit_date DATE NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    symptoms VARCHAR(500) NOT NULL,
    treatment_details VARCHAR(1000) NOT NULL,
    prescription VARCHAR(1000) NULL,
    allergies VARCHAR(255) NULL,
    notes VARCHAR(1000) NULL,
    is_demo_data BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_medical_records_code UNIQUE (record_code),
    CONSTRAINT fk_medical_records_patient FOREIGN KEY (patient_id)
        REFERENCES patients (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_medical_records_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors (id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_medical_records_patient (patient_id, visit_date),
    INDEX idx_medical_records_doctor (doctor_id)
) ENGINE=InnoDB;
