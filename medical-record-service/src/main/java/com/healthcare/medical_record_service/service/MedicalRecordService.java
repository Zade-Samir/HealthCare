package com.healthcare.medical_record_service.service;


import com.healthcare.medical_record_service.dto.MedicalRecordRequestDTO;
import com.healthcare.medical_record_service.dto.MedicalRecordResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface MedicalRecordService {
    MedicalRecordResponseDTO createRecord(@Valid MedicalRecordRequestDTO medicalRecordRequestDTO);

    MedicalRecordResponseDTO getRecord(Long id);

    List<MedicalRecordResponseDTO> getAllPatientRecords(Long patientId);

    void deleteRecord(Long id);
}
