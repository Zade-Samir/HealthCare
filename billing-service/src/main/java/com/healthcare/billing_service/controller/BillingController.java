package com.healthcare.billing_service.controller;

import com.healthcare.billing_service.common.ApiResponse;
import com.healthcare.billing_service.dto.BillingRequestDTO;
import com.healthcare.billing_service.dto.BillingResponseDTO;
import com.healthcare.billing_service.service.BillingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("billing")
@AllArgsConstructor
public class BillingController {

    private final BillingService billingService;

    //Processes payment -> doing payment
    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    @CircuitBreaker(name = "billingService", fallbackMethod = "billingFallback")
    public ResponseEntity<ApiResponse<BillingResponseDTO>> payBill(
            @Valid @RequestBody BillingRequestDTO billingRequestDTO
            ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment successful!",
                        billingService.payBill(billingRequestDTO)
                )
        );
    }

    //FALLBACK METHOD - Signature must match original method + Exception param
    public ResponseEntity<ApiResponse<BillingResponseDTO>> billingFallback(
            BillingRequestDTO billingRequestDTO,
            Exception ex
    ) {
        ApiResponse<BillingResponseDTO> objectApiResponse = new ApiResponse<>(
                false,
                "Service is currently busy or down. Please try again later. Error: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(objectApiResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }


    //Fetches billing details by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<BillingResponseDTO>> getBill(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Fetching bill successful with ID" + id,
                        billingService.getBill(id)
                )
        );
    }

    //Retrieves payment info for a specific appointment
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<BillingResponseDTO>> getBillForAppointment(
            @PathVariable Long appointmentId
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Fetching bill successful for appointment with ID" + appointmentId,
                        billingService.getBillForAppointment(appointmentId)
                )
        );
    }
}
