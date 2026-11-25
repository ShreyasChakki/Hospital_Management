package com.hospital.management.security;

import com.hospital.management.entity.Admin;
import com.hospital.management.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads administrator credentials from the {@code admins} table for Spring Security authentication.
 */
@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .disabled(!admin.isEnabled())
                .authorities(List.of(new SimpleGrantedAuthority(admin.getRole())))
                .build();
    }
}
