package com.healthcare.billing_service.dto;

import com.healthcare.billing_service.entity.PaymentMethod;
import com.healthcare.billing_service.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BillingResponseDTO {
    private Long id;
    private Long appointmentId;
    private double amount;
    private PaymentStatus paymentStatus; //PAID, PENDING
    private PaymentMethod paymentMethod; //UPI, CARD, CASH
    private String transactionId;
}
