package com.healthcare.medical_record_service.controller;

import com.healthcare.medical_record_service.common.ApiResponse;
import com.healthcare.medical_record_service.dto.MedicalRecordRequestDTO;
import com.healthcare.medical_record_service.dto.MedicalRecordResponseDTO;
import com.healthcare.medical_record_service.service.MedicalRecordService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    //Creates medical record after appointment completion (diagnosis, prescription)
    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @CircuitBreaker(name = "medicalRecordService", fallbackMethod = "medicalRecordFallback")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> createRecord (
            @Valid @RequestBody MedicalRecordRequestDTO medicalRecordRequestDTO
            ) {
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Medical Record created successfully!",
                        medicalRecordService.createRecord(medicalRecordRequestDTO)
                )
        );
    }

    //FALLBACK METHOD - Signature must match original method + Exception param
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> medicalRecordFallback(
            MedicalRecordRequestDTO medicalRecordRequestDTO,
            Exception ex
    ) {
        ApiResponse<MedicalRecordResponseDTO> objectApiResponse = new ApiResponse<>(
                false,
                "Service is currently busy or down. Please try again later. Error: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(objectApiResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }


    //Fetches specific medical record
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> getRecord(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Fetching of single medical record successful with Id " + id,
                        medicalRecordService.getRecord(id)
                )
        );
    }

    //Returns complete medical history of patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponseDTO>>> getAllPatientRecords(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Fetching of all medical record of single patient is successful with patient ID " + patientId,
                        medicalRecordService.getAllPatientRecords(patientId)
                )
        );
    }


    //Deletes record (soft delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRecord(
            @PathVariable Long id
    ) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Medical Record deleted of ID " + id,
                        "Medical Record deleted successfully!"
                )
        );
    }
}
