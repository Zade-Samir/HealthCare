package com.healthcare.appointment_service.service.impl;

import com.healthcare.appointment_service.client.DoctorClient;
import com.healthcare.appointment_service.client.PatientClient;
import com.healthcare.appointment_service.dto.AppointmentRequestDTO;
import com.healthcare.appointment_service.dto.AppointmentResponseDTO;
import com.healthcare.appointment_service.dto.AppointmentStatus;
import com.healthcare.appointment_service.entity.Appointment;
import com.healthcare.appointment_service.exception.AppointmentNotFoundException;
import com.healthcare.appointment_service.exception.InvalidStatusTransitionException;
import com.healthcare.appointment_service.mapper.AppointmentMapper;
import com.healthcare.appointment_service.repository.AppointmentRepository;
import com.healthcare.appointment_service.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;

    @Override
    public AppointmentResponseDTO book(AppointmentRequestDTO appointmentRequestDTO) {

        //CALL PATIENT-SERVICE TO VALIDATE THIS
        try {
            patientClient.getPatientById(appointmentRequestDTO.getPatientId());
        }
        catch (Exception e) {
            e.printStackTrace(); // Yeh console mein asali error batayega
            throw new AppointmentNotFoundException("Validation failed: " + e.getMessage());
        }

        //CALL DOCTOR-SERVICE TO VALIDATE THIS
        try {
            doctorClient.getDoctorById(appointmentRequestDTO.getDoctorId());
        }
        catch (Exception e) {
            e.printStackTrace(); // Yeh console mein asali error batayega
            throw new AppointmentNotFoundException("Validation failed: " + e.getMessage());
        }

        //convert requestdto to appointment
        Appointment appointment = appointmentMapper.requestDtoToAppointment(appointmentRequestDTO);

        //update status
        appointment.setStatus(AppointmentStatus.BOOKED);

        //save to repo
        Appointment savedAppointment = appointmentRepository.save(appointment);

        //return by converting appointment into responseDto
        return appointmentMapper.appointmentToResponseDto(savedAppointment);
    }

    @Override
    public AppointmentResponseDTO getAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new AppointmentNotFoundException("Appointment not available.")
        );
        return appointmentMapper.appointmentToResponseDto(appointment);
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppByPatientId(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);

        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("Appointment not available.");
        }

        return appointments.stream()
                .map(x -> appointmentMapper.appointmentToResponseDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppByDoctorId(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);

        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("Appointment not available.");
        }

        return appointments.stream()
                .map(x -> appointmentMapper.appointmentToResponseDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new AppointmentNotFoundException("Appointment not available.")
        );

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new
                    InvalidStatusTransitionException(
                    "Only BOOKED appointments can be cancelled. Current status: "
                            + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.appointmentToResponseDto(savedAppointment);
    }

    @Override
    public AppointmentResponseDTO completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new AppointmentNotFoundException("Appointment not available.")
        );

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new
                    InvalidStatusTransitionException(
                            "Only BOOKED appointments can be completed. Current status: "
                                    + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.appointmentToResponseDto(savedAppointment);
    }
}
