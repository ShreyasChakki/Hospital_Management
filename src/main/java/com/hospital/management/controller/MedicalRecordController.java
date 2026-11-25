package com.hospital.management.controller;

import com.hospital.management.dto.MedicalRecordDto;
import com.hospital.management.dto.PatientDto;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.service.DoctorService;
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
 * MVC controller for creating, updating, searching, and viewing patient medical records.
 */
@Controller
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public MedicalRecordController(MedicalRecordService medicalRecordService,
                                   PatientService patientService,
                                   DoctorService doctorService) {
        this.medicalRecordService = medicalRecordService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String listMedicalRecords(@RequestParam(value = "query", required = false) String query,
                                     Model model) {
        model.addAttribute("activePage", "medical-records");
        model.addAttribute("query", query);
        model.addAttribute("records", medicalRecordService.searchMedicalRecords(query));
        return "medical-records/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(value = "patientId", required = false) Long patientId,
                                 Model model) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setPatientId(patientId);
        dto.setVisitDate(LocalDate.now());
        populateFormOptions(model, false);
        model.addAttribute("record", dto);
        return "medical-records/form";
    }

    @PostMapping("/save")
    public String createMedicalRecord(@Valid @ModelAttribute("record") MedicalRecordDto dto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormOptions(model, false);
            return "medical-records/form";
        }
        try {
            MedicalRecordDto saved = medicalRecordService.createMedicalRecord(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Medical record created successfully with ID: " + saved.getRecordCode());
            return "redirect:/medical-records/patient/" + saved.getPatientId();
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            populateFormOptions(model, false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "medical-records/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        populateFormOptions(model, true);
        model.addAttribute("record", medicalRecordService.getMedicalRecordById(id));
        return "medical-records/form";
    }

    @PostMapping("/{id}/update")
    public String updateMedicalRecord(@PathVariable Long id,
                                      @Valid @ModelAttribute("record") MedicalRecordDto dto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            dto.setId(id);
            populateFormOptions(model, true);
            return "medical-records/form";
        }
        try {
            MedicalRecordDto updated = medicalRecordService.updateMedicalRecord(id, dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Medical record " + updated.getRecordCode() + " updated successfully.");
            return "redirect:/medical-records/patient/" + updated.getPatientId();
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            dto.setId(id);
            populateFormOptions(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "medical-records/form";
        }
    }

    @GetMapping("/patient/{patientId}")
    public String viewPatientMedicalHistory(@PathVariable Long patientId, Model model) {
        PatientDto patient = patientService.getPatientById(patientId);
        model.addAttribute("activePage", "medical-records");
        model.addAttribute("patient", patient);
        model.addAttribute("history", medicalRecordService.getPatientMedicalHistory(patientId));
        return "medical-records/history";
    }

    private void populateFormOptions(Model model, boolean isEdit) {
        model.addAttribute("activePage", "medical-records");
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());
    }
}
