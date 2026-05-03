package com.healthcare.patient_service.service;

import com.healthcare.patient_service.dto.PatientRequestDTO;
import com.healthcare.patient_service.dto.PatientResponseDTO;

import java.util.List;

public interface PatientService {
    PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO);

    PatientResponseDTO getPatient(Long id);

    List<PatientResponseDTO> getAllPatients();

    PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO);

    void deletePatient(Long id);
}
