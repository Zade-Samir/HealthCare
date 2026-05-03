package com.healthcare.doctor_service.service;

import com.healthcare.doctor_service.dto.DoctorRequestDTO;
import com.healthcare.doctor_service.dto.DoctorResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DoctorService {
    DoctorResponseDTO addDoctor(@Valid DoctorRequestDTO doctorRequestDTO);

    DoctorResponseDTO getDoctor(Long id);

    List<DoctorResponseDTO> getAllDoctors();

    List<DoctorResponseDTO> getAllAvailableDoctors();

    List<DoctorResponseDTO> getDoctorsBySpecialization(String specialization);

    DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO doctorRequestDTO);

    String deleteDoctor(Long id);
}
