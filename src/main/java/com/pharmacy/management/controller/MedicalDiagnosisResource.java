package com.pharmacy.management.controller;

import com.pharmacy.management.model.MedicalDiagnosis;
import com.pharmacy.management.repository.MedicalDiagnosisRepository;
import com.pharmacy.management.service.MedicalDiagnosisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class MedicalDiagnosisResource {

    private final Logger log = LoggerFactory.getLogger(MedicalDiagnosisResource.class);

    private final MedicalDiagnosisService medicalDiagnosisService;

    private final MedicalDiagnosisRepository medicalDiagnosisRepository;

    public MedicalDiagnosisResource(
        MedicalDiagnosisService medicalDiagnosisService,
        MedicalDiagnosisRepository medicalDiagnosisRepository
    ) {
        this.medicalDiagnosisService = medicalDiagnosisService;
        this.medicalDiagnosisRepository = medicalDiagnosisRepository;
    }

    @PostMapping("/medical-diagnoses")
    public ResponseEntity<MedicalDiagnosis> createMedicalDiagnosis(@RequestBody MedicalDiagnosis medicalDiagnosis)
        throws URISyntaxException {
        log.debug("REST request to save MedicalDiagnosis : {}", medicalDiagnosis);
        if (medicalDiagnosis.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new medicalDiagnosis cannot already have an ID");
        }
        MedicalDiagnosis result = medicalDiagnosisService.save(medicalDiagnosis);
        return ResponseEntity
            .created(new URI("/api/medical-diagnoses/" + result.getId()))
            .body(result);
    }

    @PutMapping("/medical-diagnoses/{id}")
    public ResponseEntity<MedicalDiagnosis> updateMedicalDiagnosis(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MedicalDiagnosis medicalDiagnosis
    ) throws URISyntaxException {
        log.debug("REST request to update MedicalDiagnosis : {}, {}", id, medicalDiagnosis);
        if (medicalDiagnosis.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, medicalDiagnosis.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!medicalDiagnosisRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        MedicalDiagnosis result = medicalDiagnosisService.save(medicalDiagnosis);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/medical-diagnoses")
    public Page<MedicalDiagnosis> getAllMedicalDiagnoses(Pageable pageable) {
        log.debug("REST request to get all MedicalDiagnoses");
        return medicalDiagnosisService.findAll(pageable);
    }

    @GetMapping("/medical-diagnoses/{id}")
    public ResponseEntity<MedicalDiagnosis> getMedicalDiagnosis(@PathVariable Long id) {
        log.debug("REST request to get MedicalDiagnosis : {}", id);
        Optional<MedicalDiagnosis> medicalDiagnosis = medicalDiagnosisService.findOne(id);
        return ResponseEntity.ok(medicalDiagnosis.get());
    }

    @DeleteMapping("/medical-diagnoses/{id}")
    public ResponseEntity<Void> deleteMedicalDiagnosis(@PathVariable Long id) {
        log.debug("REST request to delete MedicalDiagnosis : {}", id);
        medicalDiagnosisService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
