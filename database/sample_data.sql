-- ==============================================================================
-- Hospital Management System - Demonstration Sample Data
-- NOTE: All records below are fictional demonstration data for academic testing.
-- Do NOT run in a clinical environment with real patient data.
-- ==============================================================================

USE hospital_management_db;

-- Sample Patients (Fictional Demonstration Records)
INSERT IGNORE INTO patients
    (patient_code, full_name, date_of_birth, age, gender, phone_number, email, address, blood_group, registration_date)
VALUES
    ('PAT-2026-0001', 'Aarav Sharma', '1992-04-15', 34, 'Male', '9876543210', 'aarav.sharma@demo.local', '104 MG Road, Bengaluru', 'O+', DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
    ('PAT-2026-0002', 'Meera Nair', '1988-09-22', 38, 'Female', '9876543211', 'meera.nair@demo.local', '22 Residency Lane, Kochi', 'A+', DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
    ('PAT-2026-0003', 'Rohan Verma', '1979-12-03', 46, 'Male', '9876543212', 'rohan.verma@demo.local', '45 Park Street, Pune', 'B+', DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
    ('PAT-2026-0004', 'Ananya Iyer', '2001-06-18', 25, 'Female', '9876543213', 'ananya.iyer@demo.local', '88 Anna Nagar, Chennai', 'AB+', DATE_SUB(CURDATE(), INTERVAL 2 DAY));

-- Sample Doctors (Fictional Demonstration Records)
INSERT IGNORE INTO doctors
    (doctor_code, full_name, specialization, qualification, phone_number, email, department, availability_status)
VALUES
    ('DOC-2026-0001', 'Dr. Vikram Sethi', 'Cardiology', 'MD, DM (Cardiology)', '9123456701', 'vikram.sethi@hospitalcare.local', 'Cardiology Department', 'AVAILABLE'),
    ('DOC-2026-0002', 'Dr. Priya Kulkarni', 'Neurology', 'MD, DM (Neurology)', '9123456702', 'priya.kulkarni@hospitalcare.local', 'Neurology Department', 'AVAILABLE'),
    ('DOC-2026-0003', 'Dr. Arjun Menon', 'Orthopedics', 'MS (Orthopedics)', '9123456703', 'arjun.menon@hospitalcare.local', 'Orthopedics Department', 'AVAILABLE'),
    ('DOC-2026-0004', 'Dr. Kavita Rao', 'General Medicine', 'MBBS, MD (Internal Medicine)', '9123456704', 'kavita.rao@hospitalcare.local', 'General Medicine', 'UNAVAILABLE');

-- Sample Appointments (Fictional Demonstration Records)
INSERT IGNORE INTO appointments
    (appointment_code, patient_id, doctor_id, appointment_date, appointment_time, reason, status, notes)
SELECT 'APT-2026-0001', p.id, d.id, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00',
       'Routine cardiac checkup and ECG review', 'SCHEDULED', 'Bring prior ECG report'
FROM patients p, doctors d
WHERE p.patient_code = 'PAT-2026-0001' AND d.doctor_code = 'DOC-2026-0001';

INSERT IGNORE INTO appointments
    (appointment_code, patient_id, doctor_id, appointment_date, appointment_time, reason, status, notes)
SELECT 'APT-2026-0002', p.id, d.id, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '11:30:00',
       'Persistent migraine consultation', 'SCHEDULED', 'Follow-up after medication adjustment'
FROM patients p, doctors d
WHERE p.patient_code = 'PAT-2026-0002' AND d.doctor_code = 'DOC-2026-0002';

INSERT IGNORE INTO appointments
    (appointment_code, patient_id, doctor_id, appointment_date, appointment_time, reason, status, notes)
SELECT 'APT-2026-0003', p.id, d.id, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '15:00:00',
       'Knee joint stiffness evaluation', 'COMPLETED', 'Physiotherapy advised'
FROM patients p, doctors d
WHERE p.patient_code = 'PAT-2026-0003' AND d.doctor_code = 'DOC-2026-0003';

INSERT IGNORE INTO appointments
    (appointment_code, patient_id, doctor_id, appointment_date, appointment_time, reason, status, notes)
SELECT 'APT-2026-0004', p.id, d.id, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '09:30:00',
       'Annual wellness screening', 'CANCELLED', 'Reschedule requested by patient'
FROM patients p, doctors d
WHERE p.patient_code = 'PAT-2026-0004' AND d.doctor_code = 'DOC-2026-0001';

-- Sample Medical Records (Clearly flagged as demonstration data: is_demo_data = TRUE)
INSERT IGNORE INTO medical_records
    (record_code, patient_id, doctor_id, visit_date, diagnosis, symptoms, treatment_details, prescription, allergies, notes, is_demo_data)
SELECT 'MR-2026-0001', p.id, d.id, DATE_SUB(CURDATE(), INTERVAL 4 DAY),
       'Mild Essential Hypertension', 'Occasional dizziness, elevated BP reading (142/90)',
       'Dietary sodium restriction, daily 30-minute walk, BP monitoring',
       'Amlodipine 5mg once daily after breakfast', 'None reported',
       'DEMONSTRATION DATA: Sample initial cardiology evaluation.', TRUE
FROM patients p, doctors d
WHERE p.patient_code = 'PAT-2026-0001' AND d.doctor_code = 'DOC-2026-0001';

INSERT IGNORE INTO medical_records
    (record_code, patient_id, doctor_id, visit_date, diagnosis, symptoms, treatment_details, prescription, allergies, notes, is_demo_data)
SELECT 'MR-2026-0002', p.id, d.id, DATE_SUB(CURDATE(), INTERVAL 2 DAY),
       'Tension Migraine', 'Throbbing unilateral headache, photophobia',
       'Sleep hygiene counseling, trigger diary, acute analgesic therapy',
       'Sumatriptan 50mg as needed (max 2 doses/day)', 'Penicillin',
       'DEMONSTRATION DATA: Sample neurology outpatient note.', TRUE
FROM patients p, doctors d
WHERE p.patient_code = 'PAT-2026-0002' AND d.doctor_code = 'DOC-2026-0002';

INSERT IGNORE INTO medical_records
    (record_code, patient_id, doctor_id, visit_date, diagnosis, symptoms, treatment_details, prescription, allergies, notes, is_demo_data)
SELECT 'MR-2026-0003', p.id, d.id, DATE_SUB(CURDATE(), INTERVAL 1 DAY),
       'Early Knee Osteoarthritis', 'Morning stiffness, mild pain on stairs',
       'Quadriceps strengthening exercises, cold compress post-activity',
       'Paracetamol 500mg SOS for pain, Vitamin D3 60000 IU weekly', 'Sulfa drugs',
       'DEMONSTRATION DATA: Sample orthopedics follow-up record.', TRUE
FROM patients p, doctors d
WHERE p.patient_code = 'PAT-2026-0003' AND d.doctor_code = 'DOC-2026-0003';
