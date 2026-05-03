package com.healthcare.doctor_service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DoctorRequestDTO {

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 100, message = "Name cannot be more than 100 characters")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "specialization is required")
    private String specialization;

    @NotNull(message = "experience is required")
    @Min(value = 0, message = "experience can't be negative")
    @Max(value = 50, message = "experience can't be more than 50 years")
    private int experience;

    @NotNull(message = "Availability is required")
    private boolean isAvailable;

    @NotNull(message = "rating is required")
    @Min(value = 0, message = "rating can't be negative")
    @Max(value = 5, message = "rating can't be more than 5")
    private double rating;
}
