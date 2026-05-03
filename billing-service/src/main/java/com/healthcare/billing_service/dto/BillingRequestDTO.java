package com.healthcare.billing_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class BillingRequestDTO {

    @NotNull(message = "appointmentId ID is required")
    private Long appointmentId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    @NotBlank(message = "payment status is required")
    private String paymentStatus; //PAID, PENDING

    @NotBlank(message = "payment method is required")
    private String paymentMethod; //UPI, CARD, CASH

    private String transactionId;

}
