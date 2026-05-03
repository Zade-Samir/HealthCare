package com.healthcare.appointment_service.mapper;

import com.healthcare.appointment_service.dto.AppointmentRequestDTO;
import com.healthcare.appointment_service.dto.AppointmentResponseDTO;
import com.healthcare.appointment_service.entity.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    Appointment requestDtoToAppointment(AppointmentRequestDTO appointmentRequestDTO);

    AppointmentResponseDTO appointmentToResponseDto(Appointment appointment);

}
