package com.healthcare.patient_service.service.impl;

import com.healthcare.patient_service.dto.PatientRequestDTO;
import com.healthcare.patient_service.dto.PatientResponseDTO;
import com.healthcare.patient_service.entity.Patient;
import com.healthcare.patient_service.exception.PatientNotFoundException;
import com.healthcare.patient_service.mapper.PatientMapper;
import com.healthcare.patient_service.repository.PatientRepository;
import com.healthcare.patient_service.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    //create a patient
    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        Patient savedPatient = patientRepository.save(
                patientMapper.PatientRequestDtoToPatient(patientRequestDTO)
        );
        return patientMapper.PatientToPatientResponseDto(savedPatient);
    }


    //get a single patient
    @Override
    public PatientResponseDTO getPatient(Long id) {
        Patient patientFromDb = patientRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found in DB")
        );
        return patientMapper.PatientToPatientResponseDto(patientFromDb);
    }


    //get all the patients
    @Override
    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findByIsDeletedFalse();

        return patients.stream()
                .map(x -> patientMapper.PatientToPatientResponseDto(x))
                .collect(Collectors.toList());
    }


    //update the existing patient
    @Override
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO) {
        //get patient by id
        Patient patient = patientRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found in DB")
        );
        //update patient by requestDTO
//        patient.setName(patientRequestDTO.getName());
//        patient.setEmail(patientRequestDTO.getEmail());
//        patient.setPhone(patientRequestDTO.getPhone());
//        patient.setAge(patientRequestDTO.getAge());
//        patient.setGender(patientRequestDTO.getGender());
//        patient.setAddress(patientRequestDTO.getAddress());

        patientMapper.updatePatientFromDto(patientRequestDTO, patient);

        //save updated patient
        Patient savedPatient = patientRepository.save(patient);
        //convert patient into responseDTO
        return patientMapper.PatientToPatientResponseDto(savedPatient);
    }


    //delete the patient
    @Override
    public void deletePatient(Long id) {

        Patient patient = patientRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                () -> new PatientNotFoundException("Patient not found in DB")
        );
        patient.setDeleted(true);
        patientRepository.save(patient);
    }
}
