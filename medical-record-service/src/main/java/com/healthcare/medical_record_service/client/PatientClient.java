package com.healthcare.medical_record_service.client;

import com.healthcare.medical_record_service.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PATIENT-SERVICE") //eureka service name
public interface PatientClient {

    @GetMapping("/patients/{id}")
    ResponseEntity<ApiResponse<Object>> getPatientById(@PathVariable Long id);
}
