package com.healthcare.billing_service.service.impl;

import com.healthcare.billing_service.client.AppointmentClient;
import com.healthcare.billing_service.client.PatientClient;
import com.healthcare.billing_service.common.ApiResponse;
import com.healthcare.billing_service.dto.AppointmentResponseDTO;
import com.healthcare.billing_service.dto.BillingRequestDTO;
import com.healthcare.billing_service.dto.BillingResponseDTO;
import com.healthcare.billing_service.entity.Billing;
import com.healthcare.billing_service.exception.BillingNotFoundException;
import com.healthcare.billing_service.mapper.BillingMapper;
import com.healthcare.billing_service.repository.BillingRepository;
import com.healthcare.billing_service.service.BillingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final BillingMapper billingMapper;
    private final PatientClient patientClient;
    private final AppointmentClient appointmentClient;

    @Override
    public BillingResponseDTO payBill(BillingRequestDTO billingRequestDTO) {


        //fetch appointment from appointment-service
        ResponseEntity<ApiResponse<AppointmentResponseDTO>> response;

        try {
            response = appointmentClient.getAppointmentById(
                    billingRequestDTO.getAppointmentId());
        }
        catch (Exception e) {
            // FIX: Pass 'e' to preserve the original failure reason
            throw new BillingNotFoundException("Appointment validation failed: Service down or invalid ID", e);
        }

        AppointmentResponseDTO appointment = response.getBody().getData();

        // Business Logic Validation
        if (!appointment.getPatientId().equals(billingRequestDTO.getPatientId())) {
            throw new RuntimeException("Fraudulent Activity Detected: Patient ID mismatch for this appointment!");
        }

        //CALL PATIENT-SERVICE TO VALIDATE THIS
        try {
            patientClient.getPatientById(billingRequestDTO.getPatientId());
        }
        catch (Exception e) {
            // FIX: Replaced e.printStackTrace() with Exception Chaining
            throw new BillingNotFoundException("Patient validation failed: " + e.getMessage(), e);
        }

        Billing billing = billingMapper.requestDtoToBilling(billingRequestDTO);
        billingRepository.save(billing);

        return billingMapper.billingToResponseDto(billing);
    }

    @Override
    public BillingResponseDTO getBill(Long id) {

        Billing billing = billingRepository.findById(id)
                .orElseThrow(
                        () -> new BillingNotFoundException("Billing record not found in DB")
                );

        return billingMapper.billingToResponseDto(billing);
    }

    @Override
    public BillingResponseDTO getBillForAppointment(Long appointmentId) {

        Billing billing = billingRepository.findByAppointmentId(appointmentId)
                .orElseThrow(
                        () -> new BillingNotFoundException("Billing record not found in DB")
                );
        return billingMapper.billingToResponseDto(billing);
    }
}
