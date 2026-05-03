package com.healthcare.doctor_service.mapper;

import com.healthcare.doctor_service.dto.DoctorRequestDTO;
import com.healthcare.doctor_service.dto.DoctorResponseDTO;
import com.healthcare.doctor_service.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    Doctor doctorRequestDtoToDoctor(DoctorRequestDTO doctorRequestDTO);

    DoctorResponseDTO doctorToDoctorResponseDto(Doctor doctor);

    //update the doctor from doctorRequestDto
    void updateDoctorFromDto(DoctorRequestDTO doctorRequestDTO, @MappingTarget Doctor doctor);
}
