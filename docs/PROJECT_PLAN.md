# Project Plan — Hospital Management System (Kanban Methodology)

## 1. Project Overview
- **Project Name:** MedCore Hospital Management System
- **Methodology:** Kanban (Continuous Flow, WIP Limits, Bottleneck Management)
- **Technology Stack:** Java 17+, Spring Boot 3.3.5, Spring Security (BCrypt), Spring Data JPA / Hibernate, MySQL 8, Thymeleaf, Bootstrap 5, JUnit 5, Mockito, Git, Kanboard.

## 2. Project Objectives
1. Eliminate manual paper-based patient and appointment registers.
2. Provide secure role-protected administrator authentication with BCrypt password hashing.
3. Enforce scheduling integrity (no past slots, no unavailable doctors, no double-booked doctors or conflicting patient slots).
4. Maintain normalized relational persistence in `hospital_management_db` with proper foreign keys and constraints.
5. Demonstrate iterative engineering using Kanban boards, Git feature commits, and automated JUnit 5 + Mockito verification.

## 3. Work Breakdown & Kanban Release Phases

| Phase | Scope | Modules Covered | Target Milestone |
| :--- | :--- | :--- | :--- |
| **Phase 1: Foundation** | Requirement analysis, ER design, Maven & Spring Boot setup | `schema.sql`, `pom.xml`, `application.properties` | Week 1 |
| **Phase 2: Core Entities & Security** | JPA entities, repositories, global exception handling, Spring Security | `admins`, `patients`, `doctors`, `appointments`, `medical_records` | Week 2 |
| **Phase 3: Business Modules** | Patient, Doctor, Appointment, and Medical Record services & controllers | Modules 1–5 backend logic | Week 3 |
| **Phase 4: UI & Dashboard** | Responsive Blue/White Bootstrap 5 + Thymeleaf views | 14 application pages + sidebar | Week 4 |
| **Phase 5: Verification & Docs** | JUnit 5 & Mockito test suites, Git history, Kanboard workflow docs | `mvn test`, reports, README | Week 5 |

## 4. Risk Management & Mitigation
- **Scheduling Race / Double Booking:** Enforced in `AppointmentServiceImpl` for both doctor and patient slots, ignoring cancelled appointments.
- **Unauthorized Medical Data Access:** Protected all `/patients/**`, `/doctors/**`, `/appointments/**`, `/medical-records/**`, and `/dashboard` endpoints via Spring Security `ROLE_ADMIN`.
- **Demonstration vs. Clinical Data Confusion:** Included explicit `is_demo_data` flag in `medical_records` and UI badges (`DEMO DATA`).
- **Testing Bottlenecks:** Monitored Kanban `TESTING` column WIP limit (`3`) to ensure unit tests are written alongside each module rather than deferred to the end.
