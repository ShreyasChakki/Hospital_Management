package com.hospital.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global exception handler translating application exceptions into safe, user-friendly
 * HTTP responses without exposing internal database or stack-trace details.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("statusCode", 404);
        model.addAttribute("errorTitle", "Record Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler({BusinessRuleException.class, DuplicateResourceException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessRule(RuntimeException ex, Model model) {
        model.addAttribute("statusCode", 400);
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrity(DataIntegrityViolationException ex, Model model) {
        log.warn("Database integrity violation encountered", ex);
        model.addAttribute("statusCode", 409);
        model.addAttribute("errorTitle", "Data Conflict");
        model.addAttribute("errorMessage",
                "This operation conflicts with existing records (duplicate value or linked records).");
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorMessage", "You do not have permission to access this resource.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpected(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unexpected error on request {}", request.getRequestURI(), ex);
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "Unexpected Application Error");
        model.addAttribute("errorMessage",
                "An unexpected error occurred while processing your request. Please try again.");
        return "error";
    }
}
