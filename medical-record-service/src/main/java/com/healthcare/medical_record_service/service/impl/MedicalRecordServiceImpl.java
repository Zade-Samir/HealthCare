package com.healthcare.medical_record_service.service.impl;

import com.healthcare.medical_record_service.client.DoctorClient;
import com.healthcare.medical_record_service.client.PatientClient;
import com.healthcare.medical_record_service.dto.MedicalRecordRequestDTO;
import com.healthcare.medical_record_service.dto.MedicalRecordResponseDTO;
import com.healthcare.medical_record_service.entity.MedicalRecord;
import com.healthcare.medical_record_service.exception.MedicalRecordNotFoundException;
import com.healthcare.medical_record_service.mapper.MedicalRecordMapper;
import com.healthcare.medical_record_service.repository.MedicalRecordRepository;
import com.healthcare.medical_record_service.service.MedicalRecordService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;

    @Override
    public MedicalRecordResponseDTO createRecord(MedicalRecordRequestDTO medicalRecordRequestDTO) {

        //CALL PATIENT-SERVICE TO VALIDATE THIS
        try {
            patientClient.getPatientById(medicalRecordRequestDTO.getPatientId());
        }
        catch (Exception e) {
            e.printStackTrace(); // Yeh console mein asali error batayega
            throw new MedicalRecordNotFoundException("Validation failed: " + e.getMessage());
        }

        //CALL DOCTOR-SERVICE TO VALIDATE THIS
        try {
            doctorClient.getDoctorById(medicalRecordRequestDTO.getDoctorId());
        }
        catch (Exception e) {
            e.printStackTrace(); // Yeh console mein asali error batayega
            throw new MedicalRecordNotFoundException("Validation failed: " + e.getMessage());
        }

        MedicalRecord medicalRecord = medicalRecordMapper
                .requestDtoToMedicalRecord(medicalRecordRequestDTO);

        medicalRecordRepository.save(medicalRecord);
        return medicalRecordMapper.medicalRecordToResponseDto(medicalRecord);
    }


    //Fetches specific medical record
    @Override
    public MedicalRecordResponseDTO getRecord(Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new MedicalRecordNotFoundException("Medical Record not found in DB")
                );
        return medicalRecordMapper.medicalRecordToResponseDto(medicalRecord);
    }


    @Override
    public List<MedicalRecordResponseDTO> getAllPatientRecords(Long patientId) {
        List<MedicalRecord> medicalRecords = medicalRecordRepository
                .findByPatientIdAndIsDeletedFalse(patientId);

        return medicalRecords.stream()
                .map(x -> medicalRecordMapper.medicalRecordToResponseDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecord(Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new MedicalRecordNotFoundException("Medical Record not found in DB")
                );
        medicalRecord.setDeleted(true);
        medicalRecordRepository.save(medicalRecord);
    }
}
