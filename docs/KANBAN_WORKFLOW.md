# Kanboard Implementation & Kanban Workflow Guide

## 1. Kanboard Project Configuration
- **Project Name:** `Hospital Management System Development`
- **Board Columns (4-Stage Workflow) & WIP Limits:**

| Column Order | Column Name | WIP Limit | Purpose |
| :---: | :--- | :---: | :--- |
| 1 | **TO DO** | Unlimited | Prioritized backlog of analyzed requirements and module tasks |
| 2 | **IN PROGRESS** | **3** | Active implementation work; prevents developer context-switching |
| 3 | **TESTING** | **3** | Unit tests (JUnit 5/Mockito), UI validation, and rule verification |
| 4 | **DONE** | Unlimited | Code compiled, tests passing, documentation updated |

> **Kanboard Status Note:** Kanboard was not running locally on this workstation during generation, so the complete board task import file ([kanboard_tasks_import.csv](file:///c:/Users/SHREYAS/Desktop/Hospital/kanboard/kanboard_tasks_import.csv)) and JSON-RPC automation script ([kanboard_setup.py](file:///c:/Users/SHREYAS/Desktop/Hospital/kanboard/kanboard_setup.py)) have been provided below for instant setup on any local or Docker Kanboard instance.

---

## 2. Step-by-Step Kanboard Setup Instructions

### Option A: Quick Start with Docker
```bash
docker run -d --name kanboard -p 8081:80 kanboard/kanboard:latest
```
Open `http://localhost:8081` and sign in with default credentials (`admin` / `admin`, then change password in User Profile).

### Option B: Create the Project & Columns Manually or via Script
1. Create a new project titled **Hospital Management System Development**.
2. In **Configure this project -> Columns**, configure the exact 4 columns:
   - `TO DO` (Task limit: `0` / unlimited)
   - `IN PROGRESS` (Task limit: `3`)
   - `TESTING` (Task limit: `3`)
   - `DONE` (Task limit: `0` / unlimited)
3. Configure **Categories**: `Architecture`, `Backend`, `Frontend`, `Database`, `Security`, `Testing`, `Documentation`.
4. Import tasks via **Project -> Import -> Tasks** using `kanboard/kanboard_tasks_import.csv`, OR run the API setup script:
   ```bash
   python kanboard/kanboard_setup.py --url http://localhost:8081/jsonrpc.php --user admin --password admin
   ```

---

## 3. Complete Kanban Task Board Mapping

### Column: TO DO (Initial Backlog / Upcoming Enhancements)
1. **Requirement analysis** *(Priority: High | Category: Architecture | Deadline: Day 2)* — Define functional/non-functional requirements for all 5 hospital modules and dashboard.
2. **Database design** *(Priority: High | Category: Database | Deadline: Day 4)* — Design normalized ER schema (`admins`, `patients`, `doctors`, `appointments`, `medical_records`) with FK and unique constraints.
3. **Project architecture** *(Priority: High | Category: Architecture | Deadline: Day 5)* — Establish layered Spring Boot MVC package structure (`config`, `controller`, `service`, `repository`, `entity`, `dto`, `exception`, `security`).
4. **UI design** *(Priority: Medium | Category: Frontend | Deadline: Day 7)* — Create consistent white/blue Bootstrap 5 layout, sidebar navigation, cards, and confirmation dialogs.
5. **Patient registration module** *(Priority: High | Category: Backend | Deadline: Day 10)* — Implement auto-ID generation (`PAT-YYYY-XXXX`), validation, search, and CRUD.
6. **Doctor management module** *(Priority: High | Category: Backend | Deadline: Day 12)* — Implement doctor profiles, specialization filtering, and availability tracking.
7. **Appointment scheduling module** *(Priority: Urgent | Category: Backend | Deadline: Day 15)* — Implement slot conflict detection, past-date rejection, rescheduling, and cancellation.
8. **Medical record module** *(Priority: High | Category: Backend | Deadline: Day 18)* — Implement clinical notes, prescriptions, allergy tracking, and patient medical history.
9. **Dashboard implementation** *(Priority: Medium | Category: Frontend | Deadline: Day 20)* — Implement live summary counts, appointment status breakdown, and recent activity tables.
10. **Authentication implementation** *(Priority: Urgent | Category: Security | Deadline: Day 21)* — Implement Spring Security form login, BCrypt hashing, session fixation protection, and role guards.

### Column: IN PROGRESS (WIP Limit: 3)
- Tasks actively being coded or refactored move here one at a time while keeping active count `<= 3`.
- Example active flow during integration:
  - *Global exception handling & validation refinement* (Priority: High | Category: Backend)

### Column: TESTING (WIP Limit: 3)
1. **Patient module unit testing** *(Priority: High | Category: Testing)* — Verify registration, missing patient exception, validation failures, update, and delete.
2. **Doctor module unit testing** *(Priority: High | Category: Testing)* — Verify registration, retrieval, specialization search, and updates.
3. **Appointment module unit testing** *(Priority: Urgent | Category: Testing)* — Verify booking, past-date rejection, doctor slot conflict, patient slot conflict, reschedule, and cancel.
4. **Medical record module unit testing** *(Priority: High | Category: Testing)* — Verify record creation, patient history retrieval, and nonexistent patient/doctor rejection.
5. **Integration testing** *(Priority: High | Category: Testing)* — Verify Spring Security login, protected route redirects, and end-to-end MySQL persistence.
6. **UI validation** *(Priority: Medium | Category: Testing)* — Verify HTML5/Bootstrap validation, responsive sidebar, and confirmation modals across all 14 pages.

### Column: DONE
- Verified tasks with passing JUnit 5 tests, clean `mvn test` execution, and documented SQL/README artifacts.

---

## 4. Identifying Bottlenecks & Blocked Tasks

### How Bottlenecks Appear on the Kanban Board
1. **WIP Limit Saturation in `TESTING` (3/3):**
   - If *Patient module*, *Doctor module*, and *Appointment module* are all sitting in `TESTING` while `IN PROGRESS` finishes *Medical record module*, the `TESTING` column hits its WIP limit of `3`.
   - **Rule:** Developers stop pulling new features from `TO DO` and assist with writing JUnit/Mockito tests or fixing failing assertions until `TESTING` drops below 3.
2. **Blocked Tasks Example:**
   - **Blocked Task:** *Appointment module integration testing*
   - **Root Cause:** Blocked when a developer attempts to book an appointment without handling cancelled slots (`Status != CANCELLED`), or when local MySQL service is not started.
   - **Kanban Action:** Tag task with `blocked` label / red color in Kanboard, add a blocker comment documenting the exact failing rule, and prioritize unblocking before starting new `TO DO` items.

---

## 5. Continuous Workflow Improvement (Kaizen)
- **Lead Time & Cycle Time Tracking:** Measure days from `IN PROGRESS` -> `DONE` using Kanboard Analytics -> Cumulative Flow Diagram (CFD).
- **Small Batch Commits:** Align each Kanban card 1-to-1 with a focused Git commit/branch so code review and rollback remain trivial.
- **Shift-Left Validation:** Added Bean Validation (`@Valid`) and `EntityMapper` reuse after observing duplicate validation boilerplate across controllers, reducing rework in the `TESTING` column.
