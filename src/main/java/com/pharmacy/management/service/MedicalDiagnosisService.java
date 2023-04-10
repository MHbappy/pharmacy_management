package com.pharmacy.management.service;

import com.pharmacy.management.config.ExcelHelper;
import com.pharmacy.management.dto.request.UserDataExcelDTO;
import com.pharmacy.management.model.CompanyPolicy;
import com.pharmacy.management.model.MedicalDiagnosis;
import com.pharmacy.management.model.Roles;
import com.pharmacy.management.model.Users;
import com.pharmacy.management.repository.MedicalDiagnosisRepository;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;


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

    public Boolean getMedicalDiagnosisFromExcel(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String ext = FilenameUtils.getExtension(fileName);
            List<MedicalDiagnosis> medicalDiagnoses = ExcelHelper.excelToMedicalDiagnosis(file.getInputStream(), ext);
            List<MedicalDiagnosis> medicalDiagnosisArrayList = new ArrayList<>();
            for (MedicalDiagnosis medicalDiagnosis: medicalDiagnoses) {
                Optional<MedicalDiagnosis> medicalDiagnosisOptional = medicalDiagnosisRepository.findByNameAndIsActive(medicalDiagnosis.getName(), true);
                if (medicalDiagnosisOptional.isPresent()){
                    throw new RuntimeException("Already exist : " + medicalDiagnosis.getName());
                }

                medicalDiagnosis.setIsActive(true);
                medicalDiagnosisArrayList.add(medicalDiagnosis);
            }
            medicalDiagnosisRepository.saveAll(medicalDiagnosisArrayList);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
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
