package com.healthcare.billing_service.service;

import com.healthcare.billing_service.dto.BillingRequestDTO;
import com.healthcare.billing_service.dto.BillingResponseDTO;
import jakarta.validation.Valid;

public interface BillingService {

    BillingResponseDTO payBill(@Valid BillingRequestDTO billingRequestDTO);

    BillingResponseDTO getBill(Long id);

    BillingResponseDTO getBillForAppointment(Long appointmentId);
}
