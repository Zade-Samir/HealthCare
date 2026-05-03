package com.healthcare.appointment_service.service;

import com.healthcare.appointment_service.dto.AppointmentRequestDTO;
import com.healthcare.appointment_service.dto.AppointmentResponseDTO;
import com.healthcare.appointment_service.dto.AppointmentStatus;
import jakarta.validation.Valid;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO book(@Valid AppointmentRequestDTO appointmentRequestDTO);

    AppointmentResponseDTO getAppointment(Long id);

    List<AppointmentResponseDTO> getAllAppByPatientId(Long patientId);

    List<AppointmentResponseDTO> getAllAppByDoctorId(Long doctorId);

    AppointmentResponseDTO cancelAppointment(Long id);

    AppointmentResponseDTO completeAppointment(Long id);
}
