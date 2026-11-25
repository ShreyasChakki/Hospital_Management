# Git Version Control Workflow

## 1. Repository Setup & Branching Strategy
- **Main Branch:** `main` (always deployable, passing `mvn test`)
- **Feature Branches:** `feature/<module-name>` (aligned with Kanboard tasks)
- **Sensitive Data Protection:** `.gitignore` excludes `target/`, IDE directories (`.idea/`, `.vscode/`), `.env`, `application-local.properties`, and `*.log` so database credentials or private records are never committed.

## 2. Standard Git Commands

### Initialize Repository
```bash
git init
git branch -M main
git status
```

### Recommended Feature Branch Workflow
```bash
# Create and switch to a feature branch for a Kanban task
git checkout -b feature/appointment-scheduling

# Stage and commit changes
git add .
git commit -m "Implement appointment scheduling and conflict validation"

# Switch back to main and merge after tests pass
git checkout main
git merge --no-ff feature/appointment-scheduling
```

## 3. Milestone Commit Sequence
1. `Initial project setup` (`pom.xml`, `.gitignore`, base package layout)
2. `Configure Spring Boot and MySQL` (`application.properties`, `database/schema.sql`)
3. `Implement patient management` (`Patient`, `PatientRepository`, `PatientService`, `PatientController`, templates)
4. `Implement doctor management` (`Doctor`, `DoctorRepository`, `DoctorService`, `DoctorController`, templates)
5. `Implement appointment management` (`Appointment` entities, conflict rules, controller, templates)
6. `Implement medical record management` (`MedicalRecord`, history queries, demo badge, templates)
7. `Create admin dashboard` (`DashboardService`, `DashboardController`, `dashboard.html`)
8. `Implement authentication` (`Admin`, `SecurityConfig`, `AdminUserDetailsService`, `login.html`)
9. `Add JUnit test cases` (JUnit 5 + Mockito suites across all 5 modules)
10. `Fix bugs and improve validation` (Global exception handler, client/server validation polish)
11. `Final documentation` (`README.md`, `docs/*`, Kanboard import/setup scripts)
