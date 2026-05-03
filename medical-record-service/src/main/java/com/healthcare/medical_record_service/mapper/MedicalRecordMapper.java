package com.healthcare.medical_record_service.mapper;

import com.healthcare.medical_record_service.dto.MedicalRecordRequestDTO;
import com.healthcare.medical_record_service.dto.MedicalRecordResponseDTO;
import com.healthcare.medical_record_service.entity.MedicalRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {

    MedicalRecord requestDtoToMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO);

    MedicalRecordResponseDTO medicalRecordToResponseDto(MedicalRecord medicalRecord);
}
