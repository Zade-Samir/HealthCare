package com.healthcare.billing_service.client;

import com.healthcare.billing_service.common.ApiResponse;
import com.healthcare.billing_service.dto.AppointmentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "APPOINTMENT-SERVICE") //eureka service name
public interface AppointmentClient {

    @GetMapping("/appointments/{id}")
    ResponseEntity<ApiResponse<AppointmentResponseDTO>> getAppointmentById(@PathVariable Long id);
}