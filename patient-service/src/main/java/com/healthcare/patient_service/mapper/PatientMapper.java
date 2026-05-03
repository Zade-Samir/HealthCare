package com.healthcare.patient_service.mapper;

import com.healthcare.patient_service.dto.PatientRequestDTO;
import com.healthcare.patient_service.dto.PatientResponseDTO;
import com.healthcare.patient_service.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    Patient PatientRequestDtoToPatient(PatientRequestDTO patientRequestDTO);

    PatientResponseDTO PatientToPatientResponseDto(Patient patient);

    void updatePatientFromDto(PatientRequestDTO patientRequestDTO, @MappingTarget Patient patient);
}
