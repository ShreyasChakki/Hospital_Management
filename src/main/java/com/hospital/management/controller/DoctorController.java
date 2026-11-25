package com.hospital.management.controller;

import com.hospital.management.dto.DoctorDto;
import com.hospital.management.entity.AvailabilityStatus;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.DuplicateResourceException;
import com.hospital.management.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC controller for Doctor registration, listing, filtering by specialization, and updates.
 */
@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String listDoctors(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "specialization", required = false) String specialization,
                              Model model) {
        model.addAttribute("activePage", "doctors");
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedSpecialization", specialization);
        model.addAttribute("specializations", doctorService.getAllSpecializations());
        model.addAttribute("doctors", doctorService.searchDoctors(keyword, specialization));
        return "doctors/list";
    }

    @GetMapping("/new")
    public String showRegistrationForm(Model model) {
        model.addAttribute("activePage", "doctors");
        model.addAttribute("doctor", new DoctorDto());
        model.addAttribute("statuses", AvailabilityStatus.values());
        model.addAttribute("isEdit", false);
        return "doctors/form";
    }

    @PostMapping("/save")
    public String registerDoctor(@Valid @ModelAttribute("doctor") DoctorDto dto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "doctors");
            model.addAttribute("statuses", AvailabilityStatus.values());
            model.addAttribute("isEdit", false);
            return "doctors/form";
        }
        try {
            DoctorDto saved = doctorService.registerDoctor(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Doctor added successfully with ID: " + saved.getDoctorCode());
            return "redirect:/doctors";
        } catch (BusinessRuleException | DuplicateResourceException ex) {
            model.addAttribute("activePage", "doctors");
            model.addAttribute("statuses", AvailabilityStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "doctors/form";
        }
    }

    @GetMapping("/{id}")
    public String viewDoctorDetails(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "doctors");
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        return "doctors/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "doctors");
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        model.addAttribute("statuses", AvailabilityStatus.values());
        model.addAttribute("isEdit", true);
        return "doctors/form";
    }

    @PostMapping("/{id}/update")
    public String updateDoctor(@PathVariable Long id,
                               @Valid @ModelAttribute("doctor") DoctorDto dto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            dto.setId(id);
            model.addAttribute("activePage", "doctors");
            model.addAttribute("statuses", AvailabilityStatus.values());
            model.addAttribute("isEdit", true);
            return "doctors/form";
        }
        try {
            DoctorDto updated = doctorService.updateDoctor(id, dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Doctor " + updated.getDoctorCode() + " updated successfully.");
            return "redirect:/doctors/" + id;
        } catch (BusinessRuleException | DuplicateResourceException ex) {
            dto.setId(id);
            model.addAttribute("activePage", "doctors");
            model.addAttribute("statuses", AvailabilityStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "doctors/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        doctorService.deleteDoctor(id);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor record deleted successfully.");
        return "redirect:/doctors";
    }
}
