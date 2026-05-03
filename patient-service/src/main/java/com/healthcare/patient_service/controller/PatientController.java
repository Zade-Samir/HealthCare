package com.healthcare.patient_service.controller;

import com.healthcare.patient_service.common.ApiResponse;
import com.healthcare.patient_service.dto.PatientRequestDTO;
import com.healthcare.patient_service.dto.PatientResponseDTO;
import com.healthcare.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("patients")
@AllArgsConstructor
public class PatientController {

    private final PatientService patientService;

    //create a new patient
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')") //spring joined ROLE_ in front of them -> becames ROLE_ADMIN or ROLE_PATIENT
    public ResponseEntity<ApiResponse<PatientResponseDTO>> createPatient(
            @Valid @RequestBody PatientRequestDTO patientRequestDTO
    ) {
        ApiResponse<PatientResponseDTO> doctorAdded = new ApiResponse<>(
                true,
                "Patient Added",
                patientService.createPatient(patientRequestDTO)
        );

        return new ResponseEntity<>(doctorAdded, HttpStatus.CREATED);
    }

    //fetch patient details using patient id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> getPatient(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "patient fetched successfully with Id",
                        patientService.getPatient(id)
                )
        );
    }

    //get all patients
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") //only admin can access this
    public ResponseEntity<ApiResponse<List<PatientResponseDTO>>> getAllPatients() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All patients are fetched successfully",
                        patientService.getAllPatients()
                )
        );
    }

    //update the patient information
    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDTO patientRequestDTO
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "patient updated successfully!",
                        patientService.updatePatient(id, patientRequestDTO)
                )
        );
    }

    //soft delete patient from DB
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deletePatient(
            @PathVariable Long id
    ) {
        patientService.deletePatient(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Soft deletion is Successfully!",
                        "Patient Deleted successfully!"
                )
        );
    }
}
