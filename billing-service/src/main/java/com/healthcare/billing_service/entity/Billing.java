package com.healthcare.billing_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing")
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long appointmentId;

    @Column(nullable = false)
    private Long patientId;

    //BigDecimal in place of double, because double has problem
    // of precision issue(rounding errors after decimal point)
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; //PAID, PENDING

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.NONE; //UPI, CARD, CASH

    private String transactionId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
    }
}
