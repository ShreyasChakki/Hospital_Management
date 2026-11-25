/* ==========================================================================
   Hospital Management System - Client-Side Validation & UI Helpers
   ========================================================================== */

document.addEventListener("DOMContentLoaded", function () {
    // 1. Bootstrap 5 custom form validation
    const forms = document.querySelectorAll(".needs-validation");
    Array.from(forms).forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add("was-validated");
        }, false);
    });

    // 2. Auto-calculate age from date of birth on Patient form
    const dobInput = document.getElementById("dateOfBirth");
    const ageInput = document.getElementById("age");
    if (dobInput && ageInput) {
        dobInput.addEventListener("change", function () {
            if (!dobInput.value) {
                return;
            }
            const dob = new Date(dobInput.value);
            const today = new Date();
            let age = today.getFullYear() - dob.getFullYear();
            const monthDiff = today.getMonth() - dob.getMonth();
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
                age--;
            }
            if (age >= 0 && age <= 150) {
                ageInput.value = age;
            }
        });
    }

    // 3. Enforce minimum today date on Appointment date picker
    const apptDateInput = document.getElementById("appointmentDate");
    if (apptDateInput) {
        const todayStr = new Date().toISOString().split("T")[0];
        apptDateInput.setAttribute("min", todayStr);
    }

    // 4. Confirmation modal wiring for destructive actions (Delete / Cancel)
    const confirmModalEl = document.getElementById("hmsConfirmModal");
    if (confirmModalEl && typeof bootstrap !== "undefined") {
        const confirmModal = new bootstrap.Modal(confirmModalEl);
        const confirmMessageEl = document.getElementById("hmsConfirmMessage");
        const confirmFormEl = document.getElementById("hmsConfirmForm");

        document.querySelectorAll("[data-confirm-action]").forEach(function (btn) {
            btn.addEventListener("click", function (e) {
                e.preventDefault();
                const actionUrl = btn.getAttribute("data-confirm-action");
                const message = btn.getAttribute("data-confirm-message")
                    || "Are you sure you want to proceed with this action?";
                confirmFormEl.setAttribute("action", actionUrl);
                confirmMessageEl.textContent = message;
                confirmModal.show();
            });
        });
    }
});
