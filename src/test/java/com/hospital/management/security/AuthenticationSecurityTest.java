package com.hospital.management.security;

import com.hospital.management.controller.DashboardController;
import com.hospital.management.dto.DashboardSummaryDto;
import com.hospital.management.entity.Admin;
import com.hospital.management.repository.AdminRepository;
import com.hospital.management.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {DashboardController.class})
@Import({SecurityConfig.class, AdminUserDetailsService.class})
class AuthenticationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword(new BCryptPasswordEncoder().encode("Admin@123"));
        admin.setFullName("Hospital Chief Administrator");
        admin.setEmail("admin@hospitalcare.local");
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setRecentPatients(Collections.emptyList());
        summary.setUpcomingAppointments(Collections.emptyList());
        summary.setRecentDoctors(Collections.emptyList());
        when(dashboardService.getDashboardSummary()).thenReturn(summary);
    }

    @Test
    void login_shouldSucceedWithValidCredentials() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("Admin@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void login_shouldFailWithInvalidCredentials() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("WrongPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void protectedRoutes_shouldRedirectUnauthenticatedUsersToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/patients"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/medical-records"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
