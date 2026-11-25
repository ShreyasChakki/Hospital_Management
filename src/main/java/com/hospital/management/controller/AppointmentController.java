package com.hospital.management.controller;

import com.hospital.management.dto.AppointmentDto;
import com.hospital.management.entity.AppointmentStatus;
import com.hospital.management.exception.BusinessRuleException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC controller for scheduling, searching, rescheduling, cancelling, and updating appointments.
 */
@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AppointmentController(AppointmentService appointmentService,
                                 PatientService patientService,
                                 DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String listAppointments(@RequestParam(value = "keyword", required = false) String keyword,
                                   Model model) {
        model.addAttribute("activePage", "appointments");
        model.addAttribute("keyword", keyword);
        model.addAttribute("appointments", appointmentService.searchAppointments(keyword));
        model.addAttribute("statuses", AppointmentStatus.values());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String showBookingForm(@RequestParam(value = "patientId", required = false) Long patientId,
                                  @RequestParam(value = "doctorId", required = false) Long doctorId,
                                  Model model) {
        AppointmentDto dto = new AppointmentDto();
        dto.setPatientId(patientId);
        dto.setDoctorId(doctorId);
        populateFormOptions(model, false);
        model.addAttribute("appointment", dto);
        return "appointments/form";
    }

    @PostMapping("/save")
    public String scheduleAppointment(@Valid @ModelAttribute("appointment") AppointmentDto dto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormOptions(model, false);
            return "appointments/form";
        }
        try {
            AppointmentDto saved = appointmentService.scheduleAppointment(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Appointment scheduled successfully with ID: " + saved.getAppointmentCode());
            return "redirect:/appointments";
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            populateFormOptions(model, false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "appointments/form";
        }
    }

    @GetMapping("/{id}")
    public String viewAppointmentDetails(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "appointments");
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        model.addAttribute("statuses", AppointmentStatus.values());
        return "appointments/detail";
    }

    @GetMapping("/{id}/edit")
    public String showRescheduleForm(@PathVariable Long id, Model model) {
        populateFormOptions(model, true);
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        return "appointments/form";
    }

    @PostMapping("/{id}/update")
    public String rescheduleAppointment(@PathVariable Long id,
                                        @Valid @ModelAttribute("appointment") AppointmentDto dto,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            dto.setId(id);
            populateFormOptions(model, true);
            return "appointments/form";
        }
        try {
            AppointmentDto updated = appointmentService.rescheduleAppointment(id, dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Appointment " + updated.getAppointmentCode() + " rescheduled successfully.");
            return "redirect:/appointments/" + id;
        } catch (BusinessRuleException | ResourceNotFoundException ex) {
            dto.setId(id);
            populateFormOptions(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "appointments/form";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            AppointmentDto cancelled = appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Appointment " + cancelled.getAppointmentCode() + " has been cancelled.");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") AppointmentStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            AppointmentDto updated = appointmentService.updateAppointmentStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Appointment " + updated.getAppointmentCode() + " status updated to "
                            + status.getDisplayName() + ".");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/appointments/" + id;
    }

    private void populateFormOptions(Model model, boolean isEdit) {
        model.addAttribute("activePage", "appointments");
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());
    }
}
