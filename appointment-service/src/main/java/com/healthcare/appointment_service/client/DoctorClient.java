package com.healthcare.appointment_service.client;

import com.healthcare.appointment_service.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DOCTOR-SERVICE") //eureka service name
public interface DoctorClient {

    @GetMapping("/doctors/{id}")
    ResponseEntity<ApiResponse<Object>> getDoctorById(@PathVariable Long id);
}