package com.healthcare.doctor_service.service.impl;

import com.healthcare.doctor_service.dto.DoctorRequestDTO;
import com.healthcare.doctor_service.dto.DoctorResponseDTO;
import com.healthcare.doctor_service.entity.Doctor;
import com.healthcare.doctor_service.exception.DoctorNotFoundException;
import com.healthcare.doctor_service.mapper.DoctorMapper;
import com.healthcare.doctor_service.repository.DoctorRepository;
import com.healthcare.doctor_service.service.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    //add a doctor
    @Override
    public DoctorResponseDTO addDoctor(DoctorRequestDTO doctorRequestDTO) {
        Doctor doctor = doctorMapper.doctorRequestDtoToDoctor(doctorRequestDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.doctorToDoctorResponseDto(savedDoctor);
    }

    @Override
    public DoctorResponseDTO getDoctor(Long id) {
        Doctor doctorFromDB = doctorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new DoctorNotFoundException("Doctor not available in DB with id " + id)
                );

        return doctorMapper.doctorToDoctorResponseDto(doctorFromDB);
    }

    @Override
    public List<DoctorResponseDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findByIsDeletedFalse();

        return doctors.stream()
                .map(x -> doctorMapper.doctorToDoctorResponseDto(x))
                .collect(Collectors.toList());
    }


    //get all available doctors
    @Override
    public List<DoctorResponseDTO> getAllAvailableDoctors() {
        List<Doctor> doctors = doctorRepository.findByIsDeletedFalseAndIsAvailableTrue();

        if (doctors.isEmpty()) {
            throw new DoctorNotFoundException("No available Doctors are in DB");
        }

        return doctors.stream()
                .map(x -> doctorMapper.doctorToDoctorResponseDto(x))
                .collect(Collectors.toList());
    }


    //Filters doctors by specialization
    @Override
    public List<DoctorResponseDTO> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = doctorRepository.findBySpecializationAndIsDeletedFalse(specialization);

        if (doctors.isEmpty()) {
            throw new DoctorNotFoundException("Doctor not available in DB with specialization " + specialization);
        }

        return doctors.stream()
                .map(x -> doctorMapper.doctorToDoctorResponseDto(x))
                .collect(Collectors.toList());

    }

    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO doctorRequestDTO) {
        Doctor doctor = doctorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new DoctorNotFoundException("Doctor not available in DB with id " + id)
                );
        doctorMapper.updateDoctorFromDto(doctorRequestDTO, doctor);
        Doctor savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.doctorToDoctorResponseDto(savedDoctor);
    }

    //soft delete the doctor
    @Override
    public String deleteDoctor(Long id) {

        Doctor doctor = doctorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new DoctorNotFoundException("Doctor not available in DB with id " + id)
                );
        doctor.setDeleted(true);
        doctorRepository.save(doctor);

        return "Doctor deleted Successfully!";
    }
}
