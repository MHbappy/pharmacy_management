package com.pharmacy.management.service;

import com.pharmacy.management.model.MedicalDiagnosis;
import com.pharmacy.management.repository.MedicalDiagnosisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class MedicalDiagnosisService {

    private final Logger log = LoggerFactory.getLogger(MedicalDiagnosisService.class);

    private final MedicalDiagnosisRepository medicalDiagnosisRepository;
    public MedicalDiagnosisService(MedicalDiagnosisRepository medicalDiagnosisRepository) {
        this.medicalDiagnosisRepository = medicalDiagnosisRepository;
    }

    public MedicalDiagnosis save(MedicalDiagnosis medicalDiagnosis) {
        log.debug("Request to save MedicalDiagnosis : {}", medicalDiagnosis);
        medicalDiagnosis.setIsActive(true);
        return medicalDiagnosisRepository.save(medicalDiagnosis);
    }

    @Transactional(readOnly = true)
    public Page<MedicalDiagnosis> findAll(Pageable pageable) {
        log.debug("Request to get all MedicalDiagnoses");
        return medicalDiagnosisRepository.findAllByIsActive(true, pageable);
    }

    
    @Transactional(readOnly = true)
    public Optional<MedicalDiagnosis> findOne(Long id) {
        log.debug("Request to get MedicalDiagnosis : {}", id);
        return medicalDiagnosisRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete MedicalDiagnosis : {}", id);
        Optional<MedicalDiagnosis> medicalDiagnosisOptional = medicalDiagnosisRepository.findById(id);
        medicalDiagnosisOptional.ifPresentOrElse(orders -> {
            orders.setIsActive(false);
            medicalDiagnosisRepository.save(orders);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no region!");
        });
    }

}
