package com.healthcare.doctor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
public class DoctorResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String specialization;
    private int experience;
    private boolean isAvailable;
    private double rating;
}
