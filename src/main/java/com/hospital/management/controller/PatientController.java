package com.hospital.management.controller;

import com.hospital.management.dto.PatientDto;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.MedicalRecordService;
import com.hospital.management.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * MVC controller for Patient registration, listing, searching, updating, and deletion.
 */
@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    public PatientController(PatientService patientService,
                             AppointmentService appointmentService,
                             MedicalRecordService medicalRecordService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public String listPatients(@RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {
        model.addAttribute("activePage", "patients");
        model.addAttribute("keyword", keyword);
        model.addAttribute("patients", patientService.searchPatients(keyword));
        return "patients/list";
    }

    @GetMapping("/new")
    public String showRegistrationForm(Model model) {
        PatientDto dto = new PatientDto();
        dto.setRegistrationDate(LocalDate.now());
        model.addAttribute("activePage", "patients");
        model.addAttribute("patient", dto);
        model.addAttribute("isEdit", false);
        return "patients/form";
    }

    @PostMapping("/save")
    public String registerPatient(@Valid @ModelAttribute("patient") PatientDto dto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "patients");
            model.addAttribute("isEdit", false);
            return "patients/form";
        }
        try {
            PatientDto saved = patientService.registerPatient(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient registered successfully with ID: " + saved.getPatientCode());
            return "redirect:/patients";
        } catch (BusinessRuleException | DuplicateResourceException ex) {
            model.addAttribute("activePage", "patients");
            model.addAttribute("isEdit", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "patients/form";
        }
    }

    @GetMapping("/{id}")
    public String viewPatientDetails(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "patients");
        model.addAttribute("patient", patientService.getPatientById(id));
        model.addAttribute("appointments", appointmentService.getAppointmentsByPatientId(id));
        model.addAttribute("medicalRecords", medicalRecordService.getPatientMedicalHistory(id));
        return "patients/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "patients");
        model.addAttribute("patient", patientService.getPatientById(id));
        model.addAttribute("isEdit", true);
        return "patients/form";
    }

    @PostMapping("/{id}/update")
    public String updatePatient(@PathVariable Long id,
                                @Valid @ModelAttribute("patient") PatientDto dto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            dto.setId(id);
            model.addAttribute("activePage", "patients");
            model.addAttribute("isEdit", true);
            return "patients/form";
        }
        try {
            PatientDto updated = patientService.updatePatient(id, dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient " + updated.getPatientCode() + " updated successfully.");
            return "redirect:/patients/" + id;
        } catch (BusinessRuleException | DuplicateResourceException ex) {
            dto.setId(id);
            model.addAttribute("activePage", "patients");
            model.addAttribute("isEdit", true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "patients/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        patientService.deletePatient(id);
        redirectAttributes.addFlashAttribute("successMessage", "Patient record deleted successfully.");
        return "redirect:/patients";
    }
}
