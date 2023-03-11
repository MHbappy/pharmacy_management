package com.pharmacy.management.repository;

import com.pharmacy.management.model.MedicalDiagnosis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalDiagnosisRepository extends JpaRepository<MedicalDiagnosis, Long> {

    Page<MedicalDiagnosis> findAllByIsActive(Boolean isActive, Pageable pageable);
}
