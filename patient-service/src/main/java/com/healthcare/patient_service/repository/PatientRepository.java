package com.healthcare.patient_service.repository;

import com.healthcare.patient_service.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByIsDeletedFalse();

    Optional<Patient> findByIdAndIsDeletedFalse(Long id);
}
