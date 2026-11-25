package com.hospital.management.doctor;

import com.hospital.management.dto.DoctorDto;
import com.hospital.management.entity.AvailabilityStatus;
import com.hospital.management.entity.Doctor;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.service.impl.DoctorServiceImpl;
import com.hospital.management.service.impl.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Spy
    private EntityMapper mapper = new EntityMapper();

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private DoctorDto doctorDto;
    private Doctor savedDoctor;

    @BeforeEach
    void setUp() {
        doctorDto = new DoctorDto();
        doctorDto.setFullName("Dr. Vikram Sethi");
        doctorDto.setSpecialization("Cardiology");
        doctorDto.setQualification("MD, DM");
        doctorDto.setPhoneNumber("9123456701");
        doctorDto.setEmail("vikram.sethi@hospitalcare.local");
        doctorDto.setDepartment("Cardiology Department");
        doctorDto.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        savedDoctor = new Doctor();
        savedDoctor.setId(1L);
        savedDoctor.setDoctorCode("DOC-2026-0001");
        savedDoctor.setFullName("Dr. Vikram Sethi");
        savedDoctor.setSpecialization("Cardiology");
        savedDoctor.setQualification("MD, DM");
        savedDoctor.setPhoneNumber("9123456701");
        savedDoctor.setEmail("vikram.sethi@hospitalcare.local");
        savedDoctor.setDepartment("Cardiology Department");
        savedDoctor.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
    }

    @Test
    void registerDoctor_shouldSucceed() {
        when(doctorRepository.findByPhoneNumber("9123456701")).thenReturn(Optional.empty());
        when(doctorRepository.findByEmailIgnoreCase("vikram.sethi@hospitalcare.local")).thenReturn(Optional.empty());
        when(doctorRepository.count()).thenReturn(0L);
        when(doctorRepository.findByDoctorCode(anyString())).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        DoctorDto result = doctorService.registerDoctor(doctorDto);

        assertNotNull(result);
        assertEquals("DOC-2026-0001", result.getDoctorCode());
        assertEquals("Cardiology", result.getSpecialization());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void getDoctorById_shouldReturnDoctorDetails() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(savedDoctor));

        DoctorDto result = doctorService.getDoctorById(1L);

        assertEquals("Dr. Vikram Sethi", result.getFullName());
        assertEquals(AvailabilityStatus.AVAILABLE, result.getAvailabilityStatus());
    }

    @Test
    void getDoctorById_shouldThrowWhenNotFound() {
        when(doctorRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(404L));
    }

    @Test
    void getDoctorsBySpecialization_shouldReturnMatchingDoctors() {
        when(doctorRepository.findBySpecializationIgnoreCaseOrderByFullNameAsc("Cardiology"))
                .thenReturn(List.of(savedDoctor));

        List<DoctorDto> doctors = doctorService.getDoctorsBySpecialization("Cardiology");

        assertEquals(1, doctors.size());
        assertEquals("Cardiology", doctors.get(0).getSpecialization());
    }

    @Test
    void updateDoctor_shouldUpdateDetailsAndAvailability() {
        DoctorDto updateDto = new DoctorDto();
        updateDto.setFullName("Dr. Vikram Sethi");
        updateDto.setSpecialization("Interventional Cardiology");
        updateDto.setQualification("MD, DM, FACC");
        updateDto.setPhoneNumber("9123456701");
        updateDto.setEmail("vikram.sethi@hospitalcare.local");
        updateDto.setDepartment("Cardiology Wing");
        updateDto.setAvailabilityStatus(AvailabilityStatus.ON_LEAVE);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(savedDoctor));
        when(doctorRepository.findByPhoneNumber("9123456701")).thenReturn(Optional.of(savedDoctor));
        when(doctorRepository.findByEmailIgnoreCase("vikram.sethi@hospitalcare.local"))
                .thenReturn(Optional.of(savedDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(inv -> inv.getArgument(0));

        DoctorDto updated = doctorService.updateDoctor(1L, updateDto);

        assertEquals("Interventional Cardiology", updated.getSpecialization());
        assertEquals(AvailabilityStatus.ON_LEAVE, updated.getAvailabilityStatus());
    }
}
