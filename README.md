# MedCore Hospital Management System (Kanban Methodology)

A complete, end-to-end web-based **Hospital Management System** built with **Java 17+**, **Spring Boot 3**, **Spring Security (BCrypt)**, **Spring Data JPA / Hibernate**, **MySQL**, **Thymeleaf**, and **Bootstrap 5**, following the **Kanban** software development methodology, **Git** version control, and **JUnit 5 + Mockito** automated testing.

---

## Table of Contents
1. [Project Introduction](#1-project-introduction)
2. [Problem Statement](#2-problem-statement)
3. [Project Objectives](#3-project-objectives)
4. [Technologies Used](#4-technologies-used)
5. [System Requirements](#5-system-requirements)
6. [Software Architecture](#6-software-architecture)
7. [Database Design & Configuration](#7-database-design--configuration)
8. [Installation & Execution Instructions](#8-installation--execution-instructions)
9. [Admin Login & Credential Management](#9-admin-login--credential-management)
10. [Application Modules & Features](#10-application-modules--features)
11. [JUnit 5 & Mockito Testing](#11-junit-5--mockito-testing)
12. [Git Version Control](#12-git-version-control)
13. [Kanboard Implementation & Workflow](#13-kanboard-implementation--workflow)
14. [Expected Application Screenshots](#14-expected-application-screenshots)

---

## 1. Project Introduction
MedCore Hospital Management System streamlines core hospital administrative operations through a centralized, authenticated web portal. Administrators can register and manage patients, manage doctor profiles and availability, schedule appointments with strict conflict validation, and maintain structured patient medical histories.

## 2. Problem Statement
Manual paper registers and disconnected spreadsheets in small-to-medium clinics frequently lead to:
- Duplicate or conflicting doctor/patient appointment bookings.
- Difficulty retrieving a patient's chronological diagnosis and treatment history.
- Lack of role-based access control over sensitive medical records.
- Inconsistent validation of contact and demographic fields.

## 3. Project Objectives
- Deliver a modular **Spring Boot MVC** application backed by normalized **MySQL** storage.
- Enforce **BCrypt-authenticated** access control across all clinical and administrative routes.
- Enforce appointment scheduling business rules (no past slots, no unavailable doctors, no double bookings).
- Demonstrate disciplined academic software engineering using **Kanban (Kanboard)**, **Git**, and **JUnit 5 / Mockito**.

## 4. Technologies Used
| Layer | Technology |
| :--- | :--- |
| **Backend** | Java 17+, Spring Boot 3.3.5, Spring Web MVC, Spring Security 6, Jakarta Bean Validation |
| **ORM & Persistence** | Spring Data JPA, Hibernate ORM, MySQL Connector/J |
| **Database** | MySQL 8.0 (`hospital_management_db`) |
| **Frontend** | HTML5, CSS3, JavaScript, Bootstrap 5.3, Bootstrap Icons, Thymeleaf |
| **Testing** | JUnit 5 (Jupiter), Mockito, Spring MockMvc & Spring Security Test |
| **Build Tool** | Apache Maven |
| **Project Management** | Kanboard (4-column Kanban workflow with WIP limits) |
| **Version Control** | Git |

## 5. System Requirements
- **JDK:** Java 17 or higher (verified with JDK 21)
- **Build Tool:** Apache Maven 3.8+ (or IntelliJ IDEA bundled Maven)
- **Database:** MySQL Server 8.0+
- **Browser:** Any modern desktop or mobile browser (Chrome, Edge, Firefox)
- **Optional:** Docker / Kanboard for live Kanban board hosting

## 6. Software Architecture
The project follows a clean layered **MVC + Service + Repository** architecture under `src/main/java/com/hospital/management/`:

```text
com.hospital.management
├── config/          # Safe default admin & optional demo seeding (AdminDataInitializer)
├── controller/      # Spring MVC controllers handling HTTP requests and Thymeleaf views
├── dto/             # Validated Data Transfer Objects (PatientDto, DoctorDto, etc.)
├── entity/          # JPA Entities & Enums (Admin, Patient, Doctor, Appointment, MedicalRecord)
├── exception/       # Custom domain exceptions & GlobalExceptionHandler
├── repository/      # Spring Data JPA repositories with indexed queries
├── security/        # SecurityConfig (BCrypt, session management) & AdminUserDetailsService
└── service/         # Service interfaces & transactional implementations with business rules
```

## 7. Database Design & Configuration

### Database Name
`hospital_management_db`

### Normalized Tables & Relationships
1. **`admins`**: Stores administrator accounts (`id`, `username`, `password` BCrypt hash, `full_name`, `email`, `role`, `enabled`).
2. **`patients`**: Stores unique system-generated `patient_code` (`PAT-YYYY-XXXX`), demographics, contact, blood group, and `registration_date`.
3. **`doctors`**: Stores unique `doctor_code` (`DOC-YYYY-XXXX`), specialization, qualification, department, contact, and `availability_status`.
4. **`appointments`**: References `patients(id)` and `doctors(id)` via foreign keys (no redundant patient/doctor names stored). Tracks `appointment_code`, date, time, reason, and status (`SCHEDULED`, `COMPLETED`, `CANCELLED`).
5. **`medical_records`**: References `patients(id)` and `doctors(id)` via foreign keys. Tracks `record_code`, visit date, diagnosis, symptoms, treatment details, prescription, allergies, notes, and `is_demo_data` flag.

### Automatic Table Creation & SQL Scripts
- Spring Boot automatically creates/updates the schema via `spring.jpa.hibernate.ddl-auto=update` and `createDatabaseIfNotExist=true` in `src/main/resources/application.properties`.
- Standalone SQL scripts are also provided in:
  - [`database/schema.sql`](database/schema.sql)
  - [`database/sample_data.sql`](database/sample_data.sql)

### Configuration (`src/main/resources/application.properties`)
Configure MySQL credentials via environment variables (recommended) instead of committing passwords:
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```
To optionally load sample academic demonstration data at startup:
```powershell
$env:HMS_SEED_DEMO_DATA="true"
```

## 8. Installation & Execution Instructions

### Step 1: Prepare MySQL Database
Ensure MySQL 8.0 is running on `localhost:3306`. Optionally run the SQL scripts manually:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample_data.sql
```

### Step 2: Build the Application
```bash
mvn clean install
```
*(If using IntelliJ IDEA's bundled Maven on Windows without global `mvn` on `PATH`, run `"C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.2.4\plugins\maven\lib\maven3\bin\mvn.cmd" clean install`.)*

### Step 3: Run Unit Tests
```bash
mvn test
```

### Step 4: Start the Application
```bash
mvn spring-boot:run
```
Access the application at: **`http://localhost:8080`**

## 9. Admin Login & Credential Management
- On first startup, `AdminDataInitializer` checks if any administrator exists in `admins`. If empty, it creates a default administrator account with a **BCrypt-hashed** password.
- **Default Credentials (for local academic setup):**
  - **Username:** `admin`
  - **Password:** `Admin@123`
- **How to Change Default Credentials Safely:**
  Set environment variables before launching the application:
  ```powershell
  $env:HMS_ADMIN_USERNAME="hospital_admin"
  $env:HMS_ADMIN_PASSWORD="YourStrongPassword!2026"
  ```
  Or update the password hash directly in the `admins` table using a BCrypt-encoded value (`BCryptPasswordEncoder`).

## 10. Application Modules & Features
1. **Admin Authentication:** Form login, BCrypt password hashing, session fixation protection, logout cookie/session invalidation, and role protection on all `/dashboard`, `/patients/**`, `/doctors/**`, `/appointments/**`, and `/medical-records/**` routes.
2. **Patient Management:** Auto-generated `PAT-YYYY-XXXX` IDs, auto-calculated age from DOB, phone/email validation & duplicate prevention, search by Patient ID or name, full profile view, edit, and confirmation-guarded delete.
3. **Doctor Management:** Auto-generated `DOC-YYYY-XXXX` IDs, specialization filter dropdown, keyword search, availability status (`AVAILABLE`, `UNAVAILABLE`, `ON_LEAVE`), edit, and delete.
4. **Appointment Management:** Patient/doctor selection, future-only date/time validation, doctor availability check, doctor double-booking prevention, patient conflict prevention, status transitions (`SCHEDULED`, `COMPLETED`, `CANCELLED`), rescheduling, and cancellation.
5. **Patient Medical Record Management:** Diagnosis, symptoms, treatment details, prescriptions, allergies, notes, chronological patient medical history timeline, and explicit `DEMO DATA` badge for academic sample records.
6. **Admin Dashboard:** Live counts (Patients, Doctors, Appointments, Medical Records), appointment status breakdown, quick actions, recent patient registrations, upcoming appointments, and recently added doctors.

## 11. JUnit 5 & Mockito Testing
Automated unit and security tests are organized by module under `src/test/java/com/hospital/management/`:
- `patient/PatientServiceTest.java`
- `doctor/DoctorServiceTest.java`
- `appointment/AppointmentServiceTest.java`
- `medicalrecord/MedicalRecordServiceTest.java`
- `security/AdminUserDetailsServiceTest.java`
- `security/AuthenticationSecurityTest.java`

Run the entire test suite with:
```bash
mvn test
```
Detailed results are documented in [`docs/TESTING_REPORT.md`](docs/TESTING_REPORT.md).

## 12. Git Version Control
Full branching and milestone commit guidance is documented in [`docs/GIT_WORKFLOW.md`](docs/GIT_WORKFLOW.md). Common commands:
```bash
git init
git status
git add .
git commit -m "Commit message"
git checkout -b feature/module-name
```

## 13. Kanboard Implementation & Workflow
- Project Name: **`Hospital Management System Development`**
- Columns: **`TO DO`** -> **`IN PROGRESS` (WIP: 3)** -> **`TESTING` (WIP: 3)** -> **`DONE`**
- Full Kanban guide, WIP limits, bottleneck identification, and Kaizen improvements: [`docs/KANBAN_WORKFLOW.md`](docs/KANBAN_WORKFLOW.md)
- Kanboard CSV task import: [`kanboard/kanboard_tasks_import.csv`](kanboard/kanboard_tasks_import.csv)
- Kanboard JSON-RPC automation script: [`kanboard/kanboard_setup.py`](kanboard/kanboard_setup.py)

## 14. Expected Application Screenshots
For academic project submission, capture screenshots of the following 14 views running at `http://localhost:8080`:
1. Admin Login Page (`/login`)
2. Main Administration Dashboard (`/dashboard`)
3. Patient List & Search (`/patients`)
4. Patient Registration Form (`/patients/new`)
5. Patient Profile Details (`/patients/{id}`)
6. Doctor List & Specialization Filter (`/doctors`)
7. Doctor Registration Form (`/doctors/new`)
8. Doctor Details (`/doctors/{id}`)
9. Appointment List (`/appointments`)
10. Appointment Booking Form (`/appointments/new`)
11. Appointment Details & Status Control (`/appointments/{id}`)
12. Medical Records List with Demo Data Badge (`/medical-records`)
13. Medical Record Creation Form (`/medical-records/new`)
14. Patient Medical History Timeline (`/medical-records/patient/{patientId}`)
