package com.healthcare.medical_record_service.repository;

import com.healthcare.medical_record_service.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByIdAndIsDeletedFalse(Long id);

    List<MedicalRecord> findByPatientIdAndIsDeletedFalse(Long patientId);
}
