# Testing Report — Hospital Management System

## 1. Test Environment & Tools
- **Testing Framework:** JUnit 5 (Jupiter)
- **Mocking Framework:** Mockito (`@Mock`, `@InjectMocks`, `@Spy`, `MockitoExtension`)
- **Web Security Verification:** Spring Boot `@WebMvcTest` + `MockMvc` + `SpringSecurityTest`
- **Java Runtime:** Java 21.0.2 (project compiled targeting Java 17)
- **Build Command:** `mvn test` / `mvn clean install`

---

## 2. Automated Test Suite Summary

| Test Class | Module | Tests Run | Passed | Failed | Skipped |
| :--- | :--- | :---: | :---: | :---: | :---: |
| `PatientServiceTest` | Patient Management | 5 | 5 | 0 | 0 |
| `DoctorServiceTest` | Doctor Management | 5 | 5 | 0 | 0 |
| `AppointmentServiceTest` | Appointment Management | 7 | 7 | 0 | 0 |
| `MedicalRecordServiceTest` | Medical Record Management | 4 | 4 | 0 | 0 |
| `AdminUserDetailsServiceTest` | Authentication (Unit) | 2 | 2 | 0 | 0 |
| `AuthenticationSecurityTest` | Authentication & Route Security | 3 | 3 | 0 | 0 |
| `WebControllersIntegrationTest` | All 14+ Thymeleaf Views, CSRF Forms & Error Pages | 3 | 3 | 0 | 0 |
| **Total** | **All Modules** | **29** | **29** | **0** | **0** |

---

## 3. Verified Test Scenarios by Module

### Module 1: Admin Authentication (`AdminUserDetailsServiceTest`, `AuthenticationSecurityTest`)
- Loads administrator user from `AdminRepository` with BCrypt-hashed password and `ROLE_ADMIN`.
- Rejects unknown usernames with `UsernameNotFoundException`.
- Verifies form login (`/login`) redirects valid credentials to `/dashboard`.
- Verifies invalid credentials redirect to `/login?error=true`.
- Verifies unauthenticated requests to `/dashboard`, `/patients`, and `/medical-records` redirect to `/login`.

### Module 2: Patient Management (`PatientServiceTest`)
- Registers new patient with auto-generated `PAT-YYYY-XXXX` code.
- Throws `ResourceNotFoundException` when patient ID does not exist.
- Rejects invalid phone numbers and malformed email addresses (`BusinessRuleException`).
- Updates existing patient details cleanly.
- Deletes patient when record exists.

### Module 3: Doctor Management (`DoctorServiceTest`)
- Registers new doctor with auto-generated `DOC-YYYY-XXXX` code.
- Retrieves doctor profile details and throws `ResourceNotFoundException` when missing.
- Filters doctors by specialization.
- Updates doctor specialization, qualification, and availability status.

### Module 4: Appointment Management (`AppointmentServiceTest`)
- Schedules valid future appointment with `SCHEDULED` status and auto-generated `APT-YYYY-XXXX` code.
- Rejects appointments scheduled in the past (`BusinessRuleException`).
- Rejects duplicate slots for the same doctor at the same date and time.
- Rejects conflicting appointments for the same patient at the same date and time.
- Rejects scheduling with an unavailable doctor.
- Cancels scheduled appointment (`CANCELLED`).
- Reschedules existing appointment to a new valid date and time.

### Module 5: Medical Record Management (`MedicalRecordServiceTest`)
- Creates clinical record with `MR-YYYY-XXXX` code and `demoData` marker.
- Retrieves patient medical history list.
- Rejects record creation for nonexistent patients (`ResourceNotFoundException`).
- Rejects record creation for nonexistent doctors (`ResourceNotFoundException`).

---

## 4. Verification Scope Notes
- **Verified Automatically (`mvn test` / `mvn clean install`):** Compilation of all main and test Java classes, rendering of all 14+ Thymeleaf pages, CSRF form submissions, global exception handling, Spring Security login/route protection, and all 29 JUnit 5 / Mockito / MockMvc test cases.
- **Requires Live External Services:** Live MySQL persistence (`MySQL80` is running on `localhost:3306`; requires setting `DB_PASSWORD` for your local `root` account before running `mvn spring-boot:run`) and Kanboard board creation (`kanboard/kanboard_setup.py` or CSV import).
