package com.healthcare.medical_record_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class MedicalRecordResponseDTO {

    private Long id;
    private Long patientId;
    private Long doctorId;
    private String diagnosis;
    private String prescription;
    private LocalDateTime createdAt;
}
