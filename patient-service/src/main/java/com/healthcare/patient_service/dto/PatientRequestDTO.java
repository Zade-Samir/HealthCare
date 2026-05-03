package com.healthcare.patient_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

//what we are getting from user
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 100, message = "Name cannot be more than 100 characters")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    private String phone;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age can't be negative")
    @Max(value = 120, message = "Age can't be more than 120")
    private Integer age;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be male, female or other")
    private String gender;

    @NotBlank(message = "Address is required")
    private String address;
}
