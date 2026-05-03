package com.healthcare.appointment_service.controller;

import com.healthcare.appointment_service.common.ApiResponse;
import com.healthcare.appointment_service.dto.AppointmentRequestDTO;
import com.healthcare.appointment_service.dto.AppointmentResponseDTO;
import com.healthcare.appointment_service.dto.AppointmentStatus;
import com.healthcare.appointment_service.service.AppointmentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("appointments")
@AllArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    //book an appointment
    @CircuitBreaker(name = "appointmentService", fallbackMethod = "appointmentFallback")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> book(
            @Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO
            ) {
        ApiResponse<AppointmentResponseDTO> booking = new ApiResponse<>(
                true,
                "booking is completed",
                appointmentService.book(appointmentRequestDTO)
        );

        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }


    //FALLBACK METHOD - Signature must match original method + Exception param
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> appointmentFallback(
            AppointmentRequestDTO appointmentRequestDTO,
            Exception ex
    ) {
        ApiResponse<AppointmentResponseDTO> objectApiResponse = new ApiResponse<>(
                false,
                "Service is currently busy or down. Please try again later. Error: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(objectApiResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }





    //fetch the appointment details
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> getAppointment(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Appointment is fetched succesfully!",
                        appointmentService.getAppointment(id)
                )
        );
    }

    //return all appointment for patient id
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> getAllAppByPatientId(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetched all appointments by patient id",
                        appointmentService.getAllAppByPatientId(patientId)
                )
        );
    }

    //return all appointment for doctor id
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> getAllAppByDoctorId(
            @PathVariable Long doctorId
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "fetched all appointments by doctor id",
                        appointmentService.getAllAppByDoctorId(doctorId)
                )
        );
    }

    //cancel the appointment
    @PutMapping("{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> cancelAppointment(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Appointment cancelled.",
                        appointmentService.cancelAppointment(id)
                )
        );
    }

    //mark appointment as completed
    @PutMapping("{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> completeAppointment(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Appointment completed.",
                        appointmentService.completeAppointment(id)
                )
        );
    }

}
