package com.healthcare.doctor_service.controller;

import com.healthcare.doctor_service.common.ApiResponse;
import com.healthcare.doctor_service.dto.DoctorRequestDTO;
import com.healthcare.doctor_service.dto.DoctorResponseDTO;
import com.healthcare.doctor_service.service.DoctorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@AllArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    //add a new doctor
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> addDoctor(
            @Valid @RequestBody DoctorRequestDTO doctorRequestDTO
            ) {
        ApiResponse<DoctorResponseDTO> doctorAdded = new ApiResponse<>(
                true,
                "Doctor Added",
                doctorService.addDoctor(doctorRequestDTO)
        );
        return new ResponseEntity<>(doctorAdded, HttpStatus.CREATED);
    }

    //fetch doctor by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> getDoctor(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "doctor fetched successfully",
                        doctorService.getDoctor(id)
                )
        );
    }


    //get all doctors from DB
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<DoctorResponseDTO>>> getAllDoctors() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All doctors are fetched successfully",
                        doctorService.getAllDoctors()
                )
        );
    }


    //get only available doctors
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<DoctorResponseDTO>>> getAllAvailableDoctors() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All available doctors are fetched successfully",
                        doctorService.getAllAvailableDoctors()
                )
        );
    }

    //Filters doctors by specialization
    @GetMapping("specialization/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<DoctorResponseDTO>>> getDoctorsBySpecialization(
            @PathVariable("type") String specialization
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All specialization type doctors are fetched successfully",
                        doctorService.getDoctorsBySpecialization(specialization)
                )
        );
    }


    //update doctors information
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDTO doctorRequestDTO
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Doctor updated successfully!",
                        doctorService.updateDoctor(id, doctorRequestDTO)
                )
        );
    }


    //soft delete the doctor
    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Soft deletion is Successfully!",
                        doctorService.deleteDoctor(id)
                )
        );
    }
}
